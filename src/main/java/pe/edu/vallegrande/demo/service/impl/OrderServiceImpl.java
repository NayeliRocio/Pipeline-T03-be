package pe.edu.vallegrande.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import pe.edu.vallegrande.demo.model.Customer;
import pe.edu.vallegrande.demo.model.Order;
import pe.edu.vallegrande.demo.model.OrderDetail;
import pe.edu.vallegrande.demo.model.Product;
import pe.edu.vallegrande.demo.repository.OrderRepository;
import pe.edu.vallegrande.demo.repository.OrderDetailRepository;
import pe.edu.vallegrande.demo.repository.ProductRepository;
import pe.edu.vallegrande.demo.service.OrderService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * ✅ OrderServiceImpl con TRANSACCIONES REACTIVAS
 * 
 * ARQUITECTURA:
 * - Mantiene programación reactiva con Mono/Flux
 * - Usa TransactionalOperator para transacciones REALES
 * - Rollback automático en MongoDB en caso de error
 * - Compatible con WebFlux
 * - NO usa JPA ni EntityManager
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    // 🔥 INYECCIONES NECESARIAS PARA TRANSACCIONES
    private final OrderRepository orderRepo;
    private final OrderDetailRepository detailRepo;
    private final ProductRepository productRepo;
    private final pe.edu.vallegrande.demo.repository.CustomerRepository customerRepository;
    private final TransactionalOperator transactionalOperator; // ✅ PARA TRANSACCIONES

    @Override
    public Flux<Order> getAll() {
        return orderRepo.findAll().flatMap(this::loadRelations);
    }

    @Override
    public Flux<Order> getActive() {
        return orderRepo.findByIsActiveTrue().flatMap(this::loadRelations);
    }

    @Override
    public Flux<Order> getInactive() {
        return orderRepo.findByIsActiveFalse().flatMap(this::loadRelations);
    }

    @Override
    public Mono<Order> getById(String id) {
        return orderRepo.findById(id).flatMap(this::loadRelations);
    }

    // --- MÉTODO PARA CARGAR RELACIONES ---
    private Mono<Order> loadRelations(Order order) {
        // Cargar customer si existe
        Mono<Customer> customerMono = Mono.justOrEmpty(order.getCustomerId())
                .flatMap(customerRepository::findById)
                .switchIfEmpty(Mono.empty());
        
        // Cargar detalles con productos
        Mono<java.util.List<OrderDetail>> detailsMono = detailRepo.findByOrderId(order.getId())
                .flatMap(detail -> {
                    if (detail.getProductId() != null) {
                        return productRepo.findById(detail.getProductId())
                                .map(product -> {
                                    detail.setProduct(product);
                                    return detail;
                                })
                                .defaultIfEmpty(detail);
                    }
                    return Mono.just(detail);
                })
                .collectList();
        
        return Mono.zip(
                customerMono.defaultIfEmpty(new Customer()),
                detailsMono,
                (customer, details) -> {
                    // Solo asignar customer si tiene ID (no es el objeto vacío por defecto)
                    if (customer.getId() != null) {
                        order.setCustomer(customer);
                    }
                    order.setDetails(details);
                    return order;
                }
        );
    }

    /**
     * ✅ CREATE CON TRANSACCIÓN REACTIVA
     * 
     * FLUJO:
     * 1. Valida stock de cada producto
     * 2. Crea el pedido
     * 3. Descuenta stock de cada producto
     * 4. Guarda detalles del pedido
     * 5. Calcula total
     * 6. Si error → ROLLBACK automático en toda la transacción
     * 
     * @param order pedido con detalles
     * @return Order guardado (o error con rollback)
     */
    @Override
    public Mono<Order> create(Order order) {
        // ✅ GENERAR ID MONGODB - SI VIENE NULL, GENERAMOS UNO
        if (order.getId() == null || order.getId().isEmpty()) {
            order.setId(new org.bson.types.ObjectId().toString());
        }
        
        // CONFIGURAR VALORES INICIALES
        LocalDateTime now = LocalDateTime.now();
        order.setCreatedAt(now);
        order.setIsActive(true);
        if (order.getDetails() == null || order.getDetails().isEmpty()) {
            order.setTotalAmount(BigDecimal.ZERO);
        }

        // 🔥 FLUJO TRANSACCIONAL COMPLETO
        Mono<Order> transactionalFlow = Mono.just(order)
                .flatMap(o -> {
                    // 1️⃣ VALIDAR STOCK PRIMERO
                    if (o.getDetails() != null && !o.getDetails().isEmpty()) {
                        return validateStock(o.getDetails())
                                .then(Mono.just(o));
                    }
                    return Mono.just(o);
                })
                .flatMap(orderRepo::save) // 2️⃣ GUARDAR ORDEN
                .flatMap(savedOrder -> {
                    if (savedOrder.getDetails() == null || savedOrder.getDetails().isEmpty()) {
                        return Mono.just(savedOrder);
                    }
                    
                    // 3️⃣ DESCONTAR STOCK Y GUARDAR DETALLES
                    return Flux.fromIterable(savedOrder.getDetails())
                            .flatMap(detail -> {
                                // ✅ GENERAR ID PARA ORDERDETAIL SI NO TIENE
                                if (detail.getId() == null || detail.getId().isEmpty()) {
                                    detail.setId(new org.bson.types.ObjectId().toString());
                                }
                                return decreaseProductStock(detail.getProductId(), detail.getQuantity())
                                    .flatMap(product -> {
                                        detail.setOrderId(savedOrder.getId());
                                        detail.setCreatedAt(now);
                                        detail.setIsActive(true);
                                        return detailRepo.save(detail);
                                    });
                            })
                            .reduce(BigDecimal.ZERO, (total, d) ->
                                    total.add(d.getPriceAtPurchase()
                                            .multiply(BigDecimal.valueOf(d.getQuantity())))
                            )
                            .flatMap(total -> {
                                savedOrder.setTotalAmount(total);
                                return orderRepo.save(savedOrder);
                            })
                            .flatMap(this::loadRelations); // Cargar relaciones antes de devolver
                });

        // VOLVER TODO EN TRANSACCIÓN - ROLLBACK AUTOMÁTICO EN ERROR
        return transactionalOperator.transactional(transactionalFlow);
    }

    /**
     * VALIDAR STOCK
     * Si algún producto NO tiene stock suficiente → rechaza con error
     * 
     * @param details detalles del pedido
     * @return Mono<List> si OK, o error si no hay stock
     */
    private Mono<Void> validateStock(java.util.List<OrderDetail> details) {
        return Flux.fromIterable(details)
                .flatMap(detail -> {
                    //  VALIDAR QUE PRODUCT_ID NO SEA NULL
                    if (detail.getProductId() == null || detail.getProductId().isEmpty()) {
                        return Mono.error(new RuntimeException(
                                "❌ El productId es requerido en los detalles del pedido"));
                    }
                    
                    // VALIDAR QUE QUANTITY NO SEA NULL
                    if (detail.getQuantity() == null || detail.getQuantity() <= 0) {
                        return Mono.error(new RuntimeException(
                                "❌ La cantidad debe ser mayor a 0"));
                    }
                    
                    return productRepo.findById(detail.getProductId())
                            .switchIfEmpty(Mono.error(new RuntimeException(
                                    "❌ Producto NO encontrado: " + detail.getProductId())))
                            .flatMap(product -> {
                                if (product.getStock() < detail.getQuantity()) {
                                    return Mono.error(new RuntimeException(
                                            "❌ Stock insuficiente para: " + product.getName() +
                                                    ". Disponible: " + product.getStock() +
                                                    ", Solicitado: " + detail.getQuantity()));
                                }
                                return Mono.just(product);
                            });
                })
                .then();
    }

    /**
     * ✅ DESCONTAR STOCK
     * Reduce el stock del producto en la cantidad solicitada
     * 
     * @param productId ID del producto
     * @param quantity cantidad a descontar
     * @return Producto actualizado
     */
    private Mono<Product> decreaseProductStock(String productId, Integer quantity) {
        return productRepo.findById(productId)
                .flatMap(product -> {
                    // 🔥 DESCUENTA EL STOCK
                    product.setStock(product.getStock() - quantity);
                    product.setUpdatedAt(LocalDateTime.now());
                    
                    // 🔥 GUARDAR CAMBIO (Si falla aquí, se hace rollback de TODO)
                    return productRepo.save(product);
                });
    }

    @Override
    public Mono<Order> update(String id, Order order) {
        return orderRepo.findById(id)
                .flatMap(existing -> {

                    existing.setCustomerId(order.getCustomerId());
                    existing.setUpdatedAt(LocalDateTime.now());

                    return detailRepo.findByOrderId(existing.getId())
                            .flatMap(oldDetail -> detailRepo.delete(oldDetail))
                            .thenMany(Flux.fromIterable(order.getDetails()))
                            .flatMap(detail -> {
                                detail.setOrderId(existing.getId());
                                detail.setCreatedAt(LocalDateTime.now());
                                detail.setIsActive(true);
                                return detailRepo.save(detail);
                            })
                            .collectList()
                            .flatMap(newDetails -> {

                                BigDecimal total = newDetails.stream()
                                        .map(d -> d.getPriceAtPurchase()
                                                .multiply(BigDecimal.valueOf(d.getQuantity())))
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                                existing.setTotalAmount(total);
                                existing.setDetails(newDetails);

                                return orderRepo.save(existing);
                            })
                            .flatMap(this::loadRelations);
                });
    }

    @Override
    public Mono<Void> delete(String id) {
        return orderRepo.findById(id)
                .flatMap(order -> {
                    order.setIsActive(false);
                    order.setDeletedAt(LocalDateTime.now());
                    order.setUpdatedAt(LocalDateTime.now());
                    return orderRepo.save(order);
                }).then();
    }

    @Override
    public Mono<Void> restore(String id) {
        return orderRepo.findById(id)
                .flatMap(order -> {
                    order.setIsActive(true);
                    order.setRestoredAt(LocalDateTime.now());
                    order.setDeletedAt(null);
                    order.setUpdatedAt(LocalDateTime.now());
                    return orderRepo.save(order);
                }).then();
    }
}

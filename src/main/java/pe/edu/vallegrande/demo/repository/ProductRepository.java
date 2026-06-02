package pe.edu.vallegrande.demo.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.edu.vallegrande.demo.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
    
    @Query("{ 'isAvailable': true }")
    Flux<Product> findByIsAvailableTrue();

    @Query("{ 'isAvailable': false }")
    Flux<Product> findByIsAvailableFalse();

    // 🔥 MÉTODOS NUEVOS PARA TRANSACCIONES DE STOCK
    
    /**
     * ✅ Buscar producto por ID (para validar stock)
     */
    Mono<Product> findById(String id);

    /**
     * ✅ Consultar stock de un producto
     * @param id ID del producto
     * @return cantidad en stock
     */
    @Query("{ '_id': ?0 }")
    Mono<Product> findProductForStockCheck(String id);
}



package pe.edu.vallegrande.demo.service.impl;

import org.springframework.stereotype.Service;
import pe.edu.vallegrande.demo.model.Customer;
import pe.edu.vallegrande.demo.repository.CustomerRepository;
import pe.edu.vallegrande.demo.service.CustomerService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repo;

    public CustomerServiceImpl(CustomerRepository repo) {
        this.repo = repo;
    }

    // ================= LISTAR =================

    @Override
    public Flux<Customer> listarTodos() {
        return repo.findAll();
    }

    @Override
    public Flux<Customer> listarActivos() {
        return repo.findByIsActiveTrue();
    }

    @Override
    public Flux<Customer> listarInactivos() {
        return repo.findByIsActiveFalse();
    }

    @Override
    public Mono<Customer> buscarPorId(String id) {
        return repo.findById(id);
    }

    // ================= CREAR =================

    @Override
    public Mono<Customer> crear(Customer customer) {
        // ✅ GENERAR ID MONGODB SI NO VIENE
        if (customer.getId() == null || customer.getId().isEmpty()) {
            customer.setId(new org.bson.types.ObjectId().toString());
        }
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(null); // ✅ correcto
        customer.setIsActive(true);
        customer.setDeletedAt(null);
        customer.setRestoredAt(null);
        return repo.save(customer);
    }

    // ================= EDITAR =================

    @Override
    public Mono<Customer> editar(String id, Customer customer) {
        return repo.findById(id)
                .flatMap(existente -> {
                    existente.setFirstName(customer.getFirstName());
                    existente.setLastName(customer.getLastName());
                    existente.setPhone(customer.getPhone());
                    existente.setEmail(customer.getEmail());
                    existente.setPreferences(customer.getPreferences());
                    existente.setClientType(customer.getClientType());
                    existente.setBalance(customer.getBalance());
                    existente.setLoyaltyPoints(customer.getLoyaltyPoints());
                    existente.setBirthDate(customer.getBirthDate());

                    // 🔥 auditoría correcta
                    existente.setUpdatedAt(LocalDateTime.now());

                    return repo.save(existente);
                });
    }

    // ================= ELIMINAR (LÓGICO) =================

    @Override
    public Mono<Void> eliminar(String id) {
        return repo.findById(id)
                .flatMap(customer -> {
                    customer.setIsActive(false);
                    customer.setDeletedAt(LocalDateTime.now());
                    customer.setRestoredAt(null); // 🔥 FIX IMPORTANTE
                    customer.setUpdatedAt(LocalDateTime.now());
                    return repo.save(customer);
                })
                .then();
    }

    // ================= RESTAURAR =================

    @Override
    public Mono<Void> restaurar(String id) {
        return repo.findById(id)
                .flatMap(customer -> {
                    customer.setIsActive(true);
                    customer.setRestoredAt(LocalDateTime.now());
                    customer.setDeletedAt(null); // 🔥 FIX IMPORTANTE
                    customer.setUpdatedAt(LocalDateTime.now());
                    return repo.save(customer);
                })
                .then();
    }
}

package pe.edu.vallegrande.demo.repository;

import pe.edu.vallegrande.demo.model.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Query;
import reactor.core.publisher.Flux;

public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {

    @Query("{ 'isActive': true }")
    Flux<Customer> findByIsActiveTrue();

    @Query("{ 'isActive': false }")
    Flux<Customer> findByIsActiveFalse();
}

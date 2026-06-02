package pe.edu.vallegrande.demo.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.edu.vallegrande.demo.model.Order;
import reactor.core.publisher.Flux;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {

    @Query("{ 'isActive': true }")
    Flux<Order> findByIsActiveTrue();

    @Query("{ 'isActive': false }")
    Flux<Order> findByIsActiveFalse();
}
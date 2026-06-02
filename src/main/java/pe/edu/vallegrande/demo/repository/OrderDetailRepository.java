package pe.edu.vallegrande.demo.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.edu.vallegrande.demo.model.OrderDetail;
import reactor.core.publisher.Flux;

public interface OrderDetailRepository extends ReactiveMongoRepository<OrderDetail, String> {

    @Query("{ 'isActive': true }")
    Flux<OrderDetail> findByIsActiveTrue();

    @Query("{ 'isActive': false }")
    Flux<OrderDetail> findByIsActiveFalse();

    @Query("{ 'order': ?0 }")
    Flux<OrderDetail> findByOrderId(String orderId);
}
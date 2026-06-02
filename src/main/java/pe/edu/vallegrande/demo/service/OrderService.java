package pe.edu.vallegrande.demo.service;

import pe.edu.vallegrande.demo.model.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {

    Flux<Order> getAll();
    Flux<Order> getActive();
    Flux<Order> getInactive();

    Mono<Order> getById(String id);
    Mono<Order> create(Order order);
    Mono<Order> update(String id, Order order);

    Mono<Void> delete(String id);
    Mono<Void> restore(String id);
}
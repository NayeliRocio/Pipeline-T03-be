package pe.edu.vallegrande.demo.service;

import pe.edu.vallegrande.demo.model.OrderDetail;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderDetailService {

    Flux<OrderDetail> getAll();
    Flux<OrderDetail> getActive();
    Flux<OrderDetail> getInactive();

    Mono<OrderDetail> getById(String id);
    Mono<OrderDetail> create(OrderDetail detail);
    Mono<OrderDetail> update(String id, OrderDetail detail);

    Mono<Void> delete(String id);
    Mono<Void> restore(String id);
}
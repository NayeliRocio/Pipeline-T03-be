package pe.edu.vallegrande.demo.service;

import pe.edu.vallegrande.demo.model.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Flux<Customer> listarTodos();
    Flux<Customer> listarActivos();
    Flux<Customer> listarInactivos();
    Mono<Customer> buscarPorId(String id);
    Mono<Customer> crear(Customer customer);
    Mono<Customer> editar(String id, Customer customer);
    Mono<Void> eliminar(String id);
    Mono<Void> restaurar(String id);
}


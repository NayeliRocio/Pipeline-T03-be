package pe.edu.vallegrande.demo.service;

import pe.edu.vallegrande.demo.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {
    Flux<Product> listarTodos();
    Flux<Product> listarActivos();
    Flux<Product> listarInactivos();
    Mono<Product> buscarPorId(String id);
    Mono<Product> crear(Product product);
    Mono<Product> editar(String id, Product product);
    Mono<Void> eliminar(String id);
    Mono<Void> restaurar(String id);
}


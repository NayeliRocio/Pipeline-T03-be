package pe.edu.vallegrande.demo.rest;

import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.demo.model.Product;
import pe.edu.vallegrande.demo.service.ProductService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
public class ProductRest {

    private final ProductService service;

    public ProductRest(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<Product> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/activos")
    public Flux<Product> listarActivos() {
        return service.listarActivos();
    }

    @GetMapping("/inactivos")
    public Flux<Product> listarInactivos() {
        return service.listarInactivos();
    }

    @GetMapping("/{id}")
    public Mono<Product> buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Mono<Product> crear(@RequestBody Product product) {
        return service.crear(product);
    }

    @PutMapping("/{id}")
    public Mono<Product> editar(@PathVariable String id, @RequestBody Product product) {
        return service.editar(id, product);
    }

    @PatchMapping("/{id}/eliminar")
    public Mono<Void> eliminar(@PathVariable String id) {
        return service.eliminar(id);
    }

    @PatchMapping("/{id}/restaurar")
    public Mono<Void> restaurar(@PathVariable String id) {
        return service.restaurar(id);
    }
}
 
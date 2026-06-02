package pe.edu.vallegrande.demo.rest;

import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.demo.model.Order;
import pe.edu.vallegrande.demo.service.OrderService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
public class OrderRest {

    private final OrderService service;

    // 🔥 CONSTRUCTOR MANUAL (SOLUCIÓN)
    public OrderRest(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<Order> getAll() {
        return service.getAll();
    }
    @GetMapping("/activos")
    public Flux<Order> getActive() {
        return service.getActive();
    }

    @GetMapping("/inactivos")
    public Flux<Order> getInactive() {
        return service.getInactive();
    }

    @GetMapping("/{id}")
    public Mono<Order> getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public Mono<Order> create(@RequestBody Order order) {
        return service.create(order);
    }

    @PutMapping("/{id}")
    public Mono<Order> update(@PathVariable String id, @RequestBody Order order) {
        return service.update(id, order);
    }

    @PatchMapping("/{id}/eliminar")
    public Mono<Void> delete(@PathVariable String id) {
        return service.delete(id);
    }

    @PatchMapping("/{id}/restaurar")
    public Mono<Void> restore(@PathVariable String id) {
        return service.restore(id);
    }
}
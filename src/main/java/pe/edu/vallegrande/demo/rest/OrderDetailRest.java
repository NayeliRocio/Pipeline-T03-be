package pe.edu.vallegrande.demo.rest;

import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.demo.model.OrderDetail;
import pe.edu.vallegrande.demo.service.OrderDetailService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/order-details")
public class OrderDetailRest {

    private final OrderDetailService service;

    // 🔥 CONSTRUCTOR MANUAL
    public OrderDetailRest(OrderDetailService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<OrderDetail> getAll() {
        return service.getAll();
    }

    @GetMapping("/activos")
    public Flux<OrderDetail> getActive() {
        return service.getActive();
    }

    @GetMapping("/inactivos")
    public Flux<OrderDetail> getInactive() {
        return service.getInactive();
    }

    @GetMapping("/{id}")
    public Mono<OrderDetail> getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public Mono<OrderDetail> create(@RequestBody OrderDetail detail) {
        return service.create(detail);
    }

    @PutMapping("/{id}")
    public Mono<OrderDetail> update(@PathVariable String id, @RequestBody OrderDetail detail) {
        return service.update(id, detail);
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
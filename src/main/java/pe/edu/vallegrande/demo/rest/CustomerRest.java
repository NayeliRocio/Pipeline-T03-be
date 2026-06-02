package pe.edu.vallegrande.demo.rest;

import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.demo.model.Customer;
import pe.edu.vallegrande.demo.service.CustomerService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/customers")
public class CustomerRest {

    private final CustomerService service;

    public CustomerRest(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<Customer> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/activos")
    public Flux<Customer> listarActivos() {
        return service.listarActivos();
    }

    @GetMapping("/inactivos")
    public Flux<Customer> listarInactivos() {
        return service.listarInactivos();
    }

    @GetMapping("/{id}")
    public Mono<Customer> buscarPorId(@PathVariable String id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Mono<Customer> crear(@RequestBody Customer customer) {
        return service.crear(customer);
    }

    @PutMapping("/{id}")
    public Mono<Customer> editar(@PathVariable String id, @RequestBody Customer customer) {
        return service.editar(id, customer);
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


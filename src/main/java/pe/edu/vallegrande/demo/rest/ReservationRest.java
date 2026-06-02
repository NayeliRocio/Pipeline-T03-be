package pe.edu.vallegrande.demo.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.demo.model.Reservation;
import pe.edu.vallegrande.demo.service.ReservationService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationRest {

    private final ReservationService service;

    @GetMapping
    public Flux<Reservation> getAll() {
        return service.getAll();
    }

    @GetMapping("/activos")
    public Flux<Reservation> getActive() {
        return service.getActive();
    }

    @GetMapping("/inactivos")
    public Flux<Reservation> getInactive() {
        return service.getInactive();
    }

    @GetMapping("/{id}")
    public Mono<Reservation> getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public Mono<Reservation> create(@RequestBody Reservation reservation) {
        return service.create(reservation);
    }

    @PutMapping("/{id}")
    public Mono<Reservation> update(@PathVariable String id, @RequestBody Reservation reservation) {
        return service.update(id, reservation);
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


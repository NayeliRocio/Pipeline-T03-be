package pe.edu.vallegrande.demo.service;

import pe.edu.vallegrande.demo.model.Reservation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReservationService {

    Flux<Reservation> getAll();
    Flux<Reservation> getActive();
    Flux<Reservation> getInactive();
    Mono<Reservation> getById(String id);
    Mono<Reservation> create(Reservation reservation);
    Mono<Reservation> update(String id, Reservation reservation);
    Mono<Void> delete(String id); 
    Mono<Void> restore(String id);
}


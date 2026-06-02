package pe.edu.vallegrande.demo.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.demo.model.Reservation;
import reactor.core.publisher.Flux;

@Repository
public interface ReservationRepository extends ReactiveMongoRepository<Reservation, String> {

    @Query("{ 'isActive': true }")
    Flux<Reservation> findByIsActiveTrue();

    @Query("{ 'isActive': false }")
    Flux<Reservation> findByIsActiveFalse();
}


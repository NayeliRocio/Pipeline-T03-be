package pe.edu.vallegrande.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import pe.edu.vallegrande.demo.model.Customer;
import pe.edu.vallegrande.demo.model.Reservation;
import pe.edu.vallegrande.demo.model.TableSpot;
import pe.edu.vallegrande.demo.repository.ReservationRepository;
import pe.edu.vallegrande.demo.repository.CustomerRepository; // Asegúrate que existan
import pe.edu.vallegrande.demo.repository.TableSpotRepository; // Asegúrate que existan
import pe.edu.vallegrande.demo.service.ReservationService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository repository;
    private final CustomerRepository customerRepository;
    private final TableSpotRepository tableRepository;

    @Override
    public Flux<Reservation> getAll() {
        return repository.findAll().flatMap(this::loadRelations);
    }

    @Override
    public Flux<Reservation> getActive() {
        return repository.findByIsActiveTrue().flatMap(this::loadRelations);
    }

    @Override
    public Flux<Reservation> getInactive() {
        return repository.findByIsActiveFalse().flatMap(this::loadRelations);
    }

    @Override
    public Mono<Reservation> getById(String id) {
        return repository.findById(id).flatMap(this::loadRelations);
    }

    // --- MÉTODO MÁGICO PARA CARGAR LOS DATOS ---
    private Mono<Reservation> loadRelations(Reservation reservation) {

        Mono<Customer> customerMono = Mono.empty();
        Mono<TableSpot> tableMono = Mono.empty();

        // 🔥 CUSTOMER
        if (reservation.getCustomerId() != null) {
            customerMono = customerRepository.findById(reservation.getCustomerId());
        }

        // 🔥 TABLE
        if (reservation.getTableId() != null) {
            tableMono = tableRepository.findById(reservation.getTableId());
        }

        return customerMono.defaultIfEmpty(new Customer())
                .zipWith(tableMono.defaultIfEmpty(new TableSpot()))
                .map(tuple -> {
                    reservation.setCustomer(tuple.getT1());
                    reservation.setTableSpot(tuple.getT2());
                    return reservation;
                });
    }

    @Override
    public Mono<Reservation> create(Reservation reservation) {

        LocalDateTime now = LocalDateTime.now();

        reservation.setIsActive(true);
        reservation.setCreatedAt(now);
        reservation.setUpdatedAt(null);
        reservation.setDeletedAt(null);
        reservation.setRestoredAt(null);

        return repository.save(reservation)
                .flatMap(this::loadRelations); // 🔥 AUTOMÁTICO DEVUELVE CON RELACIONES
    }

    @Override
    public Mono<Reservation> update(String id, Reservation reservation) {
        return repository.findById(id)
                .flatMap(existing -> {

                    existing.setReservationDate(reservation.getReservationDate());
                    existing.setReservationTime(reservation.getReservationTime());
                    existing.setGuestsCount(reservation.getGuestsCount());
                    existing.setStatus(reservation.getStatus());

                    // 🔥 GUARDAS IDS, NO OBJETOS
                    existing.setCustomerId(reservation.getCustomerId());
                    existing.setTableId(reservation.getTableId());

                    existing.setUpdatedAt(LocalDateTime.now());

                    return repository.save(existing);
                }).flatMap(this::loadRelations);
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.findById(id)
                .flatMap(reservation -> {
                    reservation.setIsActive(false);
                    reservation.setUpdatedAt(LocalDateTime.now());
                    return repository.save(reservation);
                }).then();
    }

    @Override
    public Mono<Void> restore(String id) {
        return repository.findById(id)
                .flatMap(reservation -> {
                    reservation.setIsActive(true);
                    reservation.setUpdatedAt(LocalDateTime.now());
                    return repository.save(reservation);
                }).then();
    }
}
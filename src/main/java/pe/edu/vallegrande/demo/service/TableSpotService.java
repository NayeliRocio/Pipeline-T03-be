package pe.edu.vallegrande.demo.service;

import pe.edu.vallegrande.demo.model.TableSpot;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TableSpotService {

    Flux<TableSpot> getAll();
    Mono<TableSpot> getById(String id);

    Mono<TableSpot> create(TableSpot tableSpot);
    Mono<TableSpot> update(String id, TableSpot tableSpot);

    Mono<Void> delete(String id); // lógico
    Mono<Void> restore(String id);
}
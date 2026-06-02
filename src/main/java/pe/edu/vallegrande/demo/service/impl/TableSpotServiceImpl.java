package pe.edu.vallegrande.demo.service.impl;

import org.springframework.stereotype.Service;
import pe.edu.vallegrande.demo.model.TableSpot;
import pe.edu.vallegrande.demo.repository.TableSpotRepository;
import pe.edu.vallegrande.demo.service.TableSpotService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class TableSpotServiceImpl implements TableSpotService {

    private final TableSpotRepository repository;

    public TableSpotServiceImpl(TableSpotRepository repository) {
        this.repository = repository;
    }

    @Override
    public Flux<TableSpot> getAll() {
        return repository.findAll();
    }

    @Override
    public Mono<TableSpot> getById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<TableSpot> create(TableSpot tableSpot) {
        tableSpot.setCreatedAt(LocalDateTime.now());
        tableSpot.setIsAvailable(true); // ✅ único estado
        return repository.save(tableSpot);
    }

    @Override
    public Mono<TableSpot> update(String id, TableSpot tableSpot) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setTableNumber(tableSpot.getTableNumber());
                    existing.setUpdatedAt(LocalDateTime.now());
                    return repository.save(existing);
                });
    }
    @Override
    public Mono<Void> delete(String id) {
        return repository.findById(id)
                .flatMap(table -> {
                    table.setIsAvailable(false); // 🔥 eliminación lógica
                    table.setDeletedAt(LocalDateTime.now());
                    return repository.save(table);
                })
                .then();
    }

    @Override
    public Mono<Void> restore(String id) {
        return repository.findById(id)
                .flatMap(table -> {
                    table.setIsAvailable(true); // 🔥 restaurar
                    table.setRestoredAt(LocalDateTime.now());
                    return repository.save(table);
                })
                .then();
    }

}
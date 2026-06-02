package pe.edu.vallegrande.demo.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.demo.model.TableSpot;
import pe.edu.vallegrande.demo.service.TableSpotService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tables")
public class TableSpotRest {

    private final TableSpotService service;

    // 🔥 SOLUCIÓN
    public TableSpotRest(TableSpotService service) {
        this.service = service;
    }
    @GetMapping
    public Flux<TableSpot> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Mono<TableSpot> getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public Mono<TableSpot> create(@RequestBody TableSpot tableSpot) {
        return service.create(tableSpot);
    }

    @PutMapping("/{id}")
    public Mono<TableSpot> update(@PathVariable String id, @RequestBody TableSpot tableSpot) {
        return service.update(id, tableSpot);
    }

    // 🔥 ELIMINAR LÓGICO
    @PatchMapping("/{id}/eliminar")
    public Mono<Void> delete(@PathVariable String id) {
        return service.delete(id);
    }

    // 🔥 RESTAURAR
    @PatchMapping("/{id}/restaurar")
    public Mono<Void> restore(@PathVariable String id) {
        return service.restore(id);
    }
}
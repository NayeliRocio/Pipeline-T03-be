package pe.edu.vallegrande.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.demo.model.OrderDetail;
import pe.edu.vallegrande.demo.repository.OrderDetailRepository;
import pe.edu.vallegrande.demo.service.OrderDetailService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository repo;

    @Override
    public Flux<OrderDetail> getAll() {
        return repo.findAll();
    }

    @Override
    public Flux<OrderDetail> getActive() {
        return repo.findByIsActiveTrue();
    }

    @Override
    public Flux<OrderDetail> getInactive() {
        return repo.findByIsActiveFalse();
    }

    @Override
    public Mono<OrderDetail> getById(String id) {
        return repo.findById(id);
    }

    @Override
    public Mono<OrderDetail> create(OrderDetail detail) {
        detail.setCreatedAt(LocalDateTime.now());
        detail.setIsActive(true);
        return repo.save(detail);
    }

    @Override
    public Mono<OrderDetail> update(String id, OrderDetail detail) {
        return repo.findById(id)
                .flatMap(existing -> {
                    existing.setQuantity(detail.getQuantity());
                    existing.setPriceAtPurchase(detail.getPriceAtPurchase());
                    existing.setProductId(detail.getProductId());
                    existing.setUpdatedAt(LocalDateTime.now());
                    return repo.save(existing);
                });
    }

    @Override
    public Mono<Void> delete(String id) {
        return repo.findById(id)
                .flatMap(d -> {
                    d.setIsActive(false);
                    d.setDeletedAt(LocalDateTime.now());
                    d.setUpdatedAt(LocalDateTime.now());
                    return repo.save(d);
                }).then();
    }

    @Override
    public Mono<Void> restore(String id) {
        return repo.findById(id)
                .flatMap(d -> {
                    d.setIsActive(true);
                    d.setRestoredAt(LocalDateTime.now());
                    d.setDeletedAt(null);
                    d.setUpdatedAt(LocalDateTime.now());
                    return repo.save(d);
                }).then();
    }
}
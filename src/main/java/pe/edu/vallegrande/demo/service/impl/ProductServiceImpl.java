package pe.edu.vallegrande.demo.service.impl;

import org.springframework.stereotype.Service;
import pe.edu.vallegrande.demo.model.Product;
import pe.edu.vallegrande.demo.repository.ProductRepository;
import pe.edu.vallegrande.demo.service.ProductService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    public ProductServiceImpl(ProductRepository repo) {
        this.repo = repo;
    }

    @Override
    public Flux<Product> listarTodos() {
        return repo.findAll();
    }

    @Override
    public Flux<Product> listarActivos() {
        return repo.findByIsAvailableTrue();
    }

    @Override
    public Flux<Product> listarInactivos() {
        return repo.findByIsAvailableFalse();
    }

    @Override
    public Mono<Product> buscarPorId(String id) {
        return repo.findById(id);
    }

    @Override
    public Mono<Product> crear(Product product) {
        product.setIsAvailable(true);
        product.setIsFeatured(false);
        product.setCreatedAt(LocalDateTime.now());
        return repo.save(product);
    }

    @Override
    public Mono<Product> editar(String id, Product product) {
        return repo.findById(id)
                .flatMap(existente -> {
                    existente.setName(product.getName());
                    existente.setDescription(product.getDescription());
                    existente.setPrice(product.getPrice());
                    existente.setCategory(product.getCategory());
                    existente.setIsAvailable(product.getIsAvailable());
                    existente.setImageUrl(product.getImageUrl());
                    existente.setPrepTime(product.getPrepTime());
                    existente.setIsFeatured(product.getIsFeatured());
                    existente.setNutritionalInfo(product.getNutritionalInfo());
                    existente.setUpdatedAt(LocalDateTime.now());
                    return repo.save(existente);
                });
    }

    @Override
    public Mono<Void> eliminar(String id) {
        return repo.findById(id)
                .flatMap(product -> {
                    product.setIsAvailable(false);
                    product.setDeletedAt(LocalDateTime.now());
                    product.setUpdatedAt(LocalDateTime.now());
                    return repo.save(product);
                })
                .then();
    }

    @Override
    public Mono<Void> restaurar(String id) {
        return repo.findById(id)
                .flatMap(product -> {
                    product.setIsAvailable(true);
                    product.setRestoredAt(LocalDateTime.now());
                    product.setDeletedAt(null);
                    product.setUpdatedAt(LocalDateTime.now());
                    return repo.save(product);
                })
                .then();
    }
}

package pe.edu.vallegrande.demo.service;

import pe.edu.vallegrande.demo.model.AdminUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AdminUserService {
    Mono<AdminUser> login(String username, String password);
    Flux<AdminUser> getAllUsers();
}


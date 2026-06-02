package pe.edu.vallegrande.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.demo.model.AdminUser;
import pe.edu.vallegrande.demo.repository.AdminUserRepository;
import pe.edu.vallegrande.demo.service.AdminUserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserRepository repository;

    @Override
    public Mono<AdminUser> login(String username, String password) {
        return repository.findByUsernameAndPassword(username, password);
    }

    @Override
    public Flux<AdminUser> getAllUsers() {
        return repository.findAll();
    }
}


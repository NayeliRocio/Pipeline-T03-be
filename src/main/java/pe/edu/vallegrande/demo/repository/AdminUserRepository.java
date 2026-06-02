package pe.edu.vallegrande.demo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.demo.model.AdminUser;
import reactor.core.publisher.Mono;

@Repository
public interface AdminUserRepository extends ReactiveMongoRepository<AdminUser, String> {
    Mono<AdminUser> findByUsernameAndPassword(String username, String password);
    Mono<AdminUser> findByUsername(String username);
}


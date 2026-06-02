package pe.edu.vallegrande.demo.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.demo.model.AdminUser;
import pe.edu.vallegrande.demo.service.AdminUserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminUserRest {

    private final AdminUserService service;

    @PostMapping("/login")
    public Mono<AdminUser> login(@RequestBody AdminUser user) {
        return service.login(user.getUsername(), user.getPassword());
    }

    @GetMapping("/users")
    public Flux<AdminUser> getAllUsers() {
        return service.getAllUsers();
    }
}


package pe.edu.vallegrande.demo.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.demo.dto.AuthResponse;
import pe.edu.vallegrande.demo.dto.LoginRequest;
import pe.edu.vallegrande.demo.repository.AdminUserRepository;
import pe.edu.vallegrande.demo.security.CryptoUtil;
import pe.edu.vallegrande.demo.security.JwtProvider;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthRest {

    private final AdminUserRepository adminUserRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final CryptoUtil cryptoUtil;

    public AuthRest(AdminUserRepository adminUserRepository, JwtProvider jwtProvider, PasswordEncoder passwordEncoder, CryptoUtil cryptoUtil) {
        this.adminUserRepository = adminUserRepository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.cryptoUtil = cryptoUtil;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody LoginRequest request) {
        // Encriptar el username recibido de la misma forma que está en la BD (determinístico)
        String encryptedUsername = cryptoUtil.encryptDeterministic(request.getUsername());

        return adminUserRepository.findByUsername(encryptedUsername)
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .map(user -> {
                    // El listener onAfterConvert ya desencripta el usuario y el rol, 
                    // así que user.getRole() y user.getUsername() ya están en texto plano.
                    String token = jwtProvider.generateToken(user.getUsername(), user.getRole());
                    return ResponseEntity.ok(new AuthResponse(token));
                })
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    // ENDPOINT TEMPORAL PARA REGISTRAR USUARIO Y PROBAR EL LOGIN
    @PostMapping("/register")
    public Mono<ResponseEntity<pe.edu.vallegrande.demo.model.AdminUser>> register(@RequestBody pe.edu.vallegrande.demo.model.AdminUser user) {
        return adminUserRepository.save(user)
                .map(savedUser -> ResponseEntity.ok(savedUser));
    }
}

package pe.edu.vallegrande.demo.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;

    public JwtAuthenticationManager(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        
        if (jwtProvider.validateToken(token)) {
            String username = jwtProvider.extractUsername(token);
            String role = jwtProvider.extractRole(token);
            
            // We assume token is valid and signature is verified.
            return Mono.just(new UsernamePasswordAuthenticationToken(
                    username, 
                    token, 
                    List.of(new SimpleGrantedAuthority(role))
            ));
        }
        
        return Mono.empty();
    }
}

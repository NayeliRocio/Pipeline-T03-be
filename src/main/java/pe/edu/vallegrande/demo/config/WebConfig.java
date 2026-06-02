package pe.edu.vallegrande.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class WebConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir orígenes específicos
config.setAllowedOrigins(Arrays.asList(
    "http://localhost:5173",      // Vite dev server
    "http://localhost:3000",      // Puerto alternativo
    "http://localhost:8080",      // Backend
    "http://localhost:8081",      // Backend
    "http://localhost:8082",
    "http://localhost:8083",     // Expo web dev server
    "http://127.0.0.1:5173",
    "http://127.0.0.1:3000",
    "http://127.0.0.1:8080",
    "http://127.0.0.1:8081",
    "http://127.0.0.1:8082"
));
        
        // Métodos HTTP permitidos
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // Headers permitidos
        config.setAllowedHeaders(Arrays.asList("*"));
        
        // Headers que el cliente puede leer
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        // Permitir credenciales
        config.setAllowCredentials(true);
        
        // Cache tiempo
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsWebFilter(source);
    }
}

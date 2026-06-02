package pe.edu.vallegrande.demo.config;

import org.bson.Document;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.event.ReactiveAfterConvertCallback;
import org.springframework.data.mongodb.core.mapping.event.ReactiveBeforeConvertCallback;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.vallegrande.demo.model.AdminUser;
import pe.edu.vallegrande.demo.security.CryptoUtil;
import reactor.core.publisher.Mono;

@Configuration
public class MongoListenerConfig {

    private final CryptoUtil cryptoUtil;
    private final PasswordEncoder passwordEncoder;

    public MongoListenerConfig(CryptoUtil cryptoUtil, PasswordEncoder passwordEncoder) {
        this.cryptoUtil = cryptoUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public ReactiveBeforeConvertCallback<AdminUser> onBeforeConvert() {
        return (entity, collection) -> {
            // Encrypt username deterministically
            if (entity.getUsername() != null && !entity.getUsername().startsWith("ENC-")) {
                entity.setUsername(cryptoUtil.encryptDeterministic(entity.getUsername()));
            }
            
            // Hash password if not already hashed
            if (entity.getPassword() != null && !entity.getPassword().startsWith("$2a$")) {
                entity.setPassword(passwordEncoder.encode(entity.getPassword()));
            }
            
            // Encrypt role deterministically
            if (entity.getRole() != null && !entity.getRole().startsWith("ENC-")) {
                entity.setRole(cryptoUtil.encryptDeterministic(entity.getRole()));
            }
            
            return Mono.just(entity);
        };
    }

    @Bean
    public ReactiveAfterConvertCallback<AdminUser> onAfterConvert() {
        return (entity, document, collection) -> {
            if (entity != null) {
                if (entity.getUsername() != null) {
                    try {
                        String decrypted = cryptoUtil.decryptDeterministic(entity.getUsername());
                        if (decrypted != null) {
                            entity.setUsername(decrypted);
                        }
                    } catch (Exception ignored) {}
                }
                
                if (entity.getRole() != null) {
                    try {
                        String decrypted = cryptoUtil.decryptDeterministic(entity.getRole());
                        if (decrypted != null) {
                            entity.setRole(decrypted);
                        }
                    } catch (Exception ignored) {}
                }
            }
            return Mono.just(entity);
        };
    }
}


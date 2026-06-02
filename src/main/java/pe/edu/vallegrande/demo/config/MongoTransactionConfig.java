package pe.edu.vallegrande.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

/**
 * ✅ Configuración de Transacciones Reactivas en MongoDB
 * 
 * IMPORTANTE: MongoDB requiere REPLICA SET para transacciones.
 * En desarrollo local con Docker, ya está configurado en docker-compose.yml
 * 
 * Alternativa para desarrollo sin replica set:
 * - Usar transacciones simuladas con rollback manual
 * - O configurar MongoDB con replica set
 */
@Configuration
public class MongoTransactionConfig {

    /**
     * ✅ ReactiveMongoTransactionManager bean
     * Gestiona transacciones en MongoDB de forma reactiva
     */
    @Bean
    public ReactiveMongoTransactionManager reactiveMongoTransactionManager(
            ReactiveMongoDatabaseFactory factory) {
        return new ReactiveMongoTransactionManager(factory);
    }

    /**
     * ✅ TransactionalOperator bean
     * Proporciona operaciones transaccionales en programación reactiva
     * 
     * Uso:
     * return TransactionalOperator.create(txManager)
     *        .transactional(mono);
     */
    @Bean
    public TransactionalOperator transactionalOperator(
            ReactiveTransactionManager txManager) {
        return TransactionalOperator.create(txManager);
    }
}

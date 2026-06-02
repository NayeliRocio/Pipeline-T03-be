package pe.edu.vallegrande.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("📦 API - Altavista Rooftop (WebFlux + MongoDB)")
                        .version("2.0.0")
                        .description("API REST Reactiva para la gestión de clientes y productos de Altavista Rooftop. Construida con Spring WebFlux y MongoDB.")
                        .contact(new Contact()
                                .name("Equipo Vallegrande")
                                .email("soporte@vallegrande.edu.pe")
                                .url("https://vallegrande.edu.pe"))
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}

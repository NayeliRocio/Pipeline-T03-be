package pe.edu.vallegrande.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Product - Representa los productos del sistema
 */
@Document(collection = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    // ============= Identificador =============
    @Id
    private String id;

    // ============= Información Básica =============
    private String name;

    private String description;

    private BigDecimal price;

    private String category;

    // ============= Estado y Disponibilidad =============
    @Field("isAvailable")
    @JsonProperty("is_available")
    private Boolean isAvailable;

    @Field("imageUrl")
    @JsonProperty("image_url")
    private String imageUrl;

    @Field("prepTime")
    @JsonProperty("prep_time")
    private String prepTime;

    @Field("isFeatured")
    @JsonProperty("is_featured")
    private Boolean isFeatured;

    @Field("nutritionalInfo")
    @JsonProperty("nutritional_info")
    private String nutritionalInfo;

    // ============= Stock (PARA TRANSACCIONES) =============
    @Field("stock")
    @JsonProperty("stock")
    private Integer stock = 0; // 🔥 Cantidad disponible en stock

    // ============= Auditoría =============
    @Field("createdAt")
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Field("updatedAt")
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @Field("deletedAt")
    @JsonProperty("deleted_at")
    private LocalDateTime deletedAt;

    @Field("restoredAt")
    @JsonProperty("restored_at")
    private LocalDateTime restoredAt;

}

package pe.edu.vallegrande.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "order_details")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetail {

    @Id
    private String id;

    // ============= Información del Detalle =============
    @JsonAlias({"quantity", "qty"})
    private Integer quantity;

    @Field("priceAtPurchase")
    @JsonProperty("price_at_purchase")
    @JsonAlias({"priceAtPurchase", "price_at_purchase"})
    private BigDecimal priceAtPurchase;

    // ============= Referencias (IDs) =============
    @Field(name = "order")
    @JsonProperty("order_id")
    @JsonAlias({"orderId", "order_id"})
    private String orderId;

    @Field(name = "product")
    @JsonProperty("product_id")
    @JsonAlias({"productId", "product_id"})
    private String productId;

    // ============= Objetos Relacionados =============
    @Transient
    private Product product;

    // ============= Auditoría =============
    @Field("isActive")
    @JsonProperty("is_active")
    private Boolean isActive;

    @Field("createdAt")
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Field("updatedAt")
    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @Field("deletedAt")
    @JsonProperty("deleted_at")
    private LocalDateTime deletedAt;

    @Field("restoredAt")
    @JsonProperty("restored_at")
    private LocalDateTime restoredAt;
}
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
import java.util.List;

@Document(collection = "orders")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order {

    @Id
    private String id;

    // ============= Información de la Orden =============
    @Field("orderDate")
    @JsonProperty("order_date")
    @JsonAlias({"orderDate", "order_date"})
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderDate;

    @Field("totalAmount")
    @JsonProperty("total_amount")
    @JsonAlias({"totalAmount", "total_amount"})
    private BigDecimal totalAmount;

    // ============= Referencias (IDs) =============
    @Field(name = "customer")
    @JsonProperty("customer_id")
    @JsonAlias({"customerId", "customer_id"})
    private String customerId;

    // ============= Objetos Relacionados =============
    @Transient
    private Customer customer;

    private List<OrderDetail> details;

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
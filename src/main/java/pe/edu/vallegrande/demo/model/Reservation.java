package pe.edu.vallegrande.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "reservations")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Reservation {

    @Id
    private String id;

    // ============= Información de la Reserva =============
    @Field("reservationDate")
    @JsonProperty("reservation_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reservationDate;

    @Field("reservationTime")
    @JsonProperty("reservation_time")
    private String reservationTime;

    @Field("guestsCount")
    @JsonProperty("guests_count")
    private Integer guestsCount;

    private String status;

    // ============= Referencias (IDs) - Solo se guardan, no se muestran en JSON =============
    @Field(name = "customer")
    @JsonProperty(value = "customer_id", access = JsonProperty.Access.WRITE_ONLY)
    private String customerId;

    @Field(name = "tableSpot")
    @JsonProperty(value = "table_id", access = JsonProperty.Access.WRITE_ONLY)
    private String tableId;

    // ============= Objetos Relacionados (solo para respuesta) =============
    @Transient
    private Customer customer;

    @Transient
    private TableSpot tableSpot;

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
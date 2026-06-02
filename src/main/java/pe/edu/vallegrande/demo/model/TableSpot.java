package pe.edu.vallegrande.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Document(collection = "table_spots")
@Data
public class TableSpot {

    @Id
    private String id;

    // ============= Información de la Mesa =============
    @Field("tableNumber")
    @JsonProperty("table_number")
    private Integer tableNumber;

    private String location;

    private Integer capacity;

    private String notes;

    private String status;

    // ============= Estado =============
    @Field("isAvailable")
    @JsonProperty("is_available")
    private Boolean isAvailable;

    @Field("lastClean")
    @JsonProperty("last_clean")
    private LocalDate lastClean;

    @Field("cleaningTime")
    @JsonProperty("cleaning_time")
    private String cleaningTime;

    @Field("layoutDetails")
    @JsonProperty("layout_details")
    private String layoutDetails;

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
package pe.edu.vallegrande.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Document(collection = "customers")
@Data
public class Customer {

    @Id
    private String id;

    @Field("firstName")
    @JsonProperty("first_name")
    private String firstName;

    @Field("lastName")
    @JsonProperty("last_name")
    private String lastName;

    private String phone;
    private String email;
    private String preferences;

    @Field("clientType")
    @JsonProperty("client_type")
    private String clientType;

    // 🔥 NUEVOS CAMPOS
    private Double balance;

    private Integer loyaltyPoints;

    private LocalDate birthDate;

    // 🔥 EDAD CALCULADA (NO SE GUARDA)
    @JsonProperty("age")
    public Integer getAge() {
        if (birthDate == null) return null;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    // ================= AUDITORÍA =================

    @Field("isActive")
    @JsonProperty("is_active")
    private Boolean isActive;

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
package pe.edu.vallegrande.demo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@Document(collection = "admin_users")
@Data
public class AdminUser {

    @Id
    @JsonProperty("user_id")
    private String userId;

    private String username;

    private String password;

    @JsonProperty("is_active")
    private Boolean isActive = true;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    private String role = "ROLE_ADMIN";
}


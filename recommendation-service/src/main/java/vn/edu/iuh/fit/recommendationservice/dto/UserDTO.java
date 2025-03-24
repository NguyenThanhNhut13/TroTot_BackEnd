package vn.edu.iuh.fit.recommendationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String email;
    private String phoneNumber;
    private String fullName;
    private String address;
    private LocalDateTime dob;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

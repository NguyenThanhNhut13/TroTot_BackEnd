package vn.edu.iuh.fit.reviewservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String fullName;
    private String address;
    private LocalDate dob;

    public UserDTO(Long userId, String testUser) {
    }
}

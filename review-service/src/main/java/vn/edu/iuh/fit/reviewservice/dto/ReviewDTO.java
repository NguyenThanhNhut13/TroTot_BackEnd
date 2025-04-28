package vn.edu.iuh.fit.reviewservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private Long roomId;
    private UserDTO user; // lấy từ user-service
    private Integer rating;
    private String comment;
    private List<String> images;
    private LocalDateTime createAt;
}


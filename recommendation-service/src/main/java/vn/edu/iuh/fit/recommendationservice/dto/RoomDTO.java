package vn.edu.iuh.fit.recommendationservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomDTO {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private double price;
    private double area;
    private List<String> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

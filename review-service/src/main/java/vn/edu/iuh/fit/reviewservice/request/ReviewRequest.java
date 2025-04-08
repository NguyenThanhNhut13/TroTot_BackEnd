package vn.edu.iuh.fit.reviewservice.request;

import lombok.Data;

import java.util.List;

@Data
public class ReviewRequest {
    private Long roomId;
    private Long userId;
    private Integer rating;
    private String comment;
    private List<String> images;
}


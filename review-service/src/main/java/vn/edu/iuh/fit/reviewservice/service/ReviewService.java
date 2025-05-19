package vn.edu.iuh.fit.reviewservice.service;

import vn.edu.iuh.fit.reviewservice.dto.ReviewDTO;
import vn.edu.iuh.fit.reviewservice.request.ReviewRequest;

import java.util.List;

public interface ReviewService {
    ReviewDTO create(ReviewRequest request);
    ReviewDTO update(Long id, ReviewRequest request);
    void delete(Long id);
    List<ReviewDTO> getByRoomId(Long roomId);
    ReviewDTO getById(Long id);
    List<ReviewDTO> getByUserId(Long userId);
}


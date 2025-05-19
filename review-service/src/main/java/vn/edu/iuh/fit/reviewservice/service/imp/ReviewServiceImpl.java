package vn.edu.iuh.fit.reviewservice.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.reviewservice.dto.ReviewDTO;
import vn.edu.iuh.fit.reviewservice.dto.UserDTO;
import vn.edu.iuh.fit.reviewservice.entity.Review;
import vn.edu.iuh.fit.reviewservice.repository.ReviewRepository;
import vn.edu.iuh.fit.reviewservice.request.ReviewRequest;
import vn.edu.iuh.fit.reviewservice.service.ReviewService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public ReviewDTO create(ReviewRequest request) {
        Review review = new Review();
        BeanUtils.copyProperties(request, review);
        review.setImages(request.getImages());
        review.setCreateAt(LocalDateTime.now());
        return toDTO(reviewRepository.save(review));
    }


    @Override
    public ReviewDTO update(Long id, ReviewRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        BeanUtils.copyProperties(request, review);
        review.setImages(request.getImages());
        return toDTO(reviewRepository.save(review));
    }


    @Override
    public void delete(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public List<ReviewDTO> getByRoomId(Long roomId) {
        return reviewRepository.findByRoomId(roomId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ReviewDTO getById(Long id) {
        return reviewRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    @Override
    public List<ReviewDTO> getByUserId(Long userId) {
        return reviewRepository.findByUserId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ReviewDTO toDTO(Review review) {
        UserDTO user = getUserFromService(review.getUserId());

        return new ReviewDTO(
                review.getId(),
                review.getRoomId(),
                user,
                review.getRating(),
                review.getComment(),
                review.getImages(),
                review.getCreateAt()
        );
    }

    private UserDTO getUserFromService(Long userId) {
        // Giả lập dữ liệu, bạn có thể dùng RestTemplate hoặc WebClient
        return new UserDTO(userId, "Test User");
    }
}


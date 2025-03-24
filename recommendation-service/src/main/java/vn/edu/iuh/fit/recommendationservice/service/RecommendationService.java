package vn.edu.iuh.fit.recommendationservice.service;

import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.recommendationservice.client.RoomServiceClient;
import vn.edu.iuh.fit.recommendationservice.client.UserServiceClient;
import vn.edu.iuh.fit.recommendationservice.dto.RoomDTO;
import vn.edu.iuh.fit.recommendationservice.entity.Recommendation;
import vn.edu.iuh.fit.recommendationservice.repository.RecommendationRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final RoomServiceClient roomServiceClient;
    private final UserServiceClient userServiceClient;

    public RecommendationService(RecommendationRepository recommendationRepository,
                                 RoomServiceClient roomServiceClient,
                                 UserServiceClient userServiceClient) {
        this.recommendationRepository = recommendationRepository;
        this.roomServiceClient = roomServiceClient;
        this.userServiceClient = userServiceClient;
    }

    // Get recommendations for a user
    public List<RoomDTO> getRecommendations(Long userId) {

        // 1. Lấy danh sách recommendation từ database (Basic Algorithm)
        List<Recommendation> recommendations = recommendationRepository.findByUserIdOrderByScoreDesc(userId);

        if (recommendations.isEmpty()) {
            System.out.println("⚠ No recommendations found for userId: " + userId);
            return Collections.emptyList();
        }

        return recommendations.stream()
                .map(r -> roomServiceClient.getRoomById(r.getRoomId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

}


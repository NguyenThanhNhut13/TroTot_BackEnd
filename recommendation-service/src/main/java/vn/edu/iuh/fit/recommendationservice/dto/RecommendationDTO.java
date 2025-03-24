package vn.edu.iuh.fit.recommendationservice.dto;

public class RecommendationDTO {
    private Long id;

    private Long userId;
    private Long roomId;
    private Double score;

    public RecommendationDTO() {
    }

    public RecommendationDTO(Long id, Long userId, Long roomId, Double score) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}

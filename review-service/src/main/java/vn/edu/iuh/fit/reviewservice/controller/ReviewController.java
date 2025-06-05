package vn.edu.iuh.fit.reviewservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.reviewservice.dto.ReviewDTO;
import vn.edu.iuh.fit.reviewservice.request.ReviewRequest;
import vn.edu.iuh.fit.reviewservice.response.BaseResponse;
import vn.edu.iuh.fit.reviewservice.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<BaseResponse<ReviewDTO>> create(@RequestBody ReviewRequest request) {
        return ResponseEntity.ok(new BaseResponse<>(true, "Created", reviewService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ReviewDTO>> update(@PathVariable Long id, @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(new BaseResponse<>(true, "Updated", reviewService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.ok(new BaseResponse<>(true, "Deleted", null));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<BaseResponse<List<ReviewDTO>>> getByRoomId(@PathVariable Long roomId) {
        return ResponseEntity.ok(new BaseResponse<>(true, "Success", reviewService.getByRoomId(roomId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ReviewDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new BaseResponse<>(true, "Success", reviewService.getById(id)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponse<List<ReviewDTO>>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(new BaseResponse<>(true, "Success", reviewService.getByUserId(userId)));
    }
}


/*
 * @ (#) MediaController.java       1.0     09/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.mediaservice.controller;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 09/04/2025
 * @version:    1.0
 */

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.iuh.fit.mediaservice.model.dto.response.BaseResponse;
import vn.edu.iuh.fit.mediaservice.model.dto.response.ImageUploadResponse;
import vn.edu.iuh.fit.mediaservice.service.CloudinaryService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/medias")
@RequiredArgsConstructor
public class MediaController {
    private final CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        ImageUploadResponse response = cloudinaryService.uploadImage(file);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Upload file successfully.", response)
        );
    }

    @PostMapping("/uploads")
    public ResponseEntity<?> uploadMultipleImages(@RequestParam("files") List<MultipartFile> files) {
        List<ImageUploadResponse> uploadedImages = cloudinaryService.uploadMultipleImages(files);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Upload file successfully.", uploadedImages)
        );
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<?> deleteImage(@PathVariable String publicId) {
        cloudinaryService.deleteImage(publicId);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Deleted image with publicId: " + publicId, null)
        );
    }

}

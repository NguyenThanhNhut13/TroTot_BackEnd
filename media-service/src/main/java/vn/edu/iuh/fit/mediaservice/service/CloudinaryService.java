/*
 * @ (#) CloudinaryService.java       1.0     09/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.mediaservice.service;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 09/04/2025
 * @version:    1.0
 */

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.iuh.fit.mediaservice.exception.NoResourceFoundException;
import vn.edu.iuh.fit.mediaservice.model.dto.response.ImageUploadResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public ImageUploadResponse uploadImage(MultipartFile file) {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String publicId = (String) uploadResult.get("public_id");
            String imageUrl = (String) uploadResult.get("secure_url");

            return ImageUploadResponse.builder()
                    .publicId(publicId)
                    .imageUrl(imageUrl)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }

    public List<ImageUploadResponse> uploadMultipleImages(List<MultipartFile> files) {
        return files.stream()
                .map(this::uploadImage)
                .collect(Collectors.toList());
    }

    public void deleteImage(String publicId) {
        if (publicId == null || publicId.isEmpty()) {
            throw new NoResourceFoundException("Not found public id!");
        }
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image", e);
        }
    }
}

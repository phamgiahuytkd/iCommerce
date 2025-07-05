package com.example.iCommerce.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class ImageController {

    private final String imagePath = "uploads/"; // Đảm bảo rằng đường dẫn này đúng với thư mục uploads của bạn

    @GetMapping("/images/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
        File imageFile = new File(imagePath + imageName);

        if (!imageFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(imageFile);

        String contentType = "application/octet-stream";
        if (imageName.endsWith(".png")) {
            contentType = "image/png";
        } else if (imageName.endsWith(".jpg") || imageName.endsWith(".jpeg")) {
            contentType = "image/jpeg";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageName + "\"")
                .contentType(MediaType.valueOf(contentType))
                .body(resource);
    }
}

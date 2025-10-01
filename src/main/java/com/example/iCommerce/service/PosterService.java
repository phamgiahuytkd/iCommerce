package com.example.iCommerce.service;


import com.example.iCommerce.dto.response.BrandResponse;
import com.example.iCommerce.dto.response.PosterResponse;
import com.example.iCommerce.entity.Brand;
import com.example.iCommerce.entity.Poster;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.BrandMapper;
import com.example.iCommerce.mapper.PosterMapper;
import com.example.iCommerce.repository.BrandRepository;
import com.example.iCommerce.repository.PosterRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PosterService {
    PosterRepository posterRepository;
    PosterMapper posterMapper;
    CloudinaryService cloudinaryService;


    public List<PosterResponse> getPosters(){
        return posterRepository.findAll().stream().map(posterMapper::toPosterResponse).toList();
    }

    public PosterResponse getPoster(String id){
        Poster poster = posterRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.UNAUTHENTICATED)
        );
        return posterMapper.toPosterResponse(poster);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void createPoster(String link, MultipartFile image) throws IOException {
        // Kiểm tra ảnh hợp lệ
        if (image == null || image.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_IMAGE);
        }

        String originalFileName = image.getOriginalFilename();
        if (originalFileName == null || !originalFileName.matches(".*\\.(jpg|jpeg|png|gif)$")) {
            throw new AppException(ErrorCode.INVALID_IMAGE_FORMAT);
        }

        // Upload ảnh lên Cloudinary
        String imageUrl = cloudinaryService.upload(image);

        // Lưu brand vào database
        posterRepository.save(Poster.builder()
                .link(link)
                .image(imageUrl) // lưu URL Cloudinary thay vì filename
                .build());
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void updatePoster(String id, String link, MultipartFile image) throws IOException {
        // Kiểm tra ảnh hợp lệ

        Poster poster = posterRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)
        );

        if (link != null && !link.isEmpty()) {
            poster.setLink(link);
        }

        // Upload ảnh mới nếu có
        if (image != null && !image.isEmpty()) {
            String originalFileName = image.getOriginalFilename();
            if (originalFileName == null || !originalFileName.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$")) {
                throw new AppException(ErrorCode.INVALID_IMAGE_FORMAT);
            }

            // Upload lên Cloudinary
            String imageUrl = cloudinaryService.update(poster.getImage(), image);
            poster.setImage(imageUrl);
        }

        posterRepository.save(poster);
    }



    @PreAuthorize("hasRole('ADMIN')")
    public void deletePoster(String id) throws IOException {
        Poster poster = posterRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)
        );

        cloudinaryService.delete(poster.getImage());
        posterRepository.deleteById(id);
    }



















}

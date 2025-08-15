package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.BrandRequest;
import com.example.iCommerce.dto.response.BrandResponse;
import com.example.iCommerce.dto.response.CategoryResponse;
import com.example.iCommerce.dto.response.ProductResponse;
import com.example.iCommerce.entity.Brand;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.BrandMapper;
import com.example.iCommerce.mapper.CategoryMapper;
import com.example.iCommerce.repository.BrandRepository;
import com.example.iCommerce.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrandService {
    BrandRepository brandRepository;
    BrandMapper brandMapper;
    String uploadDir = "uploads/";
    CloudinaryService cloudinaryService;


    public List<BrandResponse> getBrands(){
        return brandRepository.findAllWithProductCount();
    }

    public BrandResponse getBrand(String id){
        return brandMapper.toBrandResponse(brandRepository.findById(id).
                orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void createBrand(String name, MultipartFile image) throws IOException {
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
        brandRepository.save(Brand.builder()
                .name(name)
                .image(imageUrl) // lưu URL Cloudinary thay vì filename
                .build());
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void updateBrand(String id, String name, MultipartFile image) throws IOException {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED));

        // Cập nhật tên
        if (name != null && !name.isEmpty()) {
            brand.setName(name);
        }

        // Upload ảnh mới nếu có
        if (image != null && !image.isEmpty()) {
            String originalFileName = image.getOriginalFilename();
            if (originalFileName == null || !originalFileName.matches(".*\\.(jpg|jpeg|png|gif)$")) {
                throw new AppException(ErrorCode.INVALID_IMAGE_FORMAT);
            }

            // Upload lên Cloudinary
            String imageUrl = cloudinaryService.upload(image);
            brand.setImage(imageUrl);
        }

        brandRepository.save(brand);
    }




























}

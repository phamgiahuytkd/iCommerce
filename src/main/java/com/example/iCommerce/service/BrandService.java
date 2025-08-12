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


    public List<BrandResponse> getBrands(){
        return brandRepository.findAllWithProductCount();
    }

    public BrandResponse getBrand(String id){
        return brandMapper.toBrandResponse(brandRepository.findById(id).
                orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void updateBrand(String id, String name, MultipartFile image) throws IOException {

        Brand brand = brandRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.BRAND_NOT_EXISTED)
        );

        // Xử lý ảnh
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        if (image != null && !image.isEmpty()) {
            String originalFileName = image.getOriginalFilename();
            if (originalFileName == null || !originalFileName.matches(".*\\.(jpg|jpeg|png|gif)$")) {
                throw new AppException(ErrorCode.INVALID_IMAGE_FORMAT);
            }

            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(newFileName);
            image.transferTo(filePath);

            // Xóa ảnh cũ nếu tồn tại
            if (Objects.nonNull(brand.getImage()) && !brand.getImage().isEmpty()) {
                Path oldImagePath = Paths.get(uploadDir, brand.getImage());
                Files.deleteIfExists(oldImagePath);
            }

            brand.setImage(newFileName);
        }


        brand.setName(name);
        brandRepository.save(brand);
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

        // Lưu ảnh vào thư mục uploads
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(newFileName);
        image.transferTo(filePath);

        brandRepository.save(Brand.builder()
                .name(name)
                .image(newFileName)
                .build());

    }


























}

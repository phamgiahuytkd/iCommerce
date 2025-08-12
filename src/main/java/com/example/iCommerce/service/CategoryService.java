package com.example.iCommerce.service;


import com.example.iCommerce.dto.response.BrandResponse;
import com.example.iCommerce.dto.response.CategoryResponse;
import com.example.iCommerce.entity.Brand;
import com.example.iCommerce.entity.Category;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.CategoryMapper;
import com.example.iCommerce.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
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
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    String uploadDir = "uploads/";


    public List<CategoryResponse> getCategories(){
        return categoryRepository.findAllWithProductCount();
    }

    public CategoryResponse getCategory(String id){
        return categoryMapper.toCategoryResponse(categoryRepository.findById(id).
                orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)));
    }



    @PreAuthorize("hasRole('ADMIN')")
    public void updateCategory(String id, String name, MultipartFile image) throws IOException {

        Category category = categoryRepository.findById(id).orElseThrow(
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
            if (Objects.nonNull(category.getImage()) && !category.getImage().isEmpty()) {
                Path oldImagePath = Paths.get(uploadDir, category.getImage());
                Files.deleteIfExists(oldImagePath);
            }

            category.setImage(newFileName);
        }


        category.setName(name);
        categoryRepository.save(category);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void createCategory(String name, MultipartFile image) throws IOException {

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

        categoryRepository.save(Category.builder()
                .name(name)
                .image(newFileName)
                .build());

    }























}

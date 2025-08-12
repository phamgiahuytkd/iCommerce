package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.RatingRequest;
import com.example.iCommerce.dto.response.*;
import com.example.iCommerce.entity.Order;
import com.example.iCommerce.entity.ProductVariant;
import com.example.iCommerce.entity.Rating;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.CategoryMapper;
import com.example.iCommerce.mapper.RatingMapper;
import com.example.iCommerce.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingService {
    String uploadDir = "uploads/";

    RatingRepository ratingRepository;
    RatingMapper ratingMapper;
    UserRepository userRepository;
    ProductVariantRepository productVariantRepository;
    OrderRepository orderRepository;


    public List<StarCountResponse> getStarCountsForProduct(String productId) {
        return ratingRepository.countStarByProductId(productId);
    }



    public List<RatingResponse> getAllRatingsForProduct(String productId) {
        return ratingRepository.findAllRatingsByProductId(productId);
    }




    //////////////////////
    @PreAuthorize("hasRole('USER')")
    public List<ProductToRateResponse> getProductsToRate(){
        Pageable pageable = PageRequest.of(0, 100);
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();
        Page<Object[]> page = ratingRepository.findProductsToRateByUser(id, pageable);
        return ratingMapper.toProductToRateResponses(page);
    }


    @PreAuthorize("hasRole('USER')")
    public void createRating(RatingRequest request, List<MultipartFile> images) throws IOException {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        ProductVariant productVariant = productVariantRepository.findById(request.getProduct_variant_id())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        Order order = orderRepository.findById(request.getOrder_id())
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        Rating rating = ratingMapper.toRating(request);
        rating.setUser(user);
        rating.setOrder(order);
        rating.setProductVariant(productVariant);
        rating.setCreate_day(LocalDateTime.now());

        List<String> imageNames = new ArrayList<>();

        // Chỉ xử lý nếu có ảnh
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (image == null || image.isEmpty()) {
                    System.out.println("❌ File rỗng hoặc không tồn tại");
                    continue;
                }

                String originalFileName = image.getOriginalFilename();
                System.out.println("📂 Nhận file: " + originalFileName);

                // Kiểm tra file hợp lệ (không phân biệt hoa/thường)
                if (originalFileName == null ||
                        !originalFileName.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)$")) {
                    System.out.println("⚠ Bỏ qua file không hợp lệ: " + originalFileName);
                    continue;
                }

                // Tạo thư mục nếu chưa có
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                    System.out.println("📁 Đã tạo thư mục upload: " + uploadPath);
                }

                // Tạo tên mới và lưu file
                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String newFileName = UUID.randomUUID().toString() + extension;
                Path filePath = uploadPath.resolve(newFileName);
                image.transferTo(filePath);

                imageNames.add(newFileName);
                System.out.println("✅ Đã lưu file: " + newFileName);
            }
        } else {
            System.out.println("⚠ Không nhận được ảnh nào từ request");
        }

        // Gán chuỗi ảnh nếu có
        rating.setImages(imageNames.isEmpty() ? null : String.join(",", imageNames));

        ratingRepository.save(rating);
        System.out.println("💾 Đã lưu rating thành công");
    }



    @PreAuthorize("hasRole('USER')")
    public List<UserRatingResponse> getAllRatingsOfUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Pageable pageable = PageRequest.of(0, 100);
        Page<Object[]> page = ratingRepository.findAllRatingsByUserId(userId, pageable);
        return ratingMapper.toUserRatingResponses(page);
    }












}

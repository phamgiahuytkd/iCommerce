package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.*;
import com.example.iCommerce.dto.response.PriceRangeResponse;
import com.example.iCommerce.dto.response.ProductResponse;
import com.example.iCommerce.entity.*;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.ProductMapper;
import com.example.iCommerce.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    CategoryRepository categoryRepository;
    BrandRepository brandRepository;
    LoveProductRepository loveProductRepository;
    UserRepository userRepository;
    AttributeValueRepository attributeValueRepository;

    String uploadDir = "uploads/";
    private String saveImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) return null;

        String originalFileName = image.getOriginalFilename();
        if (originalFileName == null || !originalFileName.matches(".*\\.(jpg|jpeg|png|gif)$")) {
            throw new AppException(ErrorCode.INVALID_IMAGE_FORMAT);
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(newFileName);
        image.transferTo(filePath);
        System.out.println("Saved image: " + filePath);

        return newFileName;
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void createProduct(ProductRequest request) throws IOException {
        // === 1. Lưu ảnh sản phẩm chính ===
        String productImageFileName = saveImage(request.getImage());

        // === 2. Lấy Category và Brand ===
        Category category = categoryRepository.findById(request.getCategory_id())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        Brand brand = brandRepository.findById(request.getBrand_id())
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_EXISTED));

        // === 3. Tạo đối tượng Product ===
        Product product = productMapper.toProduct(request);
        product.setImage(productImageFileName);
        product.setCategory(category);
        product.setBrand(brand);
        product.setView(0L);
        product.setProductVariants(new ArrayList<>());

        // === 4. Duyệt qua từng ProductVariantRequest ===
        for (ProductVariantRequest variantRequest : request.getVariants()) {
            String variantThumbFileName = saveImage(variantRequest.getImage());
            List<String> variantImagesFileNames = new ArrayList<>();
            if (variantRequest.getImages() != null) {
                for (MultipartFile image : variantRequest.getImages()) {
                    String fileName = saveImage(image);
                    if (fileName != null) variantImagesFileNames.add(fileName);
                }
            }

            ProductVariant variant = ProductVariant.builder()
                    .price(variantRequest.getPrice())
                    .stock(variantRequest.getStock())
                    .stop_day(variantRequest.getStop_day())
                    .create_day(LocalDateTime.now())
                    .image(variantThumbFileName)
                    .images(String.join(",", variantImagesFileNames))
                    .product(product)
                    .variantAttributes(new ArrayList<>())
                    .discounts(new ArrayList<>())
                    .build();

            // === 5. Thuộc tính ===
            if (variantRequest.getAttributes() != null) {
                for (String attributeValueId : variantRequest.getAttributes()) {
                    AttributeValue value = attributeValueRepository.findById(attributeValueId)
                            .orElseThrow(() -> new AppException(ErrorCode.ATTRIBUTE_VALUE_NOT_EXISTED));
                    variant.getVariantAttributes().add(
                            VariantAttribute.builder()
                                    .productVariant(variant)
                                    .attributeValue(value)
                                    .build()
                    );
                }
            }

            // === 6. Discount ===
            if (variantRequest.getDiscount() != null) {
                DiscountRequest d = variantRequest.getDiscount();
                variant.getDiscounts().add(
                        Discount.builder()
                                .productVariant(variant)
                                .percent(d.getPercent())
                                .start_day(d.getStart_day())
                                .end_day(d.getEnd_day())
                                .build()
                );
            }

            product.getProductVariants().add(variant);
        }

        // === 7. Lưu Product ===
        productRepository.save(product);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void updateProduct(String id, ProductRequest request, MultipartFile image) throws IOException {
        // Tìm Product
        Product product = productRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
        );

        // Cập nhật Category
        if (Objects.nonNull(request.getCategory_id()) && !request.getCategory_id().isEmpty()) {
            Category category = categoryRepository.findById(request.getCategory_id()).orElseThrow(
                    () -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED)
            );
            product.setCategory(category);
        }

        // Cập nhật Brand
        if (Objects.nonNull(request.getBrand_id()) && !request.getBrand_id().isEmpty()) {
            Brand brand = brandRepository.findById(request.getBrand_id()).orElseThrow(
                    () -> new AppException(ErrorCode.BRAND_NOT_EXISTED)
            );
            product.setBrand(brand);
        }

        // Cập nhật Name
        if (Objects.nonNull(request.getName()) && !request.getName().isEmpty()) {
            product.setName(request.getName());
        }

        // Cập nhật View
        product.setView(0L);


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
            if (Objects.nonNull(product.getImage()) && !product.getImage().isEmpty()) {
                Path oldImagePath = Paths.get(uploadDir, product.getImage());
                Files.deleteIfExists(oldImagePath);
            }

            product.setImage(newFileName);
        }

        // Lưu Product
        productRepository.save(product);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(String id){
        try {
            productRepository.deleteById(id);
        } catch (Exception e) {
            throw new AppException(ErrorCode.CAN_NOT_DELETE_PRODUCT);
        }

    }



    public ProductResponse getProduct(String id) {
        List<Object[]> product = productRepository.findProductById(id);
        if(product.size() < 1){
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }

        return productMapper.toResponse(product.get(0)); // <-- Bạn phải tự viết mapper nhận Object[]
    }





    public List<ProductResponse> getProducts(){
        Pageable pageable = PageRequest.of(0, 100);
        Page<Object[]> page = productRepository.findProducts(pageable);

        return productMapper.toResponses(page);
    }

    public List<ProductResponse> getTop10ProductDiscount() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Object[]> page = productRepository.findTop10ProductDiscount(pageable);

        return productMapper.toResponses(page);

    }


    public List<ProductResponse> getLatestProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Object[]> page = productRepository.findLatestProducts(pageable);
        return productMapper.toResponses(page); // Chuyển từ Page<ProductResponse> sang List<ProductResponse>
    }


    public List<ProductResponse> getGroupProduct(String category_id, String brand_id){
        Category category = categoryRepository.findById(category_id).orElseThrow(
                () -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED)
        );

        Brand brand = brandRepository.findById(brand_id).orElseThrow(
                () -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED)
        );
        return productRepository.findAllByCategoryAndBrand(category, brand).stream().map(productMapper::toProductResponse).toList();
    }


    public List<ProductResponse> getFilterProduct(SearchProductRequest request) {
        Pageable pageable = PageRequest.of(0, 100);
        Page<Object[]> page = productRepository.findByDynamicQuery(pageable, request.getName(), request.getBrand_id(), request.getCategory_id(), request.getMin_price(), request.getMax_price());
        return productMapper.toResponses(page);
    }

    public List<ProductResponse> getSearchProduct(SearchProductRequest request) {
        Pageable pageable = PageRequest.of(0, 100);
        Page<Object[]> page = productRepository.searchProductsByNameOrBrandOrCategory(pageable, request.getName());
        return productMapper.toResponses(page);
    }





    @PreAuthorize("hasRole('USER')")
    public List<ProductResponse> getLoveProducts(){
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();

        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Object[]> page = loveProductRepository.findLovedProductsByUserId(pageable ,id);
        return productMapper.toResponses(page);

    }

    @PreAuthorize("hasRole('USER')")
    public void createLoveProduct(LoveProductRequest request){
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();

        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        Product product = productRepository.findById(request.getProduct_id()).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
        );

        if (loveProductRepository.existsByUserAndProduct(user, product)) {
            // Nếu tồn tại, trả lỗi
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        }

        // Nếu không tồn tại, tạo mới sản phẩm yêu thích
        LoveProduct loveProduct = LoveProduct.builder()
                .user(user)
                .product(product)
                .create_day(LocalDateTime.now())
                .build();

        loveProductRepository.save(loveProduct);
    }

    @PreAuthorize("hasRole('USER')")
    public void deleteLoveProduct(LoveProductRequest request) {
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        Product product = productRepository.findById(request.getProduct_id()).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
        );

        LoveProduct loveProduct = loveProductRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        loveProductRepository.delete(loveProduct);
    }



}

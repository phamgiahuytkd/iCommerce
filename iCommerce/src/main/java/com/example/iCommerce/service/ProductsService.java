package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.CartCreationRequest;
import com.example.iCommerce.dto.request.ProductsCreationRequest;
import com.example.iCommerce.dto.request.ProductsUpdateRequest;
import com.example.iCommerce.dto.request.SearchProductsRequest;
import com.example.iCommerce.dto.response.CartResponse;
import com.example.iCommerce.dto.response.ProductsResponse;
import com.example.iCommerce.entity.Cart;
import com.example.iCommerce.entity.ProductHistory;
import com.example.iCommerce.entity.Products;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.enums.ActionKey;
import com.example.iCommerce.enums.CartStatus;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.CartMapper;
import com.example.iCommerce.mapper.ProductsMapper;
import com.example.iCommerce.repository.CartRepository;
import com.example.iCommerce.repository.ProductHistoryRepository;
import com.example.iCommerce.repository.ProductsRepository;
import com.example.iCommerce.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductsService {
    ProductsRepository productsRepository;
    ProductsMapper productsMapper;
    ProductHistoryRepository productHistoryRepository;
    CartMapper cartMapper;
    CartRepository cartRepository;
    UserRepository userRepository;
    TrackingService trackingService;
    private EntityManager entityManager;


    @PreAuthorize("hasRole('ADMIN')")
    public void createProducts(ProductsCreationRequest request, MultipartFile image) throws IOException {

        if(productsRepository.existsByNameAndBrand(request.getName(), request.getBrand())) {
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        }

        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();

        // Lưu file ảnh vào thư mục uploads
        String uploadDir = "uploads/"; // Bạn có thể thay đổi đường dẫn theo ý muốn
        Path uploadPath = Paths.get(uploadDir);

        // Tạo thư mục nếu nó chưa tồn tại
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String originalFileName = image.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + extension; // Tên mới duy nhất

        System.out.println("#####" + newFileName);

        Path filePath = uploadPath.resolve(newFileName);
        image.transferTo(filePath);  // Lưu file ảnh

        // Cập nhật đối tượng Products với đường dẫn ảnh
        Products products = productsMapper.toProducts(request);
        products.setCreated_by(id);
        products.setCreated_date(LocalDateTime.now());
        products.setImage(newFileName); // Lưu đường dẫn file vào cơ sở dữ liệu

        System.out.println("File saved at: " + filePath);

        // Lưu sản phẩm vào cơ sở dữ liệu
        productsRepository.save(products);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public ProductsResponse updateProducts(String id, ProductsUpdateRequest request){
        Products products = productsRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
        );

        if ((request.getName() != null && !request.getName().equals(products.getName())) ||
                (request.getBrand() != null && !request.getBrand().equals(products.getBrand()))) {
            if (productsRepository.existsByNameAndBrand(request.getName(), request.getBrand())) {
                throw new AppException(ErrorCode.PRODUCT_EXISTED);
            }
        }

        var context = SecurityContextHolder.getContext();
        String created_by = context.getAuthentication().getName();

        if(!products.getPrice().equals(request.getPrice())  && request.getPrice() != null) {
            ProductHistory productHistory = ProductHistory.builder()
                    .product(products)
                    .price(products.getPrice())
                    .created_by(created_by)
                    .created_date(LocalDateTime.now())
                    .build();

            productHistoryRepository.save(productHistory);
        }

        productsMapper.updateProducts(products, request);


        return productsMapper.toProductsResponse(productsRepository.save(products));
    }



    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductsResponse> updateProductsByBrandAndName(String brand, String name, ProductsUpdateRequest request) {
        // Tìm tất cả các sản phẩm có brand và name trùng khớp
        List<Products> productsList = productsRepository.findByBrandAndName(brand, name);

        if (productsList.isEmpty()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }

        var context = SecurityContextHolder.getContext();
        String created_by = context.getAuthentication().getName();

        // Lưu lịch sử thay đổi giá (nếu có)
        for (Products product : productsList) {
            if (request.getPrice() != null && !request.getPrice().equals(product.getPrice())) {
                ProductHistory productHistory = ProductHistory.builder()
                        .product(product)
                        .price(product.getPrice())
                        .created_by(created_by)
                        .created_date(LocalDateTime.now())
                        .build();
                productHistoryRepository.save(productHistory);
            }

            // Cập nhật các thuộc tính của sản phẩm
            productsMapper.updateProducts(product, request);
        }

        // Lưu lại tất cả sản phẩm đã cập nhật
        productsRepository.saveAll(productsList);

        // Chuyển đổi danh sách sản phẩm đã cập nhật thành danh sách response
        return productsList.stream().map(productsMapper::toProductsResponse).toList();
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProducts(String id){
        productsRepository.deleteById(id);
    }



    public ProductsResponse getProduct(String id){
        var context = SecurityContextHolder.getContext();
        String userId = context.getAuthentication().getName();

        if (userId != null && !userId.equals("anonymousUser")) {
            trackingService.tracking(userId, ActionKey.VIEW_ITEM.name(), "User xem sản phẩm " + id);
        }

        return productsMapper.toProductsResponse(productsRepository.findById(id).
                orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)));
    }





    public List<ProductsResponse> getProducts(){
        return productsRepository.findAll().stream().map(productsMapper::toProductsResponse).toList();
    }


    public List<ProductsResponse> getSearchProducts(SearchProductsRequest request) {
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();


        if (id != null && !id.equals("anonymousUser")) {
            trackingService.tracking(id, ActionKey.SEARCH_ITEM.name(), "User tìm kiếm sản phẩm " + request);
        }


        return productsRepository.findByDynamicQuery(request.getName(), request.getBrand(), request.getColour(), request.getMin_price(), request.getMax_price())
                .stream().map(productsMapper::toProductsResponse).toList();


    }



    //CART

    @PreAuthorize("hasRole('USER')")
    public CartResponse createCart(CartCreationRequest request) {

        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();

        Products products = productsRepository.findById(request.getProduct_id()).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
        );

        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );


        Cart cart = cartMapper.toCart(request);
        cart.setProduct(products);
        cart.setUser(user);
        cart.setPrice(products.getPrice());
        cart.setStatus(CartStatus.WAIT.name());

        trackingService.tracking(user.getId(), ActionKey.ADD_CART.name(), "User thêm sản phẩm " + cart.getProduct().getId());

        return cartMapper.toCartResponse(cartRepository.save(cart));

    }


    @PreAuthorize("hasRole('USER')")
    public void deleteCart(String id){
        cartRepository.deleteById(id);
    }


    @PreAuthorize("hasRole('USER')")
    public List<CartResponse> getCarts(){
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();

        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        return cartRepository.findAllByUserId(id).stream().map(cartMapper::toCartResponse).toList();
    }




}

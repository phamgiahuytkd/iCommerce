package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.CartCreationRequest;
import com.example.iCommerce.dto.request.ProductsCreationRequest;
import com.example.iCommerce.dto.request.ProductsUpdateRequest;
import com.example.iCommerce.dto.response.CartResponse;
import com.example.iCommerce.dto.response.ProductsResponse;
import com.example.iCommerce.entity.Cart;
import com.example.iCommerce.entity.ProductHistory;
import com.example.iCommerce.entity.Products;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.CartMapper;
import com.example.iCommerce.mapper.ProductsMapper;
import com.example.iCommerce.repository.CartRepository;
import com.example.iCommerce.repository.ProductHistoryRepository;
import com.example.iCommerce.repository.ProductsRepository;
import com.example.iCommerce.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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


    @PreAuthorize("hasRole('ADMIN')")
    public ProductsResponse createProducts(ProductsCreationRequest request) {

        if(productsRepository.existsByNameAndBrand(request.getName(), request.getBrand()))
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();


        Products products = productsMapper.toProducts(request);
        products.setCreated_by(id);
        products.setCreated_date(LocalDateTime.now());


        return productsMapper.toProductsResponse(productsRepository.save(products));

    }


    @PreAuthorize("hasRole('ADMIN')")
    public ProductsResponse updateProducts(String id, ProductsUpdateRequest request){
        Products products = productsRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
        );

        if (!request.getName().equals(products.getName()) || !request.getBrand().equals(products.getBrand()))
            if(productsRepository.existsByNameAndBrand(request.getName(), request.getBrand()))
                throw new AppException(ErrorCode.PRODUCT_EXISTED);

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
    public void deleteProducts(String id){
        productsRepository.deleteById(id);
    }



    public ProductsResponse getProduct(String id){
        return productsMapper.toProductsResponse(productsRepository.findById(id).
                orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)));
    }





    public List<ProductsResponse> getProducts(){
        return productsRepository.findAll().stream().map(productsMapper::toProductsResponse).toList();
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

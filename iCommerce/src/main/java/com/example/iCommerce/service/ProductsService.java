package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.ProductsCreationRequest;
import com.example.iCommerce.dto.request.ProductsUpdateRequest;
import com.example.iCommerce.dto.response.ProductsResponse;
import com.example.iCommerce.entity.Products;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.ProductsMapper;
import com.example.iCommerce.repository.ProductsRepository;
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
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        if (!request.getName().equals(products.getName()) || !request.getBrand().equals(products.getBrand()))
            if(productsRepository.existsByNameAndBrand(request.getName(), request.getBrand()))
                throw new AppException(ErrorCode.PRODUCT_EXISTED);

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




}

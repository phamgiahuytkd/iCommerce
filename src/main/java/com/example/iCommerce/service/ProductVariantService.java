package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.LoveProductRequest;
import com.example.iCommerce.dto.request.ProductRequest;
import com.example.iCommerce.dto.response.PriceRangeResponse;
import com.example.iCommerce.dto.response.ProductResponse;
import com.example.iCommerce.dto.response.ProductVariantResponse;
import com.example.iCommerce.entity.*;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.ProductMapper;
import com.example.iCommerce.mapper.ProductVariantMapper;
import com.example.iCommerce.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
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

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductVariantService {
    ProductVariantRepository productVariantRepository;
    ProductVariantMapper productVariantMapper;

    String uploadDir = "uploads/";

    public List<ProductVariantResponse> getProductVariants(String productId){
        List<Object[]> raw = productVariantRepository.findProductVariantsByProductId(productId);
        return productVariantMapper.toResponses(raw);
    }

    public PriceRangeResponse getPriceRange() {
        return  productVariantRepository.findPriceRange();
    }

}

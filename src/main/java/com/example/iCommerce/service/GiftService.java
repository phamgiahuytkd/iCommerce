package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.GiftRequest;
import com.example.iCommerce.dto.response.BrandResponse;
import com.example.iCommerce.dto.response.GiftResponse;
import com.example.iCommerce.entity.Gift;
import com.example.iCommerce.entity.ProductVariant;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.BrandMapper;
import com.example.iCommerce.mapper.GiftMapper;
import com.example.iCommerce.repository.BrandRepository;
import com.example.iCommerce.repository.CartRepository;
import com.example.iCommerce.repository.GiftRepository;
import com.example.iCommerce.repository.ProductVariantRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GiftService {
    GiftRepository giftRepository;
    GiftMapper giftMapper;
    ProductVariantRepository productVariantRepository;
    private final CartRepository cartRepository;


    @PreAuthorize("hasRole('USER')")
    public List<GiftResponse> getGifts(List<String> productIds){
        return giftMapper.toResponses(giftRepository.findValidGiftsByProductIds(productIds));
    }


    public List<GiftResponse> getGiftsProductVariant(String id){

        return giftMapper.toResponses(giftRepository.findValidGiftsByProductVariantId(id));
    }

    public GiftResponse getGift(String id){

        List<Object[]> product = giftRepository.findValidGiftById(id);
        if(product.size() < 1){
            throw new AppException(ErrorCode.GIFT_NOT_EXISTED);
        }

        return giftMapper.toResponse(product.get(0));
    }



    /// admin ///
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void createGift(GiftRequest request) {
        // Kiểm tra product_variant_id (ProductVariant làm quà)
        ProductVariant giftProductVariant = productVariantRepository.findById(request.getProduct_variant_id())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        // Kiểm tra product_variant_ids (chuỗi ID)
        if (request.getProduct_variant_ids() == null || request.getProduct_variant_ids().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        // Tách chuỗi product_variant_ids thành danh sách
        List<String> variantIds = Arrays.asList(request.getProduct_variant_ids().split(","));
        if (variantIds.isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        // Kiểm tra tất cả ProductVariant trong product_variant_ids tồn tại
        List<ProductVariant> productVariants = productVariantRepository.findAllById(variantIds);
        if (productVariants.size() != variantIds.size()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }

        // Kiểm tra stock và ngày hợp lệ
        if (request.getStock() == null || request.getStock() < 0) {
            throw new AppException(ErrorCode.INVALID_STOCK);
        }

        if (request.getStart_day() == null || request.getEnd_day() == null ||
                request.getStart_day().isAfter(request.getEnd_day())) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }

        if (request.getEnd_day().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }


        Optional<Gift> existingGift = giftRepository.findActiveGift(request.getProduct_variant_id(), LocalDateTime.now());

        if (existingGift.isPresent()) {
            throw new AppException(ErrorCode.GIFT_ALREADY_EXISTS);
        }

        // Tạo Gift mới
        Gift gift = Gift.builder()
                .productVariant(giftProductVariant)
                .stock(request.getStock())
                .start_day(request.getStart_day())
                .end_day(request.getEnd_day())
                .productVariants(new ArrayList<>())
                .build();

        // Thêm các ProductVariant vào danh sách productVariants của Gift
        gift.getProductVariants().addAll(productVariants);

        // Cập nhật danh sách gifts của từng ProductVariant
        for (ProductVariant productVariant : productVariants) {
            if (productVariant.getGifts() == null) {
                productVariant.setGifts(new ArrayList<>());
            }
            productVariant.getGifts().add(gift);
        }

        // Lưu Gift
        gift = giftRepository.save(gift);
        // Lưu tất cả ProductVariant
        productVariantRepository.saveAll(productVariants);

        // Trả về response
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<GiftResponse> getGifts(){
        Pageable pageable = PageRequest.of(0, 100);
        Page<Object[]> page = giftRepository.findAllInfoGifts(pageable);
        return giftMapper.toResponses(page);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteGift(String id){
        Gift gift = giftRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.GIFT_NOT_EXISTED)
        );

        gift.setEnd_day(LocalDateTime.now());
        giftRepository.save(gift);

    }




    @PreAuthorize("hasRole('ADMIN')")
    public void updateGift(String id, GiftRequest request){

        Gift gift = giftRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.GIFT_NOT_EXISTED)
        );

        // Kiểm tra product_variant_id (ProductVariant làm quà)
        ProductVariant giftProductVariant = productVariantRepository.findById(request.getProduct_variant_id())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        // Kiểm tra product_variant_ids (chuỗi ID)
        if (request.getProduct_variant_ids() == null || request.getProduct_variant_ids().isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        // Tách chuỗi product_variant_ids thành danh sách
        List<String> variantIds = Arrays.asList(request.getProduct_variant_ids().split(","));
        if (variantIds.isEmpty()) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }

        // Kiểm tra tất cả ProductVariant trong product_variant_ids tồn tại
        List<ProductVariant> productVariants = productVariantRepository.findAllById(variantIds);
        if (productVariants.size() != variantIds.size()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }

        // Kiểm tra stock và ngày hợp lệ
        if (request.getStock() == null || request.getStock() < 0) {
            throw new AppException(ErrorCode.INVALID_STOCK);
        }

        if (request.getStart_day() == null || request.getEnd_day() == null ||
                request.getStart_day().isAfter(request.getEnd_day())) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }

        if (request.getEnd_day().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }

        giftRepository.deleteGiftRelations(id);
        gift.getProductVariants().clear();


        gift.setProductVariant(giftProductVariant);
        gift.setStock(request.getStock());
        gift.setStart_day(request.getStart_day());
        gift.setEnd_day(request.getEnd_day());

        // Thêm các ProductVariant vào danh sách productVariants của Gift
        gift.getProductVariants().addAll(productVariants);

        // Cập nhật danh sách gifts của từng ProductVariant
        for (ProductVariant productVariant : productVariants) {
            if (productVariant.getGifts() == null) {
                productVariant.setGifts(new ArrayList<>());
            }
            productVariant.getGifts().add(gift);
        }

        // Lưu Gift
        gift = giftRepository.save(gift);
        // Lưu tất cả ProductVariant
        productVariantRepository.saveAll(productVariants);

    }

















}

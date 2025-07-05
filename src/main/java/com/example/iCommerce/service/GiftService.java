package com.example.iCommerce.service;


import com.example.iCommerce.dto.response.BrandResponse;
import com.example.iCommerce.dto.response.GiftResponse;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.BrandMapper;
import com.example.iCommerce.mapper.GiftMapper;
import com.example.iCommerce.repository.BrandRepository;
import com.example.iCommerce.repository.GiftRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GiftService {
    GiftRepository giftRepository;
    GiftMapper giftMapper;


    @PreAuthorize("hasRole('USER')")
    public List<GiftResponse> getGifts(List<String> productIds){
        return giftMapper.toResponses(giftRepository.findValidGiftsByProductIds(productIds));
    }


    public List<GiftResponse> getGiftsProductVariant(String id){

        return giftMapper.toResponses(giftRepository.findValidGiftsByProductVariantId(id));
    }


























}

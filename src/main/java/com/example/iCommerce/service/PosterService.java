package com.example.iCommerce.service;


import com.example.iCommerce.dto.response.BrandResponse;
import com.example.iCommerce.dto.response.PosterResponse;
import com.example.iCommerce.mapper.BrandMapper;
import com.example.iCommerce.mapper.PosterMapper;
import com.example.iCommerce.repository.BrandRepository;
import com.example.iCommerce.repository.PosterRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PosterService {
    PosterRepository posterRepository;
    PosterMapper posterMapper;


    public List<PosterResponse> getPosters(){
        return posterRepository.findAll().stream().map(posterMapper::toPosterResponse).toList();
    }


























}

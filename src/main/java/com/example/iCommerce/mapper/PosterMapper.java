package com.example.iCommerce.mapper;


import com.example.iCommerce.dto.request.BrandRequest;
import com.example.iCommerce.dto.request.PosterRequest;
import com.example.iCommerce.dto.response.BrandResponse;
import com.example.iCommerce.dto.response.PosterResponse;
import com.example.iCommerce.entity.Brand;
import com.example.iCommerce.entity.Poster;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PosterMapper {
    Poster toPoster(PosterRequest request);
    PosterResponse toPosterResponse(Poster poster);
}

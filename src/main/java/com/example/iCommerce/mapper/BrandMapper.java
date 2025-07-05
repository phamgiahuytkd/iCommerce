package com.example.iCommerce.mapper;


import com.example.iCommerce.dto.request.BrandRequest;
import com.example.iCommerce.dto.response.BrandResponse;
import com.example.iCommerce.dto.response.CategoryResponse;
import com.example.iCommerce.entity.Brand;
import com.example.iCommerce.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    Brand toBrand(BrandRequest request);
    BrandResponse toBrandResponse(Brand brand);
}

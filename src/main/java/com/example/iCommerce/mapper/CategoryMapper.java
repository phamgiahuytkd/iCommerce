package com.example.iCommerce.mapper;


import com.example.iCommerce.dto.request.UserRequest;
import com.example.iCommerce.dto.response.CategoryResponse;
import com.example.iCommerce.dto.response.UserResponse;
import com.example.iCommerce.entity.Category;
import com.example.iCommerce.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toCategoryResponse(Category category);
}

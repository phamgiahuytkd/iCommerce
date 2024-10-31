package com.example.iCommerce.mapper;


import com.example.iCommerce.dto.request.CartCreationRequest;
import com.example.iCommerce.dto.request.UserCreationRequest;
import com.example.iCommerce.dto.request.UserUpdateRequest;
import com.example.iCommerce.dto.response.CartResponse;
import com.example.iCommerce.dto.response.UserResponse;
import com.example.iCommerce.entity.Cart;
import com.example.iCommerce.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CartMapper {
    Cart toCart(CartCreationRequest request);

    @Mapping(target = "product_id", source = "product.id")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "image", source = "product.image")
    CartResponse toCartResponse(Cart cart);



}

package com.example.iCommerce.mapper;


import com.example.iCommerce.dto.request.CartCreationRequest;
import com.example.iCommerce.dto.request.UserCreationRequest;
import com.example.iCommerce.dto.request.UserUpdateRequest;
import com.example.iCommerce.dto.response.CartResponse;
import com.example.iCommerce.dto.response.UserResponse;
import com.example.iCommerce.entity.Cart;
import com.example.iCommerce.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CartMapper {
    Cart toCart(CartCreationRequest request);
    CartResponse toCartResponse(Cart cart);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCart(@MappingTarget Cart cart, UserUpdateRequest request);
}

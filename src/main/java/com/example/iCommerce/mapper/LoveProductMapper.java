package com.example.iCommerce.mapper;


import com.example.iCommerce.dto.request.LoveProductRequest;
import com.example.iCommerce.dto.response.CartResponse;
import com.example.iCommerce.entity.Cart;
import com.example.iCommerce.entity.LoveProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoveProductMapper {

    LoveProduct toLoveProduct(LoveProductRequest request);
}

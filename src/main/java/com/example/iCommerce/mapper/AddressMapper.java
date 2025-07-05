package com.example.iCommerce.mapper;


import com.example.iCommerce.dto.request.AddressRequest;
import com.example.iCommerce.dto.request.BrandRequest;
import com.example.iCommerce.dto.response.AddressResponse;
import com.example.iCommerce.dto.response.BrandResponse;
import com.example.iCommerce.entity.Address;
import com.example.iCommerce.entity.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toAddress(AddressRequest request);
    AddressResponse toAddressResponse(Address address);
}

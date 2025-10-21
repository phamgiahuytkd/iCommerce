package com.example.iCommerce.mapper;


import com.example.iCommerce.dto.request.BrandRequest;
import com.example.iCommerce.dto.response.AttributeResponse;
import com.example.iCommerce.dto.response.AttributeValueResponse;
import com.example.iCommerce.dto.response.BrandResponse;
import com.example.iCommerce.entity.Attribute;
import com.example.iCommerce.entity.AttributeValue;
import com.example.iCommerce.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttributeMapper {
    // Entity → Response
    // map AttributeValue → AttributeValueResponse
    @Mapping(source = "name", target = "attribute_id")
    @Mapping(source = "id", target = "id")
    AttributeValueResponse toAttributeValueResponse(AttributeValue value);

    // map list
    List<AttributeValueResponse> toAttributeValueResponseList(List<AttributeValue> values);

    // map Attribute → AttributeResponse
    @Mapping(source = "attributeValues", target = "values") // 👈 Bắt buộc
    AttributeResponse toAttributeResponse(Attribute attribute);

    @Mapping(source = "attributeValues", target = "values")
    List<AttributeResponse> toAttributeResponseList(List<Attribute> attributes);

}


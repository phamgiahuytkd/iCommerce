package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.response.AttributeValueResponse;
import com.example.iCommerce.dto.response.GiftResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GiftMapper {

    ObjectMapper mapper = new ObjectMapper();

    // Chuyển JSON từ cột attribute_values -> List<AttributeValueResponse>
    default List<AttributeValueResponse> parseAttributeValuesJson(Object obj) {
        if (obj == null) return null;
        String json = obj.toString();
        try {
            return mapper.readValue(json, new TypeReference<List<AttributeValueResponse>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Chuyển 1 Object[] (1 dòng SQL) thành GiftResponse
    default GiftResponse toResponse(Object[] row) {
        return GiftResponse.builder()
                .id(row[0] != null ? row[0].toString() : null)
                .product_variant_id(row[1] != null ? row[1].toString() : null)
                .name(row[2] != null ? row[2].toString() : null)
                .image(row[3] != null ? row[3].toString() : null)
                .attribute_values(parseAttributeValuesJson(row[4]))
                .build();
    }

    // Chuyển danh sách Object[] -> List<GiftResponse>
    default List<GiftResponse> toResponses(List<Object[]> rows) {
        if (rows == null) return new ArrayList<>();
        return rows.stream().map(this::toResponse).toList();
    }

    // Chuyển Page<Object[]> -> Page<GiftResponse>
    default List<GiftResponse> toResponses(Page<Object[]> page) {
        return page.getContent().stream()
                .map(this::toResponse)
                .toList();
    }

}

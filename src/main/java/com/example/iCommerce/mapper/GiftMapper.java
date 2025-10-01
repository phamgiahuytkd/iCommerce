package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.response.GiftResponse;
import com.example.iCommerce.dto.response.ProductVariantAttributeValueResponse;
import com.example.iCommerce.entity.Gift;
import com.example.iCommerce.entity.ProductVariant;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GiftMapper {

    ObjectMapper mapper = new ObjectMapper();



    // Chuyển JSON từ cột attribute_values -> List<AttributeValueResponse>
    default List<ProductVariantAttributeValueResponse> parseAttributeValuesJson(Object obj) {
        if (obj == null) return null;
        String json = obj.toString();
        try {
            return mapper.readValue(json, new TypeReference<List<ProductVariantAttributeValueResponse>>() {});
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
                .stock(row[5] != null ? ((Number) row[5]).longValue() : null)
                .start_day((row[6] instanceof Timestamp) ? ((Timestamp) row[6]).toLocalDateTime() : null)
                .end_day((row[7] instanceof Timestamp) ? ((Timestamp) row[7]).toLocalDateTime() : null)
                .product_variant_ids(row.length > 8 && row[8] != null ? row[8].toString() : null)
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

package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.response.AttributeValueResponse;
import com.example.iCommerce.dto.response.ProductVariantResponse;
import com.example.iCommerce.entity.ProductVariant;
import com.example.iCommerce.entity.VariantAttribute;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {
    ObjectMapper mapper = new ObjectMapper();

    @Mapping(source = "id", target = "id")
    @Mapping(source = "product.name", target = "name")
    @Mapping(target = "attribute_values", source = "variantAttributes")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "image", target = "image")
    @Mapping(source = "images", target = "images")
    @Mapping(source = "stock", target = "stock")
    @Mapping(source = "create_day", target = "create_day")
    @Mapping(source = "stop_day", target = "stop_day")
    @Mapping(target = "percent", expression = "java(productVariant.getDiscounts() != null && !productVariant.getDiscounts().isEmpty() ? productVariant.getDiscounts().stream().filter(d -> d.getEnd_day().isAfter(java.time.LocalDateTime.now()) && d.getStart_day().isBefore(java.time.LocalDateTime.now())).map(com.example.iCommerce.entity.Discount::getPercent).max(java.util.Comparator.naturalOrder()).orElse(null) : null)")
    @Mapping(target = "start_day", expression = "java(productVariant.getDiscounts() != null && !productVariant.getDiscounts().isEmpty() ? productVariant.getDiscounts().stream().filter(d -> d.getEnd_day().isAfter(java.time.LocalDateTime.now()) && d.getStart_day().isBefore(java.time.LocalDateTime.now())).map(com.example.iCommerce.entity.Discount::getStart_day).min(java.util.Comparator.naturalOrder()).orElse(null) : null)")
    @Mapping(target = "end_day", expression = "java(productVariant.getDiscounts() != null && !productVariant.getDiscounts().isEmpty() ? productVariant.getDiscounts().stream().filter(d -> d.getEnd_day().isAfter(java.time.LocalDateTime.now()) && d.getStart_day().isBefore(java.time.LocalDateTime.now())).map(com.example.iCommerce.entity.Discount::getEnd_day).max(java.util.Comparator.naturalOrder()).orElse(null) : null)")
    @Mapping(target = "star", expression = "java(productVariant.getRatings() != null && !productVariant.getRatings().isEmpty() ? productVariant.getRatings().stream().mapToDouble(com.example.iCommerce.entity.Rating::getStar).average().orElse(0.0) : 0.0)")
    @Mapping(target = "rating_quantity", expression = "java(productVariant.getRatings() != null ? (long) productVariant.getRatings().size() : 0L)")
    ProductVariantResponse toProductVariantResponse(ProductVariant productVariant);

    default List<AttributeValueResponse> mapVariantAttributesToAttributeResponses(List<VariantAttribute> variantAttributes) {
        if (variantAttributes == null) return new ArrayList<>();
        return variantAttributes.stream()
                .map(va -> AttributeValueResponse.builder()
                        .id(va.getAttributeValue().getId())
//                        .attribute_id(va.getAttributeValue().getAttribute().getId())
                        .build())
                .toList();
    }

    default ProductVariantResponse toResponse(Object[] row) {
        return ProductVariantResponse.builder()
                .id(row[0] != null ? row[0].toString() : null)
                .name(row[1] != null ? row[1].toString() : null)
                .attribute_values(parseAttributeValuesJson(row[2]))
                .price(row[3] != null ? ((Number) row[3]).longValue() : null)
                .description(row[4] != null ? row[4].toString() : null)
                .instruction(row[5] != null ? row[5].toString() : null)
                .ingredient(row[6] != null ? row[6].toString() : null)
                .image(row[7] != null ? row[7].toString() : null)
                .images(row[8] != null ? row[8].toString() : null)
                .stock(row[9] != null ? ((Number) row[9]).longValue() : null)
                .create_day(row[10] != null ? ((java.sql.Timestamp) row[10]).toLocalDateTime() : null)
                .stop_day(row[11] != null ? ((java.sql.Timestamp) row[11]).toLocalDateTime() : null)
                .percent(row[12] != null ? ((Number) row[12]).intValue() : null)
                .start_day(row[13] != null ? ((java.sql.Timestamp) row[13]).toLocalDateTime() : null)
                .end_day(row[14] != null ? ((java.sql.Timestamp) row[14]).toLocalDateTime() : null)
                .star(row[15] != null ? ((Number) row[15]).doubleValue() : null)
                .rating_quantity(row[16] != null ? ((Number) row[16]).longValue() : 0L)
                .build();
    }

    default List<ProductVariantResponse> toResponses(List<Object[]> rows) {
        return rows.stream().map(this::toResponse).toList();
    }


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
}
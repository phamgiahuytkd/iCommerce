package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.request.BrandRequest;
import com.example.iCommerce.dto.request.RatingRequest;
import com.example.iCommerce.dto.response.AttributeValueResponse;
import com.example.iCommerce.dto.response.BrandResponse;
import com.example.iCommerce.dto.response.ProductToRateResponse;
import com.example.iCommerce.dto.response.UserRatingResponse;
import com.example.iCommerce.entity.Brand;
import com.example.iCommerce.entity.Rating;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RatingMapper {
    ObjectMapper mapper = new ObjectMapper();

    Rating toRating(RatingRequest request);

    default ProductToRateResponse toProductToRateResponse(Object[] row) {
        return ProductToRateResponse.builder()
                .id((String) row[0])
                .name((String) row[1])
                .image((String) row[2])
                .order_id((String) row[3])
                .attribute_values(parseAttributeValuesJson(row[4]))
                .build();
    }

    default List<ProductToRateResponse> toProductToRateResponses(List<Object[]> rows) {
        return rows.stream().map(this::toProductToRateResponse).toList();
    }

    default UserRatingResponse toUserRatingResponse(Object[] row) {
        Double starDouble = (Double) row[6];
        Integer star = starDouble != null ? starDouble.intValue() : null;

        return UserRatingResponse.builder()
                .product_variant_id((String) row[0])
                .name((String) row[1])
                .attribute_values(parseAttributeValuesJson(row[2]))
                .image((String) row[3])
                .full_name((String) row[4])
                .create_day(((Timestamp) row[5]).toLocalDateTime())
                .star(star)
                .images((String) row[7])
                .comment((String) row[8])
                .build();
    }

    default List<UserRatingResponse> toUserRatingResponses(List<Object[]> rows) {
        return rows.stream().map(this::toUserRatingResponse).toList();
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
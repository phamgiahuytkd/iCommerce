package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.response.AttributeValueResponse;
import com.example.iCommerce.dto.response.CartResponse;
import com.example.iCommerce.dto.response.GiftResponse;
import com.example.iCommerce.entity.Cart;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    ObjectMapper mapper = new ObjectMapper();

    @Mapping(source = "id", target = "id")
    @Mapping(source = "productVariant.id", target = "product_variant_id")
    @Mapping(source = "productVariant.product.id", target = "product_id")
    @Mapping(source = "productVariant.product.name", target = "name")
    @Mapping(source = "price", target = "price")
    @Mapping(source = "productVariant.image", target = "image")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "productVariant.stock", target = "stock")
    @Mapping(source = "productVariant.product.brand.id", target = "brand_id")
    @Mapping(source = "productVariant.variantAttributes", target = "attribute_values")
    @Mapping(source = "selectedGift", target = "gift")
    @Mapping(target = "percent", expression = "java(cart.getProductVariant().getDiscounts() != null && !cart.getProductVariant().getDiscounts().isEmpty() ? cart.getProductVariant().getDiscounts().get(0).getPercent() : null)")
    CartResponse toCartResponse(Cart cart);

    default CartResponse toResponse(Object[] row) {
        return CartResponse.builder()
                .id(row[0] != null ? row[0].toString() : null)
                .product_variant_id(row[1] != null ? row[1].toString() : null)
                .name(row[2] != null ? row[2].toString() : null)
                .attribute_values(parseAttributeValuesJson(row[3]))
                .price(row[4] != null ? ((Number) row[4]).longValue() : null)
                .image(row[5] != null ? row[5].toString() : null)
                .quantity(row[6] != null ? ((Number) row[6]).longValue() : null)
                .stock(row[7] != null ? ((Number) row[7]).longValue() : null)
                .brand_id(row[8] != null ? row[8].toString() : null)
                .percent(row[9] != null ? ((Number) row[9]).intValue() : null)
                .product_id(row[10] != null ? row[10].toString() : null)
                .gift(buildGiftResponse(row))
                .build();
    }

    default GiftResponse buildGiftResponse(Object[] row) {
        if (row[11] == null) return null;

        return GiftResponse.builder()
                .id(row[11] != null ? row[11].toString() : null)
                .product_variant_id(row[12] != null ? row[12].toString() : null)
                .name(row[13] != null ? row[13].toString() : null)
                .image(row[14] != null ? row[14].toString() : null)
                .attribute_values(parseAttributeValuesJson(row[15]))
                .build();
    }

    default List<AttributeValueResponse> parseAttributeValuesJson(Object obj) {
        if (obj == null) return null;
        try {
            return mapper.readValue(obj.toString(), new TypeReference<List<AttributeValueResponse>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    default List<CartResponse> toResponses(Page<Object[]> rows) {
        return rows.stream().map(this::toResponse).toList();
    }
}

package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.response.ProductVariantResponse;
import com.example.iCommerce.entity.Product;
import com.example.iCommerce.entity.ProductVariant;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class ProductVariantMapperImpl implements ProductVariantMapper {

    @Override
    public ProductVariantResponse toProductVariantResponse(ProductVariant productVariant) {
        if ( productVariant == null ) {
            return null;
        }

        ProductVariantResponse.ProductVariantResponseBuilder productVariantResponse = ProductVariantResponse.builder();

        productVariantResponse.id( productVariant.getId() );
        productVariantResponse.name( productVariantProductName( productVariant ) );
        productVariantResponse.attribute_values( parseAttributeValuesJson( productVariant.getVariantAttributes() ) );
        productVariantResponse.price( productVariant.getPrice() );
        productVariantResponse.image( productVariant.getImage() );
        productVariantResponse.images( productVariant.getImages() );
        productVariantResponse.stock( productVariant.getStock() );
        productVariantResponse.create_day( productVariant.getCreate_day() );
        productVariantResponse.stop_day( productVariant.getStop_day() );

        productVariantResponse.percent( productVariant.getDiscounts() != null && !productVariant.getDiscounts().isEmpty() ? productVariant.getDiscounts().stream().filter(d -> d.getEnd_day().isAfter(java.time.LocalDateTime.now()) && d.getStart_day().isBefore(java.time.LocalDateTime.now())).map(com.example.iCommerce.entity.Discount::getPercent).max(java.util.Comparator.naturalOrder()).orElse(null) : null );
        productVariantResponse.start_day( productVariant.getDiscounts() != null && !productVariant.getDiscounts().isEmpty() ? productVariant.getDiscounts().stream().filter(d -> d.getEnd_day().isAfter(java.time.LocalDateTime.now()) && d.getStart_day().isBefore(java.time.LocalDateTime.now())).map(com.example.iCommerce.entity.Discount::getStart_day).min(java.util.Comparator.naturalOrder()).orElse(null) : null );
        productVariantResponse.end_day( productVariant.getDiscounts() != null && !productVariant.getDiscounts().isEmpty() ? productVariant.getDiscounts().stream().filter(d -> d.getEnd_day().isAfter(java.time.LocalDateTime.now()) && d.getStart_day().isBefore(java.time.LocalDateTime.now())).map(com.example.iCommerce.entity.Discount::getEnd_day).max(java.util.Comparator.naturalOrder()).orElse(null) : null );
        productVariantResponse.star( productVariant.getRatings() != null && !productVariant.getRatings().isEmpty() ? productVariant.getRatings().stream().mapToDouble(com.example.iCommerce.entity.Rating::getStar).average().orElse(0.0) : 0.0 );
        productVariantResponse.rating_quantity( productVariant.getRatings() != null ? (long) productVariant.getRatings().size() : 0L );

        return productVariantResponse.build();
    }

    private String productVariantProductName(ProductVariant productVariant) {
        if ( productVariant == null ) {
            return null;
        }
        Product product = productVariant.getProduct();
        if ( product == null ) {
            return null;
        }
        String name = product.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}

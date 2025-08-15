package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.response.CartResponse;
import com.example.iCommerce.dto.response.GiftResponse;
import com.example.iCommerce.entity.Brand;
import com.example.iCommerce.entity.Cart;
import com.example.iCommerce.entity.Gift;
import com.example.iCommerce.entity.Product;
import com.example.iCommerce.entity.ProductVariant;
import com.example.iCommerce.entity.VariantAttribute;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class CartMapperImpl implements CartMapper {

    @Override
    public CartResponse toCartResponse(Cart cart) {
        if ( cart == null ) {
            return null;
        }

        CartResponse.CartResponseBuilder cartResponse = CartResponse.builder();

        cartResponse.id( cart.getId() );
        cartResponse.product_variant_id( cartProductVariantId( cart ) );
        cartResponse.product_id( cartProductVariantProductId( cart ) );
        cartResponse.name( cartProductVariantProductName( cart ) );
        cartResponse.price( cart.getPrice() );
        cartResponse.image( cartProductVariantImage( cart ) );
        cartResponse.quantity( cart.getQuantity() );
        cartResponse.stock( cartProductVariantStock( cart ) );
        cartResponse.brand_id( cartProductVariantProductBrandId( cart ) );
        List<VariantAttribute> variantAttributes = cartProductVariantVariantAttributes( cart );
        cartResponse.attribute_values( parseAttributeValuesJson( variantAttributes ) );
        cartResponse.gift( giftToGiftResponse( cart.getSelectedGift() ) );

        cartResponse.percent( cart.getProductVariant().getDiscounts() != null && !cart.getProductVariant().getDiscounts().isEmpty() ? cart.getProductVariant().getDiscounts().get(0).getPercent() : null );

        return cartResponse.build();
    }

    private String cartProductVariantId(Cart cart) {
        if ( cart == null ) {
            return null;
        }
        ProductVariant productVariant = cart.getProductVariant();
        if ( productVariant == null ) {
            return null;
        }
        String id = productVariant.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String cartProductVariantProductId(Cart cart) {
        if ( cart == null ) {
            return null;
        }
        ProductVariant productVariant = cart.getProductVariant();
        if ( productVariant == null ) {
            return null;
        }
        Product product = productVariant.getProduct();
        if ( product == null ) {
            return null;
        }
        String id = product.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String cartProductVariantProductName(Cart cart) {
        if ( cart == null ) {
            return null;
        }
        ProductVariant productVariant = cart.getProductVariant();
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

    private String cartProductVariantImage(Cart cart) {
        if ( cart == null ) {
            return null;
        }
        ProductVariant productVariant = cart.getProductVariant();
        if ( productVariant == null ) {
            return null;
        }
        String image = productVariant.getImage();
        if ( image == null ) {
            return null;
        }
        return image;
    }

    private Long cartProductVariantStock(Cart cart) {
        if ( cart == null ) {
            return null;
        }
        ProductVariant productVariant = cart.getProductVariant();
        if ( productVariant == null ) {
            return null;
        }
        Long stock = productVariant.getStock();
        if ( stock == null ) {
            return null;
        }
        return stock;
    }

    private String cartProductVariantProductBrandId(Cart cart) {
        if ( cart == null ) {
            return null;
        }
        ProductVariant productVariant = cart.getProductVariant();
        if ( productVariant == null ) {
            return null;
        }
        Product product = productVariant.getProduct();
        if ( product == null ) {
            return null;
        }
        Brand brand = product.getBrand();
        if ( brand == null ) {
            return null;
        }
        String id = brand.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private List<VariantAttribute> cartProductVariantVariantAttributes(Cart cart) {
        if ( cart == null ) {
            return null;
        }
        ProductVariant productVariant = cart.getProductVariant();
        if ( productVariant == null ) {
            return null;
        }
        List<VariantAttribute> variantAttributes = productVariant.getVariantAttributes();
        if ( variantAttributes == null ) {
            return null;
        }
        return variantAttributes;
    }

    protected GiftResponse giftToGiftResponse(Gift gift) {
        if ( gift == null ) {
            return null;
        }

        GiftResponse.GiftResponseBuilder giftResponse = GiftResponse.builder();

        giftResponse.id( gift.getId() );
        giftResponse.stock( gift.getStock() );
        giftResponse.start_day( gift.getStart_day() );
        giftResponse.end_day( gift.getEnd_day() );

        return giftResponse.build();
    }
}

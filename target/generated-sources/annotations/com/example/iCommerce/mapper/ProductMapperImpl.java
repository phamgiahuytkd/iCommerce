package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.request.ProductRequest;
import com.example.iCommerce.dto.response.ProductResponse;
import com.example.iCommerce.entity.Brand;
import com.example.iCommerce.entity.Category;
import com.example.iCommerce.entity.Product;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public Product toProduct(ProductRequest request) {
        if ( request == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.name( request.getName() );
        product.description( request.getDescription() );
        product.instruction( request.getInstruction() );
        product.ingredient( request.getIngredient() );

        return product.build();
    }

    @Override
    public ProductResponse toProductResponse(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductResponse.ProductResponseBuilder productResponse = ProductResponse.builder();

        productResponse.category( productCategoryId( product ) );
        productResponse.brand( productBrandId( product ) );
        productResponse.id( product.getId() );
        productResponse.name( product.getName() );
        productResponse.image( product.getImage() );
        productResponse.view( product.getView() );
        productResponse.description( product.getDescription() );
        productResponse.instruction( product.getInstruction() );
        productResponse.ingredient( product.getIngredient() );

        return productResponse.build();
    }

    private String productCategoryId(Product product) {
        if ( product == null ) {
            return null;
        }
        Category category = product.getCategory();
        if ( category == null ) {
            return null;
        }
        String id = category.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String productBrandId(Product product) {
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
}

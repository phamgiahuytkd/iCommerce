package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.request.LoveProductRequest;
import com.example.iCommerce.entity.LoveProduct;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class LoveProductMapperImpl implements LoveProductMapper {

    @Override
    public LoveProduct toLoveProduct(LoveProductRequest request) {
        if ( request == null ) {
            return null;
        }

        LoveProduct.LoveProductBuilder loveProduct = LoveProduct.builder();

        return loveProduct.build();
    }
}

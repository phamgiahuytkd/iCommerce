package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.request.BrandRequest;
import com.example.iCommerce.dto.response.BrandResponse;
import com.example.iCommerce.entity.Brand;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class BrandMapperImpl implements BrandMapper {

    @Override
    public Brand toBrand(BrandRequest request) {
        if ( request == null ) {
            return null;
        }

        Brand.BrandBuilder brand = Brand.builder();

        brand.id( request.getId() );
        brand.image( request.getImage() );

        return brand.build();
    }

    @Override
    public BrandResponse toBrandResponse(Brand brand) {
        if ( brand == null ) {
            return null;
        }

        BrandResponse.BrandResponseBuilder brandResponse = BrandResponse.builder();

        brandResponse.id( brand.getId() );
        brandResponse.name( brand.getName() );
        brandResponse.image( brand.getImage() );

        return brandResponse.build();
    }
}

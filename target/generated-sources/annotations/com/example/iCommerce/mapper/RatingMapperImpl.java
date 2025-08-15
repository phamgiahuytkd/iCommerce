package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.request.RatingRequest;
import com.example.iCommerce.entity.Rating;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class RatingMapperImpl implements RatingMapper {

    @Override
    public Rating toRating(RatingRequest request) {
        if ( request == null ) {
            return null;
        }

        Rating.RatingBuilder rating = Rating.builder();

        rating.star( request.getStar() );
        rating.comment( request.getComment() );

        return rating.build();
    }
}

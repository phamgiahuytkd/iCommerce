package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.request.PosterRequest;
import com.example.iCommerce.dto.response.PosterResponse;
import com.example.iCommerce.entity.Poster;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class PosterMapperImpl implements PosterMapper {

    @Override
    public Poster toPoster(PosterRequest request) {
        if ( request == null ) {
            return null;
        }

        Poster.PosterBuilder poster = Poster.builder();

        poster.image( request.getImage() );
        poster.link( request.getLink() );

        return poster.build();
    }

    @Override
    public PosterResponse toPosterResponse(Poster poster) {
        if ( poster == null ) {
            return null;
        }

        PosterResponse.PosterResponseBuilder posterResponse = PosterResponse.builder();

        posterResponse.id( poster.getId() );
        posterResponse.image( poster.getImage() );
        posterResponse.link( poster.getLink() );

        return posterResponse.build();
    }
}

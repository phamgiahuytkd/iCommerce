package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.response.AttributeResponse;
import com.example.iCommerce.dto.response.AttributeValueResponse;
import com.example.iCommerce.entity.Attribute;
import com.example.iCommerce.entity.AttributeValue;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class AttributeMapperImpl implements AttributeMapper {

    @Override
    public AttributeValueResponse toAttributeValueResponse(AttributeValue value) {
        if ( value == null ) {
            return null;
        }

        AttributeValueResponse.AttributeValueResponseBuilder attributeValueResponse = AttributeValueResponse.builder();

        attributeValueResponse.attribute_id( value.getName() );
        attributeValueResponse.id( value.getId() );

        return attributeValueResponse.build();
    }

    @Override
    public List<AttributeValueResponse> toAttributeValueResponseList(List<AttributeValue> values) {
        if ( values == null ) {
            return null;
        }

        List<AttributeValueResponse> list = new ArrayList<AttributeValueResponse>( values.size() );
        for ( AttributeValue attributeValue : values ) {
            list.add( toAttributeValueResponse( attributeValue ) );
        }

        return list;
    }

    @Override
    public AttributeResponse toAttributeResponse(Attribute attribute) {
        if ( attribute == null ) {
            return null;
        }

        AttributeResponse.AttributeResponseBuilder attributeResponse = AttributeResponse.builder();

        attributeResponse.values( toAttributeValueResponseList( attribute.getAttributeValues() ) );
        attributeResponse.id( attribute.getId() );
        attributeResponse.name( attribute.getName() );

        return attributeResponse.build();
    }

    @Override
    public List<AttributeResponse> toAttributeResponseList(List<Attribute> attributes) {
        if ( attributes == null ) {
            return null;
        }

        List<AttributeResponse> list = new ArrayList<AttributeResponse>( attributes.size() );
        for ( Attribute attribute : attributes ) {
            list.add( toAttributeResponse( attribute ) );
        }

        return list;
    }
}

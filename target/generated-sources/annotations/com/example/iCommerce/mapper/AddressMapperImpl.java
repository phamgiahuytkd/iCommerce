package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.request.AddressRequest;
import com.example.iCommerce.dto.response.AddressResponse;
import com.example.iCommerce.entity.Address;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class AddressMapperImpl implements AddressMapper {

    @Override
    public Address toAddress(AddressRequest request) {
        if ( request == null ) {
            return null;
        }

        Address.AddressBuilder address = Address.builder();

        address.name( request.getName() );
        address.phone( request.getPhone() );
        address.address( request.getAddress() );
        address.address_detail( request.getAddress_detail() );
        address.locate( request.getLocate() );

        return address.build();
    }

    @Override
    public AddressResponse toAddressResponse(Address address) {
        if ( address == null ) {
            return null;
        }

        AddressResponse.AddressResponseBuilder addressResponse = AddressResponse.builder();

        addressResponse.id( address.getId() );
        addressResponse.name( address.getName() );
        addressResponse.phone( address.getPhone() );
        addressResponse.address( address.getAddress() );
        addressResponse.address_detail( address.getAddress_detail() );
        addressResponse.locate( address.getLocate() );

        return addressResponse.build();
    }
}

package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.request.UserRequest;
import com.example.iCommerce.dto.response.UserLoggedResponse;
import com.example.iCommerce.dto.response.UserResponse;
import com.example.iCommerce.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl extends UserMapper {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public User toUser(UserRequest request) {
        if ( request == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.email( request.getEmail() );
        user.password( request.getPassword() );
        user.full_name( request.getFull_name() );
        user.phone( request.getPhone() );
        user.default_shipping_address( map( request.getDefault_shipping_address() ) );
        user.date_of_birth( request.getDate_of_birth() );

        return user.build();
    }

    @Override
    public UserResponse toUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.id( user.getId() );
        userResponse.email( user.getEmail() );
        userResponse.full_name( user.getFull_name() );
        userResponse.phone( user.getPhone() );
        userResponse.default_shipping_address( addressMapper.toAddressResponse( user.getDefault_shipping_address() ) );
        userResponse.date_of_birth( user.getDate_of_birth() );
        userResponse.avatar( user.getAvatar() );
        userResponse.create_day( user.getCreate_day() );
        userResponse.reputation( user.getReputation() );
        userResponse.stop_day( user.getStop_day() );

        return userResponse.build();
    }

    @Override
    public UserLoggedResponse toUserLoggedResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserLoggedResponse.UserLoggedResponseBuilder userLoggedResponse = UserLoggedResponse.builder();

        userLoggedResponse.full_name( user.getFull_name() );
        userLoggedResponse.avatar( user.getAvatar() );
        userLoggedResponse.account_type( user.getAccount_type() );
        userLoggedResponse.reputation( user.getReputation() );

        userLoggedResponse.default_address( mapAddressToId(user.getDefault_shipping_address()) );

        return userLoggedResponse.build();
    }

    @Override
    public void updateUser(User user, UserRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.getEmail() != null ) {
            user.setEmail( request.getEmail() );
        }
        if ( request.getPassword() != null ) {
            user.setPassword( request.getPassword() );
        }
        if ( request.getFull_name() != null ) {
            user.setFull_name( request.getFull_name() );
        }
        if ( request.getPhone() != null ) {
            user.setPhone( request.getPhone() );
        }
        if ( request.getDefault_shipping_address() != null ) {
            user.setDefault_shipping_address( map( request.getDefault_shipping_address() ) );
        }
        if ( request.getDate_of_birth() != null ) {
            user.setDate_of_birth( request.getDate_of_birth() );
        }
    }
}

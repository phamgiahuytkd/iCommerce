package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.request.UserRequest;
import com.example.iCommerce.dto.response.UserLoggedResponse;
import com.example.iCommerce.dto.response.UserResponse;
import com.example.iCommerce.entity.Address;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.repository.AddressRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public abstract class UserMapper {

    @Autowired
    protected AddressRepository addressRepository;

    public abstract User toUser(UserRequest request);
    public abstract UserResponse toUserResponse(User user);

    @Mapping(target = "default_address", expression = "java(mapAddressToId(user.getDefault_shipping_address()))")
    public abstract UserLoggedResponse toUserLoggedResponse(User user);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateUser(@MappingTarget User user, UserRequest request);

    // Custom mapping from String (id) to Address
    protected Address map(String addressId) {
        if (addressId == null) return null;
        return addressRepository.findById(addressId).orElse(null);
    }


    protected String mapAddressToId(Address address) {
        return address != null ? address.getId() : null;
    }


}
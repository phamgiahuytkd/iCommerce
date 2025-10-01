package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.request.UserRequest;
import com.example.iCommerce.dto.response.UserAdminResponse;
import com.example.iCommerce.dto.response.UserLoggedResponse;
import com.example.iCommerce.dto.response.UserResponse;
import com.example.iCommerce.entity.Address;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.repository.AddressRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public abstract class UserMapper {

    @Autowired
    protected AddressRepository addressRepository;

    @Mapping(target = "avatar", ignore = true)
    public abstract User toUser(UserRequest request);
    public abstract UserResponse toUserResponse(User user);

    @Mapping(target = "default_address", expression = "java(mapAddressToId(user.getDefault_shipping_address()))")
    public abstract UserLoggedResponse toUserLoggedResponse(User user);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "avatar", ignore = true)
    public abstract void updateUser(@MappingTarget User user, UserRequest request);

    // Custom mapping from String (id) to Address
    protected Address map(String addressId) {
        if (addressId == null) return null;
        return addressRepository.findById(addressId).orElse(null);
    }


    protected String mapAddressToId(Address address) {
        return address != null ? address.getId() : null;
    }


    public UserAdminResponse toUserAdminResponse(Object[] row) {
        return UserAdminResponse.builder()
                .id((String) row[0])
                .name((String) row[1])
                .phone((String) row[2])
                .email((String) row[3])
                .order_placed(row[4] != null ? ((Number) row[4]).longValue() : 0L)
                .expend(row[5] != null ? ((Number) row[5]).longValue() : 0L)
                .status((String) row[6])
                .reputation(row[7] != null ? ((Number) row[7]).intValue() : null)
                .latest_order_date((row[8] instanceof java.sql.Timestamp)
                        ? ((java.sql.Timestamp) row[8]).toLocalDateTime()
                        : null)
                .build();
    }

     public List<UserAdminResponse> toUserAdminResponses(Page<Object[]> rows) {
        return rows.stream().map(this::toUserAdminResponse).toList();
    }



}
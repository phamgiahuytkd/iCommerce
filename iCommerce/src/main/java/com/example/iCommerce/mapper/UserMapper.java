package com.example.iCommerce.mapper;


import com.example.iCommerce.dto.request.UserCreationRequest;
import com.example.iCommerce.dto.response.UserResponse;
import com.example.iCommerce.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);
}

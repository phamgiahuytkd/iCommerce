package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.UserCreationRequest;
import com.example.iCommerce.dto.response.UserResponse;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.mapper.UserMapper;
import com.example.iCommerce.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;


    public UserResponse createUser(UserCreationRequest request) {



        User user = userMapper.toUser(request);


        return userMapper.toUserResponse(userRepository.save(user));

    }














}

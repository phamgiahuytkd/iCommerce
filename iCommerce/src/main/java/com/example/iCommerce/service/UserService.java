package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.UserCreationRequest;
import com.example.iCommerce.dto.request.UserUpdateRequest;
import com.example.iCommerce.dto.response.UserResponse;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.enums.ActionKey;
import com.example.iCommerce.enums.Role;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.UserMapper;
import com.example.iCommerce.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    TrackingService trackingService;


    public UserResponse createUser(UserCreationRequest request) {

        if(userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.EMAIL_EXISTED);

        User user = userMapper.toUser(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setUser_type(Role.USER.name());


        return userMapper.toUserResponse(userRepository.save(user));

    }


    @PostAuthorize("returnObject.id == authentication.name")
    public UserResponse updateUser(UserUpdateRequest request){
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        userMapper.updateUser(user, request);

        if (request.getPassword() != null)
            user.setPassword(passwordEncoder.encode(request.getPassword()));

        trackingService.tracking(user.getId(), ActionKey.UPDATE_INFO.name(), "User thay đổi thông tin cá nhân");

        return userMapper.toUserResponse(userRepository.save(user));
    }



    public void deleteUser(String userID){
        userRepository.deleteById(userID);
    }


    @PostAuthorize("returnObject.id == authentication.name")
    public UserResponse getUser(String id){
        return userMapper.toUserResponse(userRepository.findById(id).
                orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers(){
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }



    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        return userMapper.toUserResponse(user);
    }
























}

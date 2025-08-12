package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.UserRequest;
import com.example.iCommerce.dto.response.UserAdminResponse;
import com.example.iCommerce.dto.response.UserLoggedResponse;
import com.example.iCommerce.dto.response.UserResponse;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.enums.Role;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.UserMapper;
import com.example.iCommerce.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    String uploadDir = "uploads/";


    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        // Kiểm tra ngày sinh
        if (request.getDate_of_birth() != null && !request.getDate_of_birth().isBefore(LocalDate.now())) {
            throw new AppException(ErrorCode.INVALID_DATE_OF_BIRTH);
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUser_type(Role.USER.name());
        user.setCreate_day(LocalDateTime.now());
        user.setReputation(100);

        return userMapper.toUserResponse(userRepository.save(user));
    }



    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public UserResponse updateUser(UserRequest request){
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        userMapper.updateUser(user, request);

        if (request.getPassword() != null)
            user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public void updateUserAvatar(MultipartFile image) throws IOException {
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        // Xử lý ảnh
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        if (image != null && !image.isEmpty()) {
            String originalFileName = image.getOriginalFilename();
            if (originalFileName == null || !originalFileName.matches(".*\\.(jpg|jpeg|png|gif)$")) {
                throw new AppException(ErrorCode.INVALID_IMAGE_FORMAT);
            }

            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(newFileName);
            image.transferTo(filePath);

            // Xóa ảnh cũ nếu tồn tại
            if (Objects.nonNull(user.getAvatar()) && !user.getAvatar().isEmpty()) {
                Path oldImagePath = Paths.get(uploadDir, user.getAvatar());
                Files.deleteIfExists(oldImagePath);
            }

            user.setAvatar(newFileName);
        }

        // Lưu Avatar
        userRepository.save(user);


    }





    @PostAuthorize("returnObject.id == authentication.name")
    public UserResponse getUser(String id){
        return userMapper.toUserResponse(userRepository.findById(id).
                orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<UserAdminResponse> getUsers(){
        Pageable pageable = PageRequest.of(0, 1000);
        Page<Object[]> page = userRepository.findAllCustomer(pageable);

        return userMapper.toUserAdminResponses(page);
    }



    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        return userMapper.toUserResponse(user);
    }

    public UserLoggedResponse getUserLogged(){
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        return userMapper.toUserLoggedResponse(user);
    }


    /// ADMIN ///
    @PreAuthorize("hasRole('ADMIN')")
    public void blockUser(String id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        user.setStop_day(LocalDateTime.now());
        userRepository.save(user);
    }





















}

package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.UserRequest;
import com.example.iCommerce.dto.response.*;
import com.example.iCommerce.entity.Order;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.enums.OrderStatus;
import com.example.iCommerce.enums.Role;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.OrderMapper;
import com.example.iCommerce.mapper.UserMapper;
import com.example.iCommerce.repository.OrderRepository;
import com.example.iCommerce.repository.OrderStatusRepository;
import com.example.iCommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
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
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    CloudinaryService cloudinaryService;
    OrderMapper orderMapper;
    String uploadDir = "uploads/";
    OrderRepository orderRepository;
    OrderStatusRepository orderStatusRepository;

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
    public UserResponse updateUser(UserRequest request) throws IOException {
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            String avatarUrl = cloudinaryService.upload(request.getAvatar());
            user.setAvatar(avatarUrl);
        }

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





    @PostAuthorize("hasRole('ADMIN')")
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



    @PreAuthorize("hasRole('ADMIN')")
    public UserOverviewResponse getUserOverview(String id) {
        List<Object[]> userOverview = userRepository.getUserOverview(id);
        if(userOverview.size() < 1){
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        Object[] data = userOverview.get(0);

        return UserOverviewResponse.builder()
                .total_accumulated_money(((Number) data[0]).longValue())
                .total_orders(((Number) data[1]).longValue())
                .processing_orders(((Number) data[2]).longValue())
                .success_orders(((Number) data[3]).longValue())
                .failed_orders(((Number) data[4]).longValue())
                .fraud_orders(((Number) data[5]).longValue())
                .build();
    }



    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> getRecentOrdersUser(String id){

        List<Object[]> page = userRepository.findAllOrdersByUserId(id);
        return orderMapper.toResponses(page);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<Object[]> getTopUserProducts(String id) {
        List<Object[]> raw = userRepository.findTopProductSelectedByUser(id);
        return raw;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Object[]> getTopUserGifts(String id) {
        List<Object[]> raw = userRepository.findTopGiftSelectedByUser(id);
        return raw;
    }



    /// Reputation ///
    @Transactional
    @Scheduled(cron = "0 0 2 * * *") // chạy mỗi ngày lúc 2h sáng
    public void penalizeUnpaidOrders() {
        LocalDateTime deadline = LocalDateTime.now().minusHours(24);

        List<Order> overdueOrders = orderRepository.findDeliveredUnpaidOrders(deadline);

        if (overdueOrders.isEmpty()) {
            log.info("✅ Không có đơn DELIVERED quá 24h chưa thanh toán.");
            return;
        }

        for (Order order : overdueOrders) {
            User user = order.getUser();

            // 🔍 Kiểm tra xem đã có trạng thái PENALTY chưa
            boolean alreadyPenalized = order.getOrderStatuses().stream()
                    .anyMatch(s -> s.getStatus().equals(OrderStatus.PENALTY.name()));

            if (alreadyPenalized) {
                continue; // ✅ bỏ qua nếu đã phạt rồi
            }

            // ⚠ Giảm uy tín người dùng
            int newReputation = Math.max(user.getReputation() - 30, 0);
            user.setReputation(newReputation);
            userRepository.save(user);

            // 🧾 Tạo thêm trạng thái PENALTY
            com.example.iCommerce.entity.OrderStatus penaltyStatus = new com.example.iCommerce.entity.OrderStatus();
            penaltyStatus.setOrder(order);
            penaltyStatus.setStatus(OrderStatus.PENALTY.name());
            penaltyStatus.setUpdate_day(LocalDateTime.now());
            penaltyStatus.setDescription("Đã 24h kể từ khi đơn hàng giao dến, khách hàng vân chưa thanh toán.");
            orderStatusRepository.save(penaltyStatus);

            log.warn("⚠ User {} bị trừ uy tín vì đơn {} chưa thanh toán sau 24h → thêm trạng thái PENALTY",
                    user.getFull_name(), order.getId());
        }
    }








}

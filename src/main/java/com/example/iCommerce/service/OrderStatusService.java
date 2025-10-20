package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.AddressRequest;
import com.example.iCommerce.dto.request.OrderStatusRequest;
import com.example.iCommerce.dto.response.AddressResponse;
import com.example.iCommerce.dto.response.OrderResponse;
import com.example.iCommerce.dto.response.OrderStatusResponse;
import com.example.iCommerce.entity.*;
import com.example.iCommerce.enums.ActionOrder;
import com.example.iCommerce.enums.NotifyType;
import com.example.iCommerce.enums.OrderStatus;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.AddressMapper;
import com.example.iCommerce.mapper.OrderStatusMapper;
import com.example.iCommerce.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderStatusService {
    OrderStatusRepository orderStatusRepository;
    OrderStatusMapper orderStatusMapper;
    OrderRepository orderRepository;
    UserRepository userRepository;
    ProductVariantRepository productVariantRepository;
    SimpMessagingTemplate messagingTemplate;
    NotifyRepository notifyRepository;




    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public void createOrderStatus(OrderStatusRequest request){
        var context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if (request.getStatus().equals(OrderStatus.CANCELED.name())){
            if (!role.equals("ROLE_USER")) {
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
        } else if (request.getStatus().equals(OrderStatus.APPROVED.name())) {
            if (!role.equals("ROLE_ADMIN")) {
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
        } else if (request.getStatus().equals(OrderStatus.REFUSED.name())) {
            if (!role.equals("ROLE_ADMIN")) {
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
        }else {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Order order = orderRepository.findById(request.getOrder_id()).orElseThrow(
                ()-> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)
        );

        com.example.iCommerce.entity.OrderStatus orderStatus = orderStatusMapper.toOrderStatus(request);
        orderStatus.setOrder(order);
        orderStatus.setUpdate_day(LocalDateTime.now());


        orderStatusRepository.save(orderStatus);


        // 🔹 6️⃣ Gửi thông báo cho admin
        User user;
        String title;
        String message;
        if(request.getStatus().equals(OrderStatus.REFUSED.name()) || request.getStatus().equals(OrderStatus.APPROVED.name())){
            user = order.getUser();
            if(request.getStatus().equals(OrderStatus.REFUSED.name())){
                title = "Đơn hàng bị từ chối";
                message = "Đơn hàng #" + order.getId() + " của bạn đã bị từ chối!";
            }else{
                title = "Đơn hàng đã được duyệt";
                message = "Đơn hàng #" + order.getId() + " của bạn đã được duyệt!";
            }
            messagingTemplate.convertAndSend("/topic/user/" + order.getUser().getId(), request.getStatus());
        }else {
            user = userRepository.findByEmail("admin@gmail.com").orElseThrow(
                    () -> new AppException(ErrorCode.USER_NOT_EXISTED)
            );
            title = "Đơn hàng đã bị hủy";
            message = "Khách hàng " + order.getUser().getFull_name() + " vừa hủy đơn #" + order.getId();
            messagingTemplate.convertAndSend("/topic/admin", request.getStatus());
        }





        Notify notify = Notify.builder()
                .title(title)
                .type(NotifyType.ORDER.name())
                .type_id(order.getId())
                .message(message)
                .create_day(LocalDateTime.now())
                .user(user)
                .build();

        notifyRepository.save(notify);




    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<OrderStatusResponse> getUserOrderStatus(String orderId){
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        Order order = orderRepository.findById(orderId).orElseThrow(
                ()-> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)
        );

        return orderStatusRepository.findAllByOrder(order).stream().map(orderStatusMapper::toOderStatusResponse).toList();
    }




    /// deliver ///
    @Transactional
    @PreAuthorize("hasRole('PARTNERSHIP')")
    public void createOrderStatusDeliver(OrderStatusRequest request){

        if(!request.getStatus().equals(OrderStatus.DELIVERING.name()) && !request.getStatus().equals(OrderStatus.DELIVERED.name())
                && !request.getStatus().equals(OrderStatus.PAID.name())){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Order order = orderRepository.findById(request.getOrder_id()).orElseThrow(
                ()-> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)
        );

        if(!orderStatusRepository.existsByOrderAndStatus(order, OrderStatus.DELIVERING.name())){
            for (Cart cart : order.getCarts()) {
                ProductVariant variant = cart.getProductVariant();

                // Tránh trừ âm tồn kho
                long newStock = variant.getStock() - cart.getQuantity();
                if (newStock < 0) {
                    throw new AppException(ErrorCode.PRODUCT_NOT_ENOUGH);
                }

                variant.setStock(newStock);
            }

            // ✅ Lưu toàn bộ thay đổi tồn kho xuống DB
            productVariantRepository.saveAll(
                    order.getCarts().stream()
                            .map(Cart::getProductVariant)
                            .collect(Collectors.toList())
            );


        }



        com.example.iCommerce.entity.OrderStatus orderStatus = orderStatusMapper.toOrderStatus(request);
        orderStatus.setOrder(order);
        orderStatus.setUpdate_day(LocalDateTime.now());


        orderStatusRepository.save(orderStatus);


        // 🔹 6️⃣ Gửi thông báo cho admin
        String title;
        String message;
        User user;

        if(request.getStatus().equals(OrderStatus.PAID.name())){
            user = userRepository.findByEmail("admin@gmail.com").orElseThrow(
                    () -> new AppException(ErrorCode.USER_NOT_EXISTED)
            );

            title = "Đã thanh toán";
            message ="Khách hàng " + order.getUser().getFull_name() + " đã thanh toán đơn hàng #" + order.getId();
            messagingTemplate.convertAndSend("/topic/admin", request.getStatus());
        }else {
            if(request.getStatus().equals(OrderStatus.DELIVERING.name())){
                title = "Đang vận chuyển";
                message ="Đơn hàng #" + order.getId() + " vừa được cập nhật quá trình vận chuyển!";
            }else {
                title = "Đã đến";
                message ="Đơn hàng #" + order.getId() + " vừa được giao đến!";
            }
            user = order.getUser();
            messagingTemplate.convertAndSend("/topic/user/" + order.getUser().getId(), request.getStatus());
        }







        Notify notify = Notify.builder()
                .title(title)
                .type(NotifyType.ORDER.name())
                .type_id(order.getId())
                .message(message)
                .create_day(LocalDateTime.now())
                .user(user)
                .build();

        notifyRepository.save(notify);




    }








}

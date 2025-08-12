package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.AddressRequest;
import com.example.iCommerce.dto.request.OrderStatusRequest;
import com.example.iCommerce.dto.response.AddressResponse;
import com.example.iCommerce.dto.response.OrderResponse;
import com.example.iCommerce.dto.response.OrderStatusResponse;
import com.example.iCommerce.entity.Address;
import com.example.iCommerce.entity.Order;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.enums.ActionOrder;
import com.example.iCommerce.enums.OrderStatus;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.AddressMapper;
import com.example.iCommerce.mapper.OrderStatusMapper;
import com.example.iCommerce.repository.AddressRepository;
import com.example.iCommerce.repository.OrderRepository;
import com.example.iCommerce.repository.OrderStatusRepository;
import com.example.iCommerce.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderStatusService {
    OrderStatusRepository orderStatusRepository;
    OrderStatusMapper orderStatusMapper;
    OrderRepository orderRepository;
    UserRepository userRepository;




    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public void createOrderStatus(OrderStatusRequest request){
        var context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        ActionOrder actionOrder;


        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if (request.getStatus().equals(OrderStatus.CANCELED.name())){
            if (!role.equals("ROLE_USER")) {
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
            actionOrder = ActionOrder.CANCEL;
        } else if (request.getStatus().equals(OrderStatus.APPROVED.name())) {
            if (!role.equals("ROLE_ADMIN")) {
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
            actionOrder = ActionOrder.APPROVE;
        } else if (request.getStatus().equals(OrderStatus.REFUSED.name())) {
            if (!role.equals("ROLE_ADMIN")) {
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
            actionOrder = ActionOrder.REFUSE;
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












}

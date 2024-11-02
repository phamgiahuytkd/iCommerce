package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.OrdersCreationRequest;
import com.example.iCommerce.dto.request.OrdersUpdateRequest;
import com.example.iCommerce.dto.request.UserUpdateRequest;
import com.example.iCommerce.dto.response.OrderDetailResponse;
import com.example.iCommerce.dto.response.OrdersResponse;
import com.example.iCommerce.dto.response.UserResponse;
import com.example.iCommerce.entity.*;
import com.example.iCommerce.enums.CartStatus;
import com.example.iCommerce.enums.OrderStatus;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.OrdersMapper;
import com.example.iCommerce.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    OrdersRepository ordersRepository;
    OrdersMapper ordersMapper;
    CartRepository cartRepository;
    UserRepository userRepository;
    ProductsRepository productsRepository;


    @PreAuthorize("hasRole('USER')")
    public OrdersResponse createOrders(OrdersCreationRequest request) {
        var context = SecurityContextHolder.getContext();
        var id = context.getAuthentication().getName();

        AtomicLong totalAmount = new AtomicLong(0L);

        Map<String, OrderDetailResponse> checkUniqueProduct = new HashMap<>();

        request.getProducts().forEach(cart_id ->{
            Cart cart = cartRepository.findById(cart_id).orElseThrow(
                    ()-> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
            );

            if(!cart.getStatus().equals(CartStatus.WAIT.name()))
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            if(checkUniqueProduct.containsKey(cart.getProduct().getId())){
                OrderDetailResponse detail = checkUniqueProduct.get(cart.getProduct().getId());
                int quantity = detail.getQuantity()+1;
                long price = cart.getPrice();
                long amount = price*quantity;
                totalAmount.set(totalAmount.get()-(quantity*detail.getPrice()));
                totalAmount.set(totalAmount.get()+amount);
                checkUniqueProduct.put(cart.getProduct().getId(), new OrderDetailResponse(
                        cart.getProduct().getId(), cart.getProduct().getName(), quantity, price, amount
                ));
            }else {
                checkUniqueProduct.put(cart.getProduct().getId(), new OrderDetailResponse(
                        cart.getProduct().getId(), cart.getProduct().getName(), 1, cart.getProduct().getPrice(), cart.getProduct().getPrice()
                ));
                totalAmount.set(totalAmount.get()+cart.getProduct().getPrice());
            }

            cart.setStatus(CartStatus.CHECKED.name());
            cartRepository.save(cart);

        });


        var orders = ordersMapper.toOrders(request);
        var user = userRepository.findById(id).orElseThrow(
                ()-> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        if(orders.getShipping_address() == null)
            orders.setShipping_address(user.getDefault_shipping_address());

        if(orders.getOrder_phone() == null)
            orders.setOrder_phone(user.getPhone());

        orders.setAmount(totalAmount.get());
        orders.setOrder_date(LocalDateTime.now());
        orders.setOrder_status(OrderStatus.PROCESSING.name());

        OrdersResponse ordersResponse = ordersMapper.toOrdersResponse(ordersRepository.save(orders));
        ordersResponse.setProductsList(checkUniqueProduct);
        return ordersResponse;
    }



//    @PostAuthorize("returnObject.id == authentication.name")
//    public void updateOrder(OrdersUpdateRequest request){
//        var context = SecurityContextHolder.getContext();
//        String id = context.getAuthentication().getName();
//
//
//
//        return userMapper.toUserResponse(userRepository.save(user));
//    }




}

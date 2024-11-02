package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.OrdersCreationRequest;
import com.example.iCommerce.dto.request.OrdersUpdateRequest;
import com.example.iCommerce.dto.request.UserUpdateRequest;
import com.example.iCommerce.dto.response.*;
import com.example.iCommerce.entity.*;
import com.example.iCommerce.enums.ActionOrder;
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
import org.hibernate.query.Order;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    OrderHistoryRepository orderHistoryRepository;


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
        createOrderHistory(orders.getId(), ActionOrder.CREATE.getKey(), ActionOrder.CREATE.getName(), id);
        return ordersResponse;
    }



    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public void updateOrder(String id,OrdersUpdateRequest request){
        var context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        ActionOrder actionOrder;
        
        
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        if (request.getOrder_status().equals(OrderStatus.CANCELED.name())){
            if (!role.equals("ROLE_USER")) {
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
            actionOrder = ActionOrder.CANCEL;
        } else if (request.getOrder_status().equals(OrderStatus.APPROVED.name())) {
            if (!role.equals("ROLE_ADMIN")) {
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
            actionOrder = ActionOrder.APPROVE;
        } else if (request.getOrder_status().equals(OrderStatus.REFUSED.name())) {
            if (!role.equals("ROLE_ADMIN")) {
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
            actionOrder = ActionOrder.REFUSE;
        }else {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Orders orders = ordersRepository.findById(id).orElseThrow(
                ()-> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)
        );
        orders.setOrder_status(request.getOrder_status());
        createOrderHistory(orders.getId(), actionOrder.getKey(), actionOrder.getName(), authentication.getName());
        ordersRepository.save(orders);


    }


    @PreAuthorize("hasRole('USER')")
    public List<SummaryOrdersResponse> getUserOrders(){
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();


        return orderHistoryRepository.findDistinctOrdersByCreatedBy(id).stream().map(ordersMapper::toSummaryOrdersResponse).toList();
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<SummaryOrdersResponse> getOrders(){
        return ordersRepository.findAll().stream().map(ordersMapper::toSummaryOrdersResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public OrdersResponse getOrder(String id){

        Orders orders = ordersRepository.findById(id).orElseThrow(
                ()-> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)
        );

        AtomicLong totalAmount = new AtomicLong(0L);

        Map<String, OrderDetailResponse> checkUniqueProduct = new HashMap<>();

        Arrays.asList(orders.getProducts().split(",")).forEach(cart_id ->{
            Cart cart = cartRepository.findById(cart_id).orElseThrow(
                    ()-> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
            );


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

        });

        OrdersResponse ordersResponse = ordersMapper.toOrdersResponse(orders);
        ordersResponse.setProductsList(checkUniqueProduct);
        return ordersResponse;


    }

    //ORDER HISTORY
    public void createOrderHistory(String order_id, String key, String name, String user_id){
        Orders orders = ordersRepository.findById(order_id).orElseThrow();
        OrderHistory orderHistory = OrderHistory.builder()
                .order(orders)
                .action_key(key)
                .action_name(name)
                .created_by(user_id)
                .created_date(LocalDateTime.now())
                .build();
        orderHistoryRepository.save(orderHistory);
    }



}

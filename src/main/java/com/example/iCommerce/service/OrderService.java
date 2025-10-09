package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.OrderNowRequest;
import com.example.iCommerce.dto.request.OrderRequest;
import com.example.iCommerce.dto.response.OrderResponse;
import com.example.iCommerce.entity.Cart;
import com.example.iCommerce.entity.Order;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.entity.Voucher;
import com.example.iCommerce.enums.ActionOrder;
import com.example.iCommerce.enums.CartStatus;
import com.example.iCommerce.enums.OrderStatus;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.OrderMapper;
import com.example.iCommerce.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;
    OrderMapper orderMapper;
    CartRepository cartRepository;
    UserRepository userRepository;
    ProductVariantRepository productVariantRepository;
    OrderStatusRepository orderStatusRepository;
    GiftRepository giftRepository;
    VoucherRepository voucherRepository;



    @Transactional
    @PreAuthorize("hasRole('USER')")
    public String createOrder(OrderRequest request) {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );



        List<Cart> userCarts = cartRepository.findByUserIdAndOrderIsNull(userId);

        // Tạo order
        Order order = orderMapper.toOrder(request);
        order.setUser(user);
        order.setDate(LocalDateTime.now());


        long amount = 0L;
        for (Cart cart : userCarts) {
            amount += cart.getPrice() * cart.getQuantity();
            cart.setOrder(order);
        }

// === Xử lý voucher nếu có ===
        if (request.getVoucher_id() != null && !request.getVoucher_id().isEmpty()) {
            Voucher voucher = voucherRepository.findById(request.getVoucher_id())
                    .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_EXISTED));

            boolean validVoucher =
                    voucher.getEnd_day().isAfter(LocalDateTime.now()) &&
                            voucher.getUsed_count() < voucher.getUsage_limit() &&
                            amount >= voucher.getMin_order_amount() &&
                            user.getReputation() >= 100;

            if (validVoucher) {
                order.setVoucher(voucher);

                long discount = 0L;
                if ("FIXED_AMOUNT".equalsIgnoreCase(voucher.getVoucher_type())) {
                    discount = voucher.getMax_amount(); // giảm cố định
                } else if ("PERCENTAGE".equalsIgnoreCase(voucher.getVoucher_type())) {
                    discount = Math.round(amount * (voucher.getPercent() / 100.0));
                    // không vượt quá max_amount
                    discount = Math.min(discount, voucher.getMax_amount());
                }

                // Giảm giá không được âm
                long finalAmount = Math.max(0, amount - discount);
                order.setAmount(finalAmount);

                // Cập nhật lượt dùng voucher
                voucher.setUsed_count(voucher.getUsed_count() + 1);
                voucherRepository.save(voucher);

            } else {
                // Không hợp lệ (hết hạn, hết lượt, không đủ điều kiện)
                order.setAmount(amount);
            }

        } else {
            // Không dùng voucher
            order.setAmount(amount);
        }



        order.setCarts(userCarts);
        order = orderRepository.save(order);
        com.example.iCommerce.entity.OrderStatus orderStatus = com.example.iCommerce.entity.OrderStatus.builder()
                .status(OrderStatus.PROCESSING.name())
                .description("Đang chờ người bán")
                .update_day(LocalDateTime.now())
                .order(order)
                .build();
        orderStatusRepository.save(orderStatus);

        return order.getId();

    }

    @Transactional
    @PreAuthorize("hasRole('USER')")
    public String createOrderBuyNow(OrderNowRequest request) {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("userID!!!!!!!!" + userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        Order order = orderMapper.toOrderNow(request);
        order.setUser(user);
        order.setDate(LocalDateTime.now());
        // Tạo danh sách Cart gắn với order, user, variant
        Order finalOrder = order;
        List<Cart> carts = request.getCarts().stream()
                .map(item -> Cart.builder()
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .selectedGift( item.getSelected_gift_id() != null
                                ? giftRepository.findById(item.getSelected_gift_id()).orElseThrow(
                                        () -> new AppException(ErrorCode.GIFT_NOT_EXISTED))
                                : null)
                        .status(CartStatus.CHECKED.name())
                        .productVariant(productVariantRepository.findById(item.getProduct_variant_id())
                                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)))
                        .user(user)
                        .order(finalOrder)  // Sử dụng biến order ở đây -> OK vì order không thay đổi sau này
                        .build())
                .toList();

        // Gán danh sách Cart vào order
        order.setCarts(carts);

        // Chỉ lưu order, Cart sẽ tự lưu do cascade
        order = orderRepository.save(order);
        com.example.iCommerce.entity.OrderStatus orderStatus = com.example.iCommerce.entity.OrderStatus.builder()
                .status(OrderStatus.PROCESSING.name())
                .description("Đang chờ người bán")
                .update_day(LocalDateTime.now())
                .order(order)
                .build();
        orderStatusRepository.save(orderStatus);


        return order.getId();
    }



    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public OrderResponse updateOrder(String id,OrderRequest request){
        var context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        ActionOrder actionOrder;
        
        

        Order order = orderRepository.findById(id).orElseThrow(
                ()-> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)
        );
        return orderMapper.toOrderResponse(orderRepository.save(order));

    }


    @PreAuthorize("hasRole('USER')")
    public List<OrderResponse> getUserOrders(){
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        return orderRepository.findAllByUserOrderByDateDesc(user).stream().map(orderMapper::toOrderResponse).toList();
    }



    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public OrderResponse getOrder(String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Object[]> rawList = orderRepository.findOrderWithStatusById(id);

//        if (rawList.isEmpty()) {
//            throw new AppException(ErrorCode.ORDER_NOT_FOUND); // bạn có thể định nghĩa thêm code này
//        }

        Object[] row = rawList.get(0);
        String orderUserId = (String) row[11]; // cột user_id

        boolean isUser = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));

        if (isUser) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String currentUserId = jwt.getClaim("sub");
            if (!currentUserId.equals(orderUserId)) {
                throw new AppException(ErrorCode.UNAUTHENTICATED); // Không được phép xem đơn người khác
            }
        }

        return orderMapper.toResponse(row);
    }



    ////////////////////////////////

    @PreAuthorize("hasRole('USER')")
    public List<OrderResponse> getUserOrdersPerStatus(String status){
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        List<Object[]> page = orderRepository.findOrdersByUserAndStatus(id, status);
        return orderMapper.toResponses(page);
    }


    /// admin ///
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> getOrders(String status){


        List<Object[]> page = orderRepository.findAllOrdersByStatus(status);
        return orderMapper.toResponses(page);
    }


}

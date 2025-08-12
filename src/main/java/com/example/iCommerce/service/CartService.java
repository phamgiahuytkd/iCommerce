package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.CartIdsRequest;
import com.example.iCommerce.dto.request.CartRequest;
import com.example.iCommerce.dto.response.CartResponse;
import com.example.iCommerce.entity.*;
import com.example.iCommerce.enums.CartStatus;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.CartMapper;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
    private final CartMapper cartMapper;
    CartRepository cartRepository;
    ProductRepository productRepository;
    UserRepository userRepository;
    ProductVariantRepository productVariantRepository;
    GiftRepository giftRepository;

    @PreAuthorize("hasRole('USER')")
    public void createCart(CartRequest request) {
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();

        ProductVariant productVariant = productVariantRepository.findById(request.getProduct_variant_id()).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
        );

        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        cartRepository.findByUserAndProductVariantWithOrderIdNull(user, productVariant)
                .ifPresentOrElse(cart -> {
                    // Nếu tìm thấy, cập nhật Cart
                    if ((cart.getQuantity() + request.getQuantity()) > productVariant.getStock()) {
                        throw new AppException(ErrorCode.PRODUCT_NOT_ENOUGH);
                    }
                    Gift selectedGift = null;
                    if (request.getSelected_gift_id() != null) {
                        selectedGift = giftRepository.findById(request.getSelected_gift_id())
                                .orElseThrow(() -> new AppException(ErrorCode.GIFT_NOT_EXISTED));
                    }

                    cart.setQuantity(cart.getQuantity() + request.getQuantity());
                    cart.setPrice(productVariant.getPrice());
                    cart.setSelectedGift(selectedGift);
                    cartRepository.save(cart);
                }, () -> {
                    // Nếu không tìm thấy, tạo mới Cart
                    if (request.getQuantity() > productVariant.getStock()) {
                        throw new AppException(ErrorCode.PRODUCT_NOT_ENOUGH);
                    }

                    Gift selectedGift = null;
                    if (request.getSelected_gift_id() != null) {
                        selectedGift = giftRepository.findById(request.getSelected_gift_id())
                                .orElseThrow(() -> new AppException(ErrorCode.GIFT_NOT_EXISTED));
                    }

                    Cart newCart = Cart.builder()
                            .quantity(request.getQuantity())
                            .price(productVariant.getPrice())
                            .status(CartStatus.WAIT.name())
                            .productVariant(productVariant)
                            .selectedGift(selectedGift)
                            .user(user)
                            .build();

                    cartRepository.save(newCart);
                });
    }

    @PreAuthorize("hasRole('USER')")
    public void createCarts(List<CartRequest> request) {
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();


        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        List<Cart> carts = request.stream().map(req -> {
            ProductVariant variant = productVariantRepository.findById(req.getProduct_variant_id())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

            return Cart.builder()
                    .user(user)
                    .productVariant(variant)
                    .quantity(req.getQuantity())
                    .price(req.getPrice())
                    .status(CartStatus.WAIT.name()) // hoặc bất kỳ giá trị nào mặc định
                    .build();
        }).toList();

        cartRepository.saveAll(carts);

    }


    @PreAuthorize("hasRole('USER')")
    public String createCartBuyNow(CartRequest request) {
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();

        ProductVariant productVariant = productVariantRepository.findById(request.getProduct_variant_id()).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
        );

        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        // Kiểm tra số lượng so với tồn kho
        if (request.getQuantity() > productVariant.getStock()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_ENOUGH);
        }

        Cart cart = cartRepository.save(Cart.builder()
                .quantity(request.getQuantity())
                .price(productVariant.getPrice())
                .status(CartStatus.CHECKED.name())
                .productVariant(productVariant)
                .user(user)
                .build());

        return cart.getId();
    }



    @Transactional
    @PreAuthorize("hasRole('USER')")
    public void deleteCart(String id){
        cartRepository.deleteByIdAndOrderIsNull(id);
    }

    @PreAuthorize("hasRole('USER')")
    public void updateCart(String id ,CartRequest request){
        Cart cart = cartRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)
        );
        cart.setQuantity(request.getQuantity());
        cartRepository.save(cart);
    }




    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<CartResponse> getCartsOrder(CartIdsRequest request){
        List<String> listID = Arrays.stream(request.getIds().split(","))
                .map(String::trim)  // Loại bỏ dấu cách trước và sau mỗi phần tử
                .collect(Collectors.toList());
        return cartRepository.findByIdIn(listID).stream().map(cartMapper::toCartResponse).toList();
    }


    @PreAuthorize("hasRole('USER')")
    public Long sumCartUser(){
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();
        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        return cartRepository.sumQuantityByUserIdAndStatusWait(id);
    }



    @PreAuthorize("hasRole('USER')")
    public List<CartResponse> getCarts(){
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();

        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );
        Pageable pageable = PageRequest.of(0, 100);
        Page<Object[]> page = cartRepository.findCartResponsesByUserId(id, pageable);
        return cartMapper.toResponses(page);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<CartResponse> getCartsOrder(String orderId){
        var context = SecurityContextHolder.getContext();
        String id = context.getAuthentication().getName();

        User user = userRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        Pageable pageable = PageRequest.of(0, 100);
        Page<Object[]> raw = cartRepository.findCartResponsesByUserIdAndOrderId(orderId, pageable);
        return cartMapper.toResponses(raw);
    }



}

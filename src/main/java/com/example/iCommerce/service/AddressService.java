package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.AddressRequest;
import com.example.iCommerce.dto.request.OrderRequest;
import com.example.iCommerce.dto.response.AddressResponse;
import com.example.iCommerce.dto.response.CategoryResponse;
import com.example.iCommerce.entity.Address;
import com.example.iCommerce.entity.Cart;
import com.example.iCommerce.entity.Order;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.enums.OrderStatus;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.AddressMapper;
import com.example.iCommerce.mapper.CategoryMapper;
import com.example.iCommerce.repository.AddressRepository;
import com.example.iCommerce.repository.CategoryRepository;
import com.example.iCommerce.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressService {
    AddressMapper addressMapper;
    AddressRepository addressRepository;
    UserRepository userRepository;

    @PreAuthorize("hasRole('USER')")
    public void createAddress(AddressRequest request) {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        Address address = addressMapper.toAddress(request);
        address.setUser(user);
        addressRepository.save(address);

        if(user.getDefault_shipping_address() == null) {
            user.setDefault_shipping_address(address);
            userRepository.save(user);
        }

    }


    @PreAuthorize("hasRole('USER')")
    public List<AddressResponse> findAddressesByUser() {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        return addressRepository.findAllByUser(user).stream().map(addressMapper::toAddressResponse).toList();


    }





    @PreAuthorize("hasRole('USER')")
    public void deleteAddress(String id){
        addressRepository.deleteById(id);
    }
























}

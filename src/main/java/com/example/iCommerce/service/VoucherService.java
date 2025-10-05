package com.example.iCommerce.service;


import com.example.iCommerce.dto.request.AddressRequest;
import com.example.iCommerce.dto.request.VoucherRequest;
import com.example.iCommerce.dto.response.AddressResponse;
import com.example.iCommerce.dto.response.VoucherResponse;
import com.example.iCommerce.entity.Address;
import com.example.iCommerce.entity.User;
import com.example.iCommerce.entity.Voucher;
import com.example.iCommerce.exception.AppException;
import com.example.iCommerce.exception.ErrorCode;
import com.example.iCommerce.mapper.AddressMapper;
import com.example.iCommerce.mapper.VoucherMapper;
import com.example.iCommerce.repository.AddressRepository;
import com.example.iCommerce.repository.OrderRepository;
import com.example.iCommerce.repository.UserRepository;
import com.example.iCommerce.repository.VoucherRepository;
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
public class VoucherService {
    VoucherMapper voucherMapper;
    VoucherRepository voucherRepository;
    OrderRepository orderRepository;


    @PreAuthorize("hasRole('ADMIN')")
    public void createVoucher(VoucherRequest request) {

        Voucher voucher = voucherMapper.toVoucher(request);
        voucher.setUsed_count(0);
        voucherRepository.save(voucher);

    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteVoucher(String id) {

        Voucher voucher = voucherRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.VOUCHER_NOT_EXISTED)
        );

        if (voucher.getOrders() == null || voucher.getOrders().isEmpty()){
            voucherRepository.delete(voucher);
        }else {
            voucher.setEnd_day(LocalDateTime.now());
            voucherRepository.save(voucher);
        }

    }

    @PreAuthorize("hasRole('ADMIN')")
    public void updateVoucher(String id, VoucherRequest request) {

        Voucher voucher = voucherRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.VOUCHER_NOT_EXISTED)
        );

        if (request.getCode() != null && !request.getCode().isEmpty()) {
            voucher.setCode(request.getCode());
        }

        if (request.getDescription() != null && !request.getDescription().isEmpty()) {
            voucher.setDescription(request.getDescription());
        }

        if (request.getVoucher_type() != null && !request.getVoucher_type().isEmpty()) {
            voucher.setVoucher_type(request.getVoucher_type());
        }

        if (request.getPercent() != null && request.getPercent() > 0) {
            voucher.setPercent(request.getPercent());
        }

        if (request.getMax_amount() != null) {
            voucher.setMax_amount(request.getMax_amount());
        }

        if (request.getMin_order_amount() != null) {
            voucher.setMin_order_amount(request.getMin_order_amount());
        }

        if (request.getStart_day() != null &&
                (request.getStart_day().isAfter(LocalDateTime.now()) ||
                        request.getStart_day().isEqual(LocalDateTime.now()))) {

            voucher.setStart_day(request.getStart_day());
        }

        if (request.getEnd_day() != null &&
                (request.getEnd_day().isAfter(LocalDateTime.now()) ||
                        request.getEnd_day().isEqual(LocalDateTime.now()))) {

            LocalDateTime startDayToCompare = request.getStart_day() != null ? request.getStart_day() : voucher.getStart_day();
            if (startDayToCompare != null && request.getEnd_day().isAfter(startDayToCompare)) {
                voucher.setEnd_day(request.getEnd_day());
            }
        }

        if (request.getUsage_limit() != null) {
            voucher.setUsage_limit(request.getUsage_limit());
        }

        voucherRepository.save(voucher);

    }



    @PreAuthorize("hasRole('ADMIN')")
    public List<VoucherResponse> getVouchers() {

        return voucherRepository.findAll().stream().map(voucherMapper::toVoucherResponse).toList();
    }

    @PreAuthorize("hasRole('USER')")
    public List<VoucherResponse> getVouchersByUser() {
        return voucherRepository.findValidVouchers(LocalDateTime.now()).stream().map(voucherMapper::toVoucherResponse).toList();
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public VoucherResponse getVoucher(String id) {

        Voucher voucher = voucherRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.VOUCHER_NOT_EXISTED)
        );


        return voucherMapper.toVoucherResponse(voucher);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public VoucherResponse getVoucherByOrder(String id) {

        Voucher voucher = orderRepository.findVoucherByOrderId(id).orElse(null);


        return voucherMapper.toVoucherResponse(voucher);
    }

























}

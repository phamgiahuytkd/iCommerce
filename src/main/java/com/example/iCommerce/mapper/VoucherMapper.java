package com.example.iCommerce.mapper;


import com.example.iCommerce.dto.request.AddressRequest;
import com.example.iCommerce.dto.request.VoucherRequest;
import com.example.iCommerce.dto.response.AddressResponse;
import com.example.iCommerce.dto.response.VoucherResponse;
import com.example.iCommerce.entity.Address;
import com.example.iCommerce.entity.Voucher;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VoucherMapper {
    Voucher toVoucher(VoucherRequest request);
    VoucherResponse toVoucherResponse(Voucher voucher);
}

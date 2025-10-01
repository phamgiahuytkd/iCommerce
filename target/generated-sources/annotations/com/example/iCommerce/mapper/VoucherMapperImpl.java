package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.request.VoucherRequest;
import com.example.iCommerce.dto.response.VoucherResponse;
import com.example.iCommerce.entity.Voucher;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class VoucherMapperImpl implements VoucherMapper {

    @Override
    public Voucher toVoucher(VoucherRequest request) {
        if ( request == null ) {
            return null;
        }

        Voucher.VoucherBuilder voucher = Voucher.builder();

        voucher.code( request.getCode() );
        voucher.description( request.getDescription() );
        voucher.voucher_type( request.getVoucher_type() );
        voucher.percent( request.getPercent() );
        voucher.max_amount( request.getMax_amount() );
        voucher.min_order_amount( request.getMin_order_amount() );
        voucher.start_day( request.getStart_day() );
        voucher.end_day( request.getEnd_day() );
        voucher.usage_limit( request.getUsage_limit() );
        voucher.used_count( request.getUsed_count() );

        return voucher.build();
    }

    @Override
    public VoucherResponse toVoucherResponse(Voucher voucher) {
        if ( voucher == null ) {
            return null;
        }

        VoucherResponse.VoucherResponseBuilder voucherResponse = VoucherResponse.builder();

        voucherResponse.id( voucher.getId() );
        voucherResponse.code( voucher.getCode() );
        voucherResponse.description( voucher.getDescription() );
        voucherResponse.voucher_type( voucher.getVoucher_type() );
        voucherResponse.percent( voucher.getPercent() );
        voucherResponse.max_amount( voucher.getMax_amount() );
        voucherResponse.min_order_amount( voucher.getMin_order_amount() );
        voucherResponse.start_day( voucher.getStart_day() );
        voucherResponse.end_day( voucher.getEnd_day() );
        voucherResponse.usage_limit( voucher.getUsage_limit() );
        voucherResponse.used_count( voucher.getUsed_count() );

        return voucherResponse.build();
    }
}

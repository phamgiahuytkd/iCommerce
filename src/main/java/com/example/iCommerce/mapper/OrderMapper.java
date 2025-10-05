package com.example.iCommerce.mapper;


import com.example.iCommerce.dto.request.OrderNowRequest;
import com.example.iCommerce.dto.request.OrderRequest;
import com.example.iCommerce.dto.response.OrderResponse;
import com.example.iCommerce.dto.response.UserRatingResponse;
import com.example.iCommerce.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "voucher", ignore = true)
    @Mapping(target = "amount", ignore = true)
    Order toOrder(OrderRequest request);

    Order toOrderNow(OrderNowRequest request);

    @Mapping(source = "user.id", target = "user_id")
    OrderResponse toOrderResponse(Order order);

    // ✅ Chuyển Object[] sang OrderResponse
    default OrderResponse toResponse(Object[] row) {
        return OrderResponse.builder()
                .id((String) row[0])
                .name((String) row[1])
                .amount(row[2] != null ? ((Number) row[2]).longValue() : null)
                .address((String) row[3])
                .fulladdress((String) row[4])
                .note((String) row[5])
                .phone((String) row[6])
                .date(((Timestamp) row[7]).toLocalDateTime())
                .status((String) row[8])
                .payment((String) row[9])
                .device((String) row[10])
                .user_id((String) row[11])
                .build();
    }

    default List<OrderResponse> toResponses(Page<Object[]> rows) {
        return rows.stream().map(this::toResponse).toList();
    }
}


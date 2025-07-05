package com.example.iCommerce.mapper;

import com.example.iCommerce.dto.request.ProductRequest;
import com.example.iCommerce.dto.response.ProductResponse;
import com.example.iCommerce.entity.Product;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true) // Gán từ MultipartFile
    @Mapping(target = "view", ignore = true) // Mặc định view = 0
    @Mapping(target = "category", ignore = true) // Gán thủ công
    @Mapping(target = "brand", ignore = true) // Gán thủ công
    @Mapping(target = "productVariants", ignore = true) // Khởi tạo rỗng
    Product toProduct(ProductRequest request);

    @Mapping(source = "category.id", target = "category")
    @Mapping(source = "brand.id", target = "brand")
    ProductResponse toProductResponse(Product product);

    default String localDateTimeToString(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
    }

    // Chuyển từng Object[] → DTO
    default ProductResponse toResponse(Object[] row) {
        return ProductResponse.builder()
                .id((String) row[0])
                .name((String) row[1])
                .price(row[2] != null ? ((Number) row[2]).longValue() : null)
                .brand((String) row[3])
                .category((String) row[4])
                .image((String) row[5])
                .view(row[6] != null ? ((Number) row[6]).longValue() : null)
                .stock(row[7] != null ? ((Number) row[7]).longValue() : null)
                .create_day((row[8] instanceof Timestamp) ? ((Timestamp) row[8]).toLocalDateTime() : null)
                .percent(row[9] != null ? ((Number) row[9]).intValue() : null)
                .gift_name((String) row[10])
                .gift_image((String) row[11])
                .gift_stock(row[12] != null ? ((Number) row[12]).longValue() : null)
                .gift_start_day((row[13] instanceof Timestamp) ? ((Timestamp) row[13]).toLocalDateTime() : null)
                .gift_end_day((row[14] instanceof Timestamp) ? ((Timestamp) row[14]).toLocalDateTime() : null)
                .star(row[15] != null ? ((Number) row[15]).doubleValue() : null)
                .gift_id((String) row[16]) // Ánh xạ gift_id
                .build();
    }

    // Chuyển List<Object[]> → List<DTO>
    default List<ProductResponse> toResponses(Page<Object[]> rows) {
        return rows.stream().map(this::toResponse).toList();
    }
}
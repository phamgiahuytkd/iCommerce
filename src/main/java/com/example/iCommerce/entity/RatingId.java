    package com.example.iCommerce.entity;

    import jakarta.persistence.Embeddable;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    import java.io.Serializable;
    import java.time.LocalDateTime;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable // Đánh dấu là khóa chính hỗn hợp
    public class RatingId implements Serializable {
        private String productVariant;
        private String user;
        private String order;
    }

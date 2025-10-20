package com.example.iCommerce.dto.response;

import com.example.iCommerce.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotifyResponse {
    String id;
    String title;
    String type;
    String type_id;
    String message;
    LocalDateTime create_day;
}

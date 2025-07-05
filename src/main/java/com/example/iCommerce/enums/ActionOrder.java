package com.example.iCommerce.enums;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE, makeFinal = true)
public enum ActionOrder {
    APPROVE("APPROVE", "Duyệt đơn hàng"),
    CANCEL("CANCEL", "Hủy đơn hàng"),
    CREATE("CREATE", "xác nhận tạo đơn hàng"),
    REFUSE("REFUSE", "Từ chối đơn hàng"),
    ;

    String key;
    String name;

    // Constructor
    ActionOrder(String key, String name) {
        this.key = key;
        this.name = name;
    }

    // Optional: Method to get an enum by key
    public static ActionOrder fromKey(String key) {
        for (ActionOrder action : ActionOrder.values()) {
            if (action.getKey().equals(key)) {
                return action;
            }
        }
        throw new IllegalArgumentException("Invalid key: " + key);
    }
}

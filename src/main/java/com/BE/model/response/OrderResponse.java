package com.BE.model.response;

import com.BE.enums.OrderStatus;
import com.BE.model.entity.OrderHistory;
import com.BE.model.entity.OrderItem;
import com.BE.model.entity.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    UUID id;

    BigDecimal totalPrice;

    String createdAt;

    @Enumerated(EnumType.STRING)
    OrderStatus status;

    User user;

    Set<OrderItem> orderItems = new HashSet<>();

    Set<OrderHistory> orderHistories = new HashSet<>();

}

package com.BE.enums;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAID,
    PAYMENT_FAILED,
    AWAITING_PICKUP,            // Đang chờ nhận hàng tại cửa hàng
    AWAITING_DELIVERY,          // Đang chờ giao hàng (giao hàng tận nơi)

    // Trạng thái cho đơn hàng đã hoàn tất
    COMPLETED                   // Đơn hàng đã hoàn tất (đã giao hoặc đã nhận)

}

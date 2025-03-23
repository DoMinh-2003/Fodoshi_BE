package com.BE.model.request;


import com.BE.enums.ShippingType;
import com.BE.enums.StatusEnum;
import com.BE.exception.EnumValidator;
import com.BE.model.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class CreatePaymentLinkRequestBody {
    // Fields for both guest and authenticated users
    private String returnUrl;
    private String cancelUrl;
    private String description;
    
    // Fields for authenticated users
    @NotNull(message = "cartItemIds cannot be null")
    private List<UUID> cartItemIds;
    @NotNull(message = "productId cannot be null")
    private Long productId;
    @NotNull(message = "addressId cannot be null")
    private Long addressId;
    
    // Guest checkout fields
    private boolean isGuest;
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private Address guestAddress;

    @Schema(example = "HOME_DELIVERY, IN_STORE_PICKUP", description = "Type Enum")
    @EnumValidator(enumClass = ShippingType.class, message = "Invalid Enum value")
    @Enumerated(EnumType.STRING)
    ShippingType shippingType;

    private BigDecimal totalPrice = BigDecimal.ZERO;


    // Used for direct product checkout (guest mode)
    private List<Long> productIds;
}

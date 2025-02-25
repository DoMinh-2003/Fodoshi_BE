package com.BE.model.entity;

import com.BE.enums.CartItemStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItem {

    @Id
    @UuidGenerator
    UUID id;

    BigDecimal price;
    String createdAt;

    @Enumerated(EnumType.STRING)
    CartItemStatus status;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;


    @ManyToOne
    @JoinColumn(name = "cart_id")
    @JsonIgnore
    Cart cart;
}

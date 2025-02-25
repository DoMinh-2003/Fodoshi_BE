package com.BE.mapper;

import com.BE.enums.CartItemStatus;
import com.BE.model.entity.Cart;
import com.BE.model.entity.CartItem;
import com.BE.model.response.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "cartItems", qualifiedBy = MapAddedCartItems.class)
    @Mapping(target = "totalPrice", expression = "java(setTotalPrice(cart))")
    CartResponse toCartResponse(Cart cart);

    default BigDecimal setTotalPrice(Cart cart) {
        return cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getStatus().equals(CartItemStatus.ADDED))
                .map(cartItem -> cartItem.getProduct().getSellingPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    @interface MapAddedCartItems {}
    @MapAddedCartItems
    default Set<CartItem> mapAddedCartItems(Set<CartItem> cartItems) {
        return cartItems.stream()
                .filter(cartItem -> cartItem.getStatus().equals(CartItemStatus.ADDED))
                .peek(cartItem -> cartItem.setPrice(cartItem.getProduct().getSellingPrice()))
                .collect(Collectors.toSet());
    }


}

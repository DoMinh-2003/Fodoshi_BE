package com.BE.repository;

import com.BE.enums.CartItemStatus;
import com.BE.model.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    Optional<CartItem> findByIdAndStatus(UUID uuid, CartItemStatus status);

}

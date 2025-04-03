package com.BE.repository;

import com.BE.enums.OrderStatus;
import com.BE.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByUserId(UUID id);

    Optional<Order> findByIdAndStatus(UUID uuid, OrderStatus status);

    List<Order> findAllByCreatedAtBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, OrderStatus status);
    List<Order> findAllByStatus(OrderStatus status);

    List<Order> findByUser_PhoneNumberOrUser_EmailOrAddress_GuestPhoneOrAddress_GuestEmail(String searchTerm, String searchTerm1, String searchTerm2, String searchTerm3);
}

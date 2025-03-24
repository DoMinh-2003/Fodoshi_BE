package com.BE.model.entity;


import com.BE.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Convert;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.BE.converter.StringToLocalDateTimeConverter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    OrderStatus status;

    String image;

    String note;

    @Column(name = "created_at", columnDefinition = "VARCHAR(255)")
    @Convert(converter = StringToLocalDateTimeConverter.class)
    LocalDateTime createdAt;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    Order order;
}

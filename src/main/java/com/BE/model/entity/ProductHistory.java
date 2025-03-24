package com.BE.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Convert;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import com.BE.converter.StringToLocalDateTimeConverter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String status;

    @Column(name = "created_at", columnDefinition = "VARCHAR(255)")
    @Convert(converter = StringToLocalDateTimeConverter.class)
    LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    Product product;
}

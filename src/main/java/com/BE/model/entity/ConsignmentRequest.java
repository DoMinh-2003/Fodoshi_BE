package com.BE.model.entity;

import com.BE.enums.ConsignmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "consignment_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsignmentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String sizeLength;

    @Column(nullable = false)
    private String sizeWidth;

    @Column(nullable = false)
    private String sizeWaist;

    @ElementCollection
    private List<String> imageUrls;

    @Column(nullable = false)
    private String consignmentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsignmentStatus status = ConsignmentStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
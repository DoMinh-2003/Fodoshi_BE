package com.BE.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String address;
    String province;
    String district;
    String commune;


    Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true) // Make nullable to support guest addresses
    @JsonIgnore
    User user;

    // Add guest-specific fields
    String guestName;
    String guestPhone;
    String guestEmail;

    @JsonIgnore
    @OneToMany(mappedBy = "address",cascade = CascadeType.ALL)
    Set<Order> orders = new HashSet<>();
}

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
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;


    @JsonIgnore
    @OneToMany(mappedBy = "address",cascade = CascadeType.ALL)
    Set<Order> orders = new HashSet<>();
}

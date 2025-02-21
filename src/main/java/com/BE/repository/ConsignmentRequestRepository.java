package com.BE.repository;

import com.BE.model.entity.ConsignmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsignmentRequestRepository extends JpaRepository<ConsignmentRequest, Long> {}
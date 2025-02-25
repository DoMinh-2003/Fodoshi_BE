package com.BE.model.request;

import com.BE.enums.ProductStatus;
import com.BE.exception.EnumValidator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductStatusRequest {

    @Schema(example = "PENDING, APPROVED, REJECTED, AVAILABLE, SOLD, REMOVED", description = "Status Enum")
    @EnumValidator(enumClass = ProductStatus.class, message = "Invalid status value")
    @Enumerated(EnumType.STRING)
    ProductStatus status;
}

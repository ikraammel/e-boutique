package com.ecommerce.demo.models;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


public enum Status {
    PENDING,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED
}


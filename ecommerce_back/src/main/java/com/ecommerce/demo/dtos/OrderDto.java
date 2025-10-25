package com.ecommerce.demo.dtos;

import com.ecommerce.demo.models.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Integer id;

    private UserDto user;

    private List<OrderItemDto> items;

    private double total;

    private Status status;
}

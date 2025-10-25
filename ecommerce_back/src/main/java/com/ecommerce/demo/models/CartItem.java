package com.ecommerce.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JsonIgnore
    private Cart cart;

    private int quantity;

    @ManyToOne
    private ProductVariant variant;

    private double subtotal;

    @PrePersist
    @PreUpdate
    public void calculateSubtotal(){
        this.subtotal = variant.getPrice() * quantity;
    }
}

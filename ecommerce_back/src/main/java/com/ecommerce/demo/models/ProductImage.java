package com.ecommerce.demo.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "variant")
@ToString(exclude = "variant")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;
}

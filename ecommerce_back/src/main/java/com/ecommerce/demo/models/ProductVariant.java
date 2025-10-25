package com.ecommerce.demo.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"images", "product"})
@ToString(exclude = {"images", "product"})
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String color;

    @Column(nullable = false)
    private double price;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<SizeClothing> sizeClothings;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<SizePants> sizePants;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<ProductImage> images;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}

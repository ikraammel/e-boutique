    package com.ecommerce.demo.models;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.NoArgsConstructor;
    import org.hibernate.annotations.Fetch;
    import org.hibernate.annotations.FetchMode;

    import java.util.Set;

    @Entity
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(exclude = "variants")
    public class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Column(nullable = false)
        private String name;

        private String description;

        @Column(nullable = false)
        private double price;

        @Column(nullable = false)
        private int stock;

        @Enumerated(EnumType.STRING)
        private Category category;

        @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
        @Fetch(FetchMode.SUBSELECT)
        private Set<ProductVariant> variants;

    }

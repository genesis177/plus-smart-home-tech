package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "warehouse_products")
public class Warehouse {
    @Id
    @Column(name = "product_id", nullable = false, unique = true)
    UUID productId;

    @Column(name = "fragile", nullable = false)
    boolean fragile;

    @Embedded
    Dimension dimension;

    @Column(name = "weight", nullable = false)
    Double weight;

    @Column(name = "quantity")
    long quantity;
}

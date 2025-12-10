package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;
import ru.yandex.practicum.dto.delivery.DeliveryState;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@Entity
@Table(name = "delivery")
public class Delivery {
    @Id
    @UuidGenerator
    @Column(name = "delivery_id")
    UUID deliveryId;

    @OneToOne
    @JoinColumn(name = "from_address_id")
    Address fromAddress;

    @OneToOne
    @JoinColumn(name = "to_address_id")
    Address toAddress;

    @Column(name = "order_id")
    UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_state")
    DeliveryState deliveryState;

    Double weight;

    Double volume;

    Boolean fragile;
}
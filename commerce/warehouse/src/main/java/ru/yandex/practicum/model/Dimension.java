package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Embeddable
public class Dimension {
    @Column(name = "height", nullable = false)
    Double height;

    @Column(name = "width", nullable = false)
    Double width;

    @Column(name = "depth", nullable = false)
    Double depth;

}

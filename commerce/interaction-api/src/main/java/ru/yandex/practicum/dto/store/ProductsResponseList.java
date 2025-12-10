package ru.yandex.practicum.dto.store;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductsResponseList {
    List<ProductDto> content;
    List<SortField> sort;
}
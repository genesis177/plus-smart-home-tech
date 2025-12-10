package ru.yandex.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.feign.DeliveryClient;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
public class DeliveryController implements DeliveryClient {
    final DeliveryService deliveryService;

    @Override
    public DeliveryDto delivery(DeliveryDto deliveryDto) {
        return deliveryService.delivery(deliveryDto);
    }

    @Override
    public void markAsSuccessful(UUID orderId) {
        deliveryService.markAsSuccessful(orderId);
    }

    @Override
    public void markAsPicked(UUID orderId) {
        deliveryService.markAsPicked(orderId);
    }

    @Override
    public void markAsFailed(UUID orderId) {
        deliveryService.markAsFailed(orderId);
    }

    @Override
    public Double calculateDeliveryCost(OrderDto orderDto) {
        return deliveryService.calculateDeliveryCost(orderDto);
    }
}
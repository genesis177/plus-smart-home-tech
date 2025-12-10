package ru.yandex.practicum.feign;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;

import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryClient {
    @PutMapping
    DeliveryDto delivery(@Valid @RequestBody DeliveryDto deliveryDto) throws FeignException;

    @PostMapping("/successful")
    void markAsSuccessful(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/picked")
    void markAsPicked(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/failed")
    void markAsFailed(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/cost")
    Double calculateDeliveryCost(@Valid @RequestBody OrderDto orderDto) throws FeignException;
}
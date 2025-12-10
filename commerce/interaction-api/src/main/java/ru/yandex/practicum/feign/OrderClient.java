package ru.yandex.practicum.feign;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient {
    @GetMapping
    List<OrderDto> getUserOrders(@RequestParam String username) throws FeignException;

    @PutMapping
    OrderDto createOrder(@Valid @RequestBody CreateNewOrderRequest request) throws FeignException;

    @PostMapping("/return")
    OrderDto productReturn(@Valid @RequestBody ProductReturnRequest request) throws FeignException;

    @PostMapping("/payment")
    OrderDto payment(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/payment/failed")
    OrderDto paymentFailed(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/delivery")
    OrderDto delivery(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/delivery/failed")
    OrderDto deliveryFailed(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/completed")
    OrderDto complete(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/calculate/total")
    OrderDto calculateTotalCost(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryCost(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/assembly")
    OrderDto assembly(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/assembly/failed")
    OrderDto assemblyFailed(@RequestBody UUID orderId) throws FeignException;
}
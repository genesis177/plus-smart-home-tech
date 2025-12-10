package ru.yandex.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.feign.PaymentClient;
import ru.yandex.practicum.service.PaymentService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentClient {
    final PaymentService paymentService;

    @Override
    public PaymentDto createPayment(OrderDto orderDto) {
        return paymentService.createPayment(orderDto);
    }

    @Override
    public Double totalCost(OrderDto orderDto) {
        return paymentService.totalCost(orderDto);
    }

    @Override
    public void processSuccessfulPayment(UUID paymentId) {
        paymentService.processSuccessfulPayment(paymentId);
    }

    @Override
    public Double productCost(OrderDto orderDto) {
        return paymentService.productCost(orderDto);
    }

    @Override
    public void failedPayment(UUID paymentId) {
        paymentService.failedPayment(paymentId);
    }
}
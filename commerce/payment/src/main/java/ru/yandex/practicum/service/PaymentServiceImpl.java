package ru.yandex.practicum.service;

import jakarta.ws.rs.NotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.payment.PaymentState;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.feign.OrderClient;
import ru.yandex.practicum.feign.ShoppingStoreClient;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.repository.PaymentRepository;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentServiceImpl implements PaymentService {
    final OrderClient orderClient;
    final ShoppingStoreClient storeClient;
    final PaymentRepository paymentRepository;
    final PaymentMapper paymentMapper;

    static final Double fee = 0.1;

    @Override
    @Transactional
    public PaymentDto createPayment(OrderDto orderDto) {
        checker(orderDto);
        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .totalPayment(totalCost(orderDto))
                .deliveryTotal(orderDto.getDeliveryPrice())
                .feeTotal(orderDto.getTotalPrice() * fee)
                .paymentStatus(PaymentState.PENDING)
                .build();
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public Double totalCost(OrderDto orderDto) {
        checker(orderDto);
        Payment payment = paymentRepository.findById(orderDto.getPaymentId())
                .orElseThrow(() -> new NotFoundException("Не найден платеж с ID: " + orderDto.getPaymentId()));
        Double totalCost = orderDto.getProductPrice() + orderDto.getProductPrice() * fee + orderDto.getDeliveryPrice();
        payment.setTotalPayment(totalCost);
        paymentRepository.save(payment);
        return totalCost;
    }

    @Override
    @Transactional
    public void processSuccessfulPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Не найден платеж с ID: " + paymentId));
        orderClient.payment(payment.getOrderId());
        payment.setPaymentStatus(PaymentState.SUCCESS);
        paymentRepository.save(payment);
    }

    @Override
    public Double productCost(OrderDto orderDto) {
        Map<UUID, Long> products = orderDto.getProducts();
        Map<UUID, Float> productPrices = products.keySet().stream()
                .map(storeClient::getProductById)
                .collect(Collectors.toMap(ProductDto::getProductId, ProductDto::getPrice));
        return products.entrySet().stream()
                .map(entry -> entry.getValue() * productPrices.get(entry.getKey()))
                .mapToDouble(Float::floatValue)
                .sum();
    }

    @Override
    @Transactional
    public void failedPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Не найден платеж с ID: " + paymentId));
        orderClient.paymentFailed(payment.getOrderId());
        payment.setPaymentStatus(PaymentState.FAILED);
        paymentRepository.save(payment);
    }

    private void checker(OrderDto orderDto) {
        if (orderDto.getTotalPrice() == null)
            throw new NotEnoughInfoInOrderToCalculateException("Нету стоимости заказа");
        if (orderDto.getDeliveryPrice() == null)
            throw new NotEnoughInfoInOrderToCalculateException("Нету стоимости доставки");
        if (orderDto.getProductPrice() == null)
            throw new NotEnoughInfoInOrderToCalculateException("Нету  стоимости товаров");
    }
}
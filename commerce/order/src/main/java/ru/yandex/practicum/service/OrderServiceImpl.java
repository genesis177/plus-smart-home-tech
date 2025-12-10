package ru.yandex.practicum.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.DeliveryState;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.OrderState;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.feign.DeliveryClient;
import ru.yandex.practicum.feign.PaymentClient;
import ru.yandex.practicum.feign.ShoppingCartClient;
import ru.yandex.practicum.feign.WarehouseClient;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderServiceImpl implements OrderService {
    final ShoppingCartClient cartClient;
    final DeliveryClient deliveryClient;
    final PaymentClient paymentClient;
    final WarehouseClient warehouseClient;
    final OrderRepository orderRepository;
    final OrderMapper orderMapper;

    @Override
    public List<OrderDto> getUserOrders(String username) {
        checker(username);
        ShoppingCartDto shoppingCartDto = cartClient.getCart(username);
        return orderRepository.findByShoppingCartId(shoppingCartDto.getShoppingCartId()).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateNewOrderRequest request) {
        Order order = Order.builder()
                .shoppingCartId(request.getShoppingCart().getShoppingCartId())
                .products(request.getShoppingCart().getProducts())
                .state(OrderState.NEW)
                .build();
        orderRepository.save(order);

        // продукты
        AssemblyProductsForOrderRequest assemblyRequest = AssemblyProductsForOrderRequest.builder()
                .orderId(order.getOrderId())
                .products(request.getShoppingCart().getProducts())
                .build();
        BookedProductsDto bookedProductsDto = warehouseClient.assembleProducts(assemblyRequest);

        // доставка
        DeliveryDto deliveryDto = DeliveryDto.builder()
                .orderId(order.getOrderId())
                .fromAddress(warehouseClient.getWarehouseAddress())
                .toAddress(request.getDeliveryAddress())
                .deliveryState(DeliveryState.CREATED)
                .build();
        deliveryDto = deliveryClient.delivery(deliveryDto);

        // платеж
        PaymentDto paymentDto = paymentClient.createPayment(orderMapper.toDto(order));

        order.setPaymentId(paymentDto.getPaymentId());
        order.setDeliveryId(deliveryDto.getDeliveryId());
        order.setDeliveryWeight(bookedProductsDto.getDeliveryWeight());
        order.setDeliveryVolume(bookedProductsDto.getDeliveryVolume());
        order.setFragile(bookedProductsDto.getFragile());
        order.setTotalPrice(paymentClient.totalCost(orderMapper.toDto(order)));
        order.setDeliveryPrice(deliveryClient.calculateDeliveryCost(orderMapper.toDto(order)));
        order.setProductPrice(paymentClient.productCost(orderMapper.toDto(order)));
        orderRepository.save(order);

        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException("Нет заказа с ID: " + request.getOrderId()));
        warehouseClient.acceptReturn(request.getProducts());
        order.setState(OrderState.PRODUCT_RETURNED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public OrderDto payment(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Нет заказа с ID: " + orderId));
        order.setState(OrderState.PAID);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public OrderDto paymentFailed(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        Order order = findOrder(orderId);
        deliveryClient.markAsSuccessful(orderId);
        order.setState(OrderState.DELIVERED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto complete(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.COMPLETED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = findOrder(orderId);
        order.setTotalPrice(paymentClient.totalCost(orderMapper.toDto(order)));
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = findOrder(orderId);
        order.setDeliveryPrice(deliveryClient.calculateDeliveryCost(orderMapper.toDto(order)));
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto assembly(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.ASSEMBLED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = findOrder(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    private void checker(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Нет имени пользователя");
        }
    }

    private Order findOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException("Нет заказа с ID: " + orderId));
    }
}
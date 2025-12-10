package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.DeliveryState;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.feign.OrderClient;
import ru.yandex.practicum.feign.WarehouseClient;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeliveryServiceImpl implements DeliveryService {
    final WarehouseClient warehouseClient;
    final OrderClient orderClient;
    final DeliveryRepository deliveryRepository;
    final DeliveryMapper deliveryMapper;

    static final Double BASE_RATE = 5.0;
    static final Double ADDRESS_RATE = 2.0;
    static final Double STREET_RATE = 0.2;
    static final Double FRAGILE_RATE = 0.2;
    static final Double WEIGHT_RATE = 0.3;
    static final Double VOLUME_RATE = 0.2;

    @Override
    @Transactional
    public DeliveryDto delivery(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryMapper.toEntity(deliveryDto);
        delivery.setDeliveryState(DeliveryState.CREATED);
        return deliveryMapper.toDto(deliveryRepository.save(delivery));
    }

    @Override
    @Transactional
    public void markAsSuccessful(UUID orderId) {
        Delivery delivery = deliveryRepository.findById(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Не найдена доставка с ID: " +  orderId));
        orderClient.complete(delivery.getOrderId());
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public void markAsPicked(UUID orderId) {
        Delivery delivery = deliveryRepository.findById(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Не найдена доставка с ID: " +  orderId));
        orderClient.assembly(delivery.getOrderId());
        ShippedToDeliveryRequest request = ShippedToDeliveryRequest.builder()
                .orderId(delivery.getOrderId())
                .deliveryId(delivery.getDeliveryId())
                .build();
        warehouseClient.shipToDelivery(request);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional
    public void markAsFailed(UUID orderId) {
        Delivery delivery = deliveryRepository.findById(orderId)
                .orElseThrow(() -> new NoDeliveryFoundException("Не найдена доставка с ID: " +  orderId));
        orderClient.deliveryFailed(delivery.getOrderId());
        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateDeliveryCost(OrderDto orderDto) {
        double cost = BASE_RATE;
        Delivery delivery = deliveryRepository.findById(orderDto.getDeliveryId())
                .orElseThrow(() -> new NoDeliveryFoundException("Не найдена доставка с ID: " + orderDto.getDeliveryId()));
        AddressDto warehouseAddress = warehouseClient.getWarehouseAddress();
        String street = warehouseAddress.getStreet();

        if ("ADDRESS_1".equals(street)) {
            cost *= 2.0;
        } else if ("ADDRESS_2".equals(street)) {
            cost *= (1 + ADDRESS_RATE);
        }

        if (orderDto.getFragile()) {
            cost *= (1 + FRAGILE_RATE);
        }
        cost += orderDto.getDeliveryWeight() * WEIGHT_RATE;
        cost += orderDto.getDeliveryVolume() * VOLUME_RATE;
        if (delivery.getToAddress().getStreet().equals(street)) {
            cost *= (1 + STREET_RATE);
        }
        return cost;
    }
}
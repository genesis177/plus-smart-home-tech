package ru.yandex.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.address.Address;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.Dimension;
import ru.yandex.practicum.model.Warehouse;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
@Slf4j
public class WarehouseServiceImpl implements WarehouseService{
    final WarehouseRepository warehouseRepository;
    final WarehouseMapper warehouseMapper;

    @Override
    public AddressDto getWarehouseAddress() {
        String address = Address.CURRENT_ADDRESS;
        return AddressDto.builder()
                .country(address)
                .city(address)
                .street(address)
                .house(address)
                .flat(address)
                .build();
    }

    @Override
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        Warehouse product = warehouseRepository.findById(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("На складе такого товара с ID: "
                        + request.getProductId() + " не существует. Сначала создайте его!"));
        product.setQuantity(product.getQuantity() + request.getQuantity());
        warehouseRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public BookedProductsDto checkProductQuantity(ShoppingCartDto shoppingCart) {
        boolean hasFragileProduct = false;
        double totalDeliveryWeight = 0.0;
        double totalDeliveryVolume = 0.0;

        for (Map.Entry<UUID, Long> cartEntry : shoppingCart.getProducts().entrySet()) {
            UUID productId = cartEntry.getKey();
            long quantityRequested = cartEntry.getValue();

            Warehouse warehouseProduct = warehouseRepository.findById(productId)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("На складе такого товара с ID: "
                            + productId + " не существует. Сначала создайте его!"));

            if (warehouseProduct.getQuantity() < quantityRequested) {
                throw new ProductNotFoundException(
                        "Не хватает на складе товара с id = " + productId
                );
            }

            if (warehouseProduct.isFragile()) {
                hasFragileProduct = true;
            }

            double productVolume = calculated(warehouseProduct.getDimension());
            double totalVolumeForProduct = productVolume * quantityRequested;
            double totalWeightForProduct = warehouseProduct.getWeight() * quantityRequested;

            totalDeliveryVolume += totalVolumeForProduct;
            totalDeliveryWeight += totalWeightForProduct;
        }

        return BookedProductsDto.builder()
                .fragile(hasFragileProduct)
                .deliveryVolume(totalDeliveryVolume)
                .deliveryWeight(totalDeliveryWeight)
                .build();
    }

    private double calculated(Dimension dimension) {
        return dimension.getWidth() * dimension.getHeight() * dimension.getDepth();
    }

    @Override
    @Transactional
    public void createNewProductInWarehouse(NewProductInWarehouseRequest request) {
        if (warehouseRepository.existsById(request.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException(
                    "Товар уже есть на складе с ID: " + request.getProductId());
        }
        warehouseRepository.save(warehouseMapper.toEntity(request));
    }
}

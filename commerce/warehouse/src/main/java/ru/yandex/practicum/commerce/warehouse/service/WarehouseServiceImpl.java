package ru.yandex.practicum.commerce.warehouse.service;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.warehouse.mapper.ProductMapper;
import ru.yandex.practicum.commerce.warehouse.model.Dimension;
import ru.yandex.practicum.commerce.warehouse.model.Product;
import ru.yandex.practicum.commerce.warehouse.repository.ProductRepository;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {

    private final ProductRepository productRepository;
    private final WarehouseAddressService addressService;

    /**
     * Добавляет новый продукт на склад.
     *
     * @param request данные нового продукта
     */
    @Transactional
    @Override
    public void addNewProduct(final NewProductInWarehouseRequest request) {
        log.debug("Добавление нового продукта на склад: {}", request);
        validateProductIsNew(request.getProductId());
        final Product product = ProductMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        log.debug("Продукт добавлен на склад: ID:{}, количество:{} ",
                savedProduct.getProductId(), savedProduct.getQuantity());
    }

    /**
     * Проверяет наличие товаров на складе для продуктов из корзины покупок.
     *
     * @param shoppingCart данные корзины покупок
     * @return информацию о забронированных продуктах
     */
    @Transactional
    @Override
    public BookedProductsDto checkStock(final ShoppingCartDto shoppingCart) {
        log.debug("Проверка наличия товаров для корзины покупок {}.", shoppingCart);
        validateCartNotEmpty(shoppingCart);

        final Map<UUID, Long> cartProducts = shoppingCart.getProducts();

        final Map<UUID, Product> stockProducts = productRepository.findAllById(cartProducts.keySet())
                .stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));
        validateProductsExistedInWarehouse(cartProducts.keySet(), stockProducts.keySet());
        validateProductQuantity(cartProducts, stockProducts);
        return calculateBookedProducts(cartProducts, stockProducts);
    }

    /**
     * Увеличивает количество продукта на складе.
     *
     * @param request данные запроса на увеличение количества
     */
    @Transactional
    @Override
    public void increaseProductQuantity(final AddProductToWarehouseRequest request) {
        log.debug("Увеличение количества продукта: {} на: {}.",
                request.getProductId(), request.getQuantity());

        final Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException(
                        "productID: " + request.getProductId()));
        product.setQuantity(product.getQuantity() + request.getQuantity());

        final Product updatedProduct = productRepository.save(product);
        log.debug("Обновленный продукт: ID={}, количество={}.", updatedProduct.getProductId(),
                updatedProduct.getQuantity());
    }

    /**
     * Получает адрес склада.
     *
     * @return адрес склада
     */
    @Transactional(readOnly = true)
    @Override
    public AddressDto getAddress() {
        return addressService.getAddress();
    }

    /**
     * Рассчитывает общие характеристики для забронированных продуктов (вес, объем и хрупкость).
     *
     * @param cartProducts  товары из корзины
     * @param stockProducts товары на складе
     * @return данные о забронированных продуктах
     */
    private BookedProductsDto calculateBookedProducts(final Map<UUID, Long> cartProducts,
                                                      final Map<UUID, Product> stockProducts) {
        log.debug(
                "Расчет общих характеристик для товаров в корзине: {}. Характеристики из склада: {}",
                cartProducts, stockProducts);
        double totalWeight = 0;
        double totalVolume = 0;
        boolean fragile = false;

        for (Map.Entry<UUID, Long> entry : cartProducts.entrySet()) {
            UUID productId = entry.getKey();
            long quantity = entry.getValue();
            Product product = stockProducts.get(productId);
            totalWeight += product.getWeight() * quantity;
            Dimension dim = product.getDimension();
            totalVolume += dim.getWidth() * dim.getHeight() * dim.getDepth() * quantity;

            if (product.isFragile()) {
                fragile = true;
            }
        }
        log.debug("Общий вес: {}, общий объем: {}, хрупкость: {}", totalWeight, totalVolume, fragile);
        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(fragile)
                .build();
    }

    /**
     * Проверяет наличие достаточного количества продуктов на складе.
     *
     * @param cartProducts  товары из корзины
     * @param stockProducts товары на складе
     */
    private void validateProductQuantity(final Map<UUID, Long> cartProducts,
                                         final Map<UUID, Product> stockProducts) {

        Map<UUID, Long> stock = stockProducts.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getQuantity()
                ));

        log.debug(
                "Проверка наличия всех требуемых продуктов на складе; требуемое: {}, склад: {}.",
                cartProducts, stock);

        final Set<UUID> shortProducts = stockProducts.entrySet().stream()
                .filter(entry -> entry.getValue().getQuantity() < cartProducts.get(entry.getKey()))
                .map(Entry::getKey)
                .collect(Collectors.toSet());

        if (!shortProducts.isEmpty()) {
            log.warn("Проверка количества продуктов не прошла, на складе не хватает: {} ",
                    shortProducts);
            throw new ProductInShoppingCartLowQuantityInWarehouse(
                    "Не хватает продуктов: " + shortProducts);
        }
    }

    /**
     * Проверяет, существуют ли все требуемые продукты на складе.
     *
     * @param required требуемые продукты
     * @param stock    имеющиеся продукты на складе
     */
    private void validateProductsExistedInWarehouse(final Set<UUID> required, final Set<UUID> stock) {
        log.debug(
                "Проверка, что все требуемые продукты есть на складе; требуемое: {}, на складе: {}",
                required, stock);

        final Set<UUID> missingProducts = required.stream()
                .filter(uuid -> !stock.contains(uuid))
                .collect(Collectors.toSet());

        if (!missingProducts.isEmpty()) {
            log.warn("Проверка наличия товаров на складе не прошла, отсутствуют продукты: {}.",
                    missingProducts);
            throw new NoSpecifiedProductInWarehouseException("Отсутствуют продукты: " + missingProducts);
        }
        log.debug("Успех: все требуемые продукты зарегистрированы на складе.");
    }

    /**
     * Проверяет, не существует ли уже продукт на складе.
     *
     * @param productId ID продукта
     */
    private void validateProductIsNew(final UUID productId) {
        log.debug("Проверка, что продукт с ID {} еще не существует на складе.", productId);
        if (productRepository.existsById(productId)) {
            throw new SpecifiedProductAlreadyInWarehouseException("productID: " + productId);
        }
    }

    /**
     * Проверяет, что корзина покупок не пуста.
     *
     * @param shoppingCart корзина покупок
     */
    private void validateCartNotEmpty(final ShoppingCartDto shoppingCart) {
        log.debug("Проверка, что корзина покупок содержит товары для проверки: {}", shoppingCart);
        if (shoppingCart == null ||
                shoppingCart.getProducts() == null ||
                shoppingCart.getProducts().isEmpty()
        ) {
            throw new NoProductsInShoppingCartException("Корзина покупок пуста: " + shoppingCart);
        }
    }
}

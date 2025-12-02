package ru.yandex.practicum.commerce.store.service;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.store.mapper.ProductMapper;
import ru.yandex.practicum.commerce.store.model.Product;
import ru.yandex.practicum.commerce.store.repository.ProductRepository;
import ru.yandex.practicum.commerce.store.utility.UuidGenerator;
import ru.yandex.practicum.dto.product.ProductCategory;
import ru.yandex.practicum.dto.product.ProductDto;
import ru.yandex.practicum.dto.product.ProductState;
import ru.yandex.practicum.dto.product.SetProductQuantityStateRequest;
import ru.yandex.practicum.exception.ProductNotFoundException;

/**
 * Реализация {@link ShoppingStoreService} для управления продуктами в магазине.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    private final ProductRepository productRepository;
    private final UuidGenerator uuidGenerator;

    /**
     * Получает список продуктов для указанной категории с пагинацией.
     *
     * @param category категория продукта
     * @param pageable параметры пагинации
     * @return страница с продуктами
     */
    @Transactional(readOnly = true)
    @Override
    public Page<ProductDto> getProductsByCategory(final ProductCategory category,
                                                  final Pageable pageable) {
        log.debug("Получение продуктов для категории: {}, страница: {}, размер: {}, сортировка: {}...",
                category, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        final PageRequest page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                pageable.getSort());
        final Page<Product> products = productRepository.findAllByProductCategory(category, page);
        return products.map(ProductMapper::toDto);
    }

    /**
     * Получает информацию о продукте по его ID.
     *
     * @param productId ID продукта
     * @return информация о продукте
     */
    @Transactional(readOnly = true)
    @Override
    public ProductDto getProductById(final UUID productId) {
        log.debug("Получение данных о продукте с ID {}...", productId);
        final Product product = getProductOrThrow(productId);
        log.debug("Успешно получен продукт: {}", product);
        return ProductMapper.toDto(product);
    }

    /**
     * Добавляет новый продукт в магазин.
     *
     * @param productDto данные о продукте
     * @return добавленный продукт
     */
    @Transactional
    @Override
    public ProductDto addProduct(final ProductDto productDto) {
        log.debug("Сохранение новой информации о продукте в базе данных: {}...", productDto);
        setId(productDto);
        final Product productToSave = ProductMapper.toEntity(productDto);
        final Product savedProduct = productRepository.save(productToSave);
        log.debug("Новый продукт успешно сохранен с ID: {}.", savedProduct.getProductId());
        return ProductMapper.toDto(savedProduct);
    }

    /**
     * Обновляет информацию о продукте.
     *
     * @param productDto обновленные данные о продукте
     * @return обновленный продукт
     */
    @Transactional
    @Override
    public ProductDto updateProduct(final ProductDto productDto) {
        log.debug("Обновление информации о продукте с деталями: {}...", productDto);
        final Product product = getProductOrThrow(productDto.getProductId());
        updateProductContent(product, productDto);
        final Product updatedProduct = productRepository.save(product);
        log.debug("Обновленный продукт: {}.", updatedProduct);
        return ProductMapper.toDto(updatedProduct);
    }

    /**
     * Обновляет состояние количества продукта.
     *
     * @param request запрос на обновление состояния количества
     * @return true, если обновление прошло успешно
     */
    @Transactional
    @Override
    public boolean updateQuantityState(final SetProductQuantityStateRequest request) {
        log.debug("Обновление состояния количества для продукта с ID {} - {}.",
                request.getProductId(), request.getQuantityState());
        final Product product = getProductOrThrow(request.getProductId());
        product.setQuantityState(request.getQuantityState());
        final Product updatedProduct = productRepository.save(product);
        log.debug("Обновлено состояние количества для продукта с ID {} - {}.",
                updatedProduct.getProductId(), updatedProduct.getQuantityState());
        return true;
    }

    /**
     * Удаляет продукт из ассортимента, изменяя его состояние на 'DEACTIVATE'.
     *
     * @param productId ID продукта для удаления
     * @return true, если продукт был успешно удален
     */
    @Transactional
    @Override
    public boolean removeProduct(final UUID productId) {
        log.debug("Изменение состояния продукта с ID {} на 'DEACTIVATE'.", productId);
        final Product product = getProductOrThrow(productId);
        product.setProductState(ProductState.DEACTIVATE);
        final Product updatedProduct = productRepository.save(product);
        log.debug("Обновлено состояние продукта: {}.", updatedProduct);
        return true;
    }

    /**
     * Обновляет содержимое продукта на основе данных из DTO.
     */
    private void updateProductContent(final Product target, final ProductDto source) {
        target.setProductName(
                source.getProductName() != null ? source.getProductName() : target.getProductName());
        target.setDescription(
                source.getDescription() != null ? source.getDescription() : target.getDescription());
        target.setImageSrc(source.getImageSrc() != null ? source.getImageSrc() : target.getImageSrc());
        target.setQuantityState(
                source.getQuantityState() != null ? source.getQuantityState() : target.getQuantityState());
        target.setProductState(
                source.getProductState() != null ? source.getProductState() : target.getProductState());
        target.setProductCategory(source.getProductCategory() != null ? source.getProductCategory()
                : target.getProductCategory());
        target.setPrice(source.getPrice() != null ? source.getPrice() : target.getPrice());
    }

    /**
     * Устанавливает уникальный ID для продукта, если он не был задан.
     */
    private void setId(final ProductDto productDto) {
        if (productDto.getProductId() == null) {
            productDto.setProductId(uuidGenerator.generate());
        }
    }

    /**
     * Находит продукт по ID или выбрасывает исключение, если продукт не найден.
     */
    private Product getProductOrThrow(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("ID: " + productId));
    }
}

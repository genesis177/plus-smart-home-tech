package ru.yandex.practicum.service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.ProductsResponseList;
import ru.yandex.practicum.dto.store.SortField;
import ru.yandex.practicum.dto.store.enums.ProductCategory;
import ru.yandex.practicum.dto.store.enums.ProductState;
import ru.yandex.practicum.dto.store.updateStockLevelStateRequest;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ShoppingStoreMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ShoppingStoreRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreServiceImpl implements StoreService {
    final ShoppingStoreRepository shoppingStoreRepository;
    final ShoppingStoreMapper shoppingStoreMapper;

    @Override
    public ProductDto getProductById(UUID productId) {
        return shoppingStoreMapper.toDto(shoppingStoreRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with id = " + productId + "not found")));
    }

    @Override
    public ProductsResponseList getProductsByCategory(ProductCategory category, Pageable pageable) {
        List<ProductDto> productDtos = shoppingStoreRepository
                .findAllByProductCategoryAndProductState(category, ProductState.ACTIVE, pageable)
                .stream()
                .map(shoppingStoreMapper::toDto)
                .toList();

        List<SortField> sortFields = pageable.getSort()
                .stream()
                .map(order -> new SortField(order.getProperty(), order.getDirection().name()))
                .toList();

        return ProductsResponseList.builder()
                .content(productDtos)
                .sort(sortFields)
                .build();
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        Product product = shoppingStoreMapper.toEntity(productDto);
        return shoppingStoreMapper.toDto(shoppingStoreRepository.save(product));
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        getProductById(productDto.getProductId());
        Product product = shoppingStoreMapper.toEntity(productDto);
        return shoppingStoreMapper.toDto(shoppingStoreRepository.save(product));
    }

    @Override
    public Boolean removeProduct(UUID productId) {
        Product product = shoppingStoreMapper.toEntity(getProductById(productId));
        if (product.getProductState().equals(ProductState.DEACTIVATE)) {
            return false;
        }
        product.setProductState(ProductState.DEACTIVATE);
        shoppingStoreRepository.save(product);
        return true;
    }

    public Boolean updateStockLevelState(updateStockLevelStateRequest request) {
        Product product = shoppingStoreMapper.toEntity(getProductById(request.getProductId()));
        log.info("Имя товара: {}, обновленное наличие: {}", product.getProductName(), product.getQuantityState());
        product.setQuantityState(request.getQuantityState());
        shoppingStoreRepository.save(product);
        return true;
    }
}

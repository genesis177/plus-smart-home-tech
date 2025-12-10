package ru.yandex.practicum.service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    final ShoppingCartRepository shoppingCartRepository;
    final ShoppingCartMapper shoppingCartMapper;

    @Override
    @Transactional
    public ShoppingCartDto addProductToCart(String username, Map<UUID, Long> products) {
        checker(username);

        Optional<ShoppingCart> optionalCart = shoppingCartRepository.findByUsername(username);
        ShoppingCart cart;

        if (optionalCart.isPresent()) {
            cart = optionalCart.get();
            products.forEach((productId, quantity) ->
                    cart.getProducts().merge(productId, quantity, Long::sum)
            );
        } else {
            cart = ShoppingCart.builder()
                    .username(username)
                    .products(products)
                    .isActive(true)
                    .build();
        }
        return shoppingCartMapper.toDto(shoppingCartRepository.save(cart));
    }

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getCart(String username) {
        checker(username);
        return shoppingCartRepository.findByUsername(username)
                .map(shoppingCartMapper::toDto)
                .orElseGet(() -> createNewCartDto(username));
    }

    @Override
    @Transactional
    public ShoppingCartDto removeFromCart(String username, Set<UUID> productIds) {
        checker(username);
        ShoppingCart cart = shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> new NoProductsInShoppingCartException("Корзина пользовател не найдена. Имя пользователя: " +
                        username));
        for (UUID productId : productIds) {
            if (!cart.getProducts().containsKey(productId)) {
                throw new NoProductsInShoppingCartException("Товар = " + productId + " не найден в корзине");
            }
            cart.getProducts().remove(productId);
        }
        return shoppingCartMapper.toDto(shoppingCartRepository.save(cart));
    }

    @Override
    @Transactional
    public void deleteUserCart(String username) {
        checker(username);
        shoppingCartRepository.findByUsername(username)
                .ifPresent(cart -> {
                    cart.setIsActive(false);
                    shoppingCartRepository.save(cart);
                });
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        checker(username);
        ShoppingCart cart = shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> new NoProductsInShoppingCartException("Корзина пользователя не найдена. Имя пользователя: " +
                        username));
        if (!cart.getProducts().containsKey(request.getProductId())) {
            throw new NoProductsInShoppingCartException("Товар = " + request.getProductId() + " не найден в корзине");
        }
        cart.getProducts().put(request.getProductId(), request.getNewQuantity());
        return shoppingCartMapper.toDto(shoppingCartRepository.save(cart));
    }

    private void checker(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть null или пустым");
        }
    }

    private ShoppingCartDto createNewCartDto(String username) {
        ShoppingCart newCart = createNewCart(username);
        return shoppingCartMapper.toDto(newCart);
    }

    private ShoppingCart createNewCart(String username) {
        ShoppingCart newCart = ShoppingCart.builder()
                .username(username)
                .isActive(true)
                .build();
        return shoppingCartRepository.save(newCart);
    }
}

package ru.yandex.practicum.commerce.cart.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.commerce.cart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.commerce.cart.model.ShoppingCart;
import ru.yandex.practicum.commerce.cart.model.ShoppingCartState;
import ru.yandex.practicum.commerce.cart.repository.ShoppingCartRepository;
import ru.yandex.practicum.commerce.cart.utility.UuidGenerator;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.exception.ShoppingCartModificationException;
import ru.yandex.practicum.feign.WarehouseOperations;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository cartRepository;
    private final WarehouseOperations warehouseClient;
    private final UuidGenerator uuidGenerator;

    @Transactional
    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        validateUser(username);
        ShoppingCart cart = getOrCreateShoppingCart(username);
        return ShoppingCartMapper.toDto(cart);
    }

    @Transactional
    @Override
    public ShoppingCartDto addProductsToCart(String username, Map<UUID, Long> products) {
        validateUser(username);
        ShoppingCart cart = getOrCreateShoppingCart(username);
        validateCartIsActive(cart);

        products.forEach((k, v) -> cart.getProducts().merge(k, v, Long::sum));

        cartRepository.save(cart);

        ShoppingCartDto cartDto = ShoppingCartMapper.toDto(cart);
        validateAllProductsAvailable(cartDto);

        return cartDto;
    }

    @Transactional
    @Override
    public void deactivateShoppingCart(String username) {
        validateUser(username);
        ShoppingCart cart = getOrCreateShoppingCart(username);
        if (cart.getCartState() == ShoppingCartState.DEACTIVATED) return;

        cart.setCartState(ShoppingCartState.DEACTIVATED);
        cartRepository.save(cart);
    }

    @Transactional
    @Override
    public ShoppingCartDto retainProductsInTheCart(String username, Set<UUID> products) {
        validateUser(username);
        ShoppingCart cart = getOrCreateShoppingCart(username);
        validateCartIsActive(cart);
        validateProductsInTheCart(cart, products);

        cart.getProducts().keySet().retainAll(products);
        cartRepository.save(cart);

        return ShoppingCartMapper.toDto(cart);
    }

    @Transactional
    @Override
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        validateUser(username);
        ShoppingCart cart = getOrCreateShoppingCart(username);
        validateCartIsActive(cart);

        if (!cart.getProducts().containsKey(request.getProductId())) {
            throw new NoProductsInShoppingCartException(
                    "No such Product in the cart - " + request.getProductId()
            );
        }

        cart.getProducts().put(request.getProductId(), request.getNewQuantity());
        cartRepository.save(cart);

        return ShoppingCartMapper.toDto(cart);
    }

    // ---------------- Private helpers ----------------

    private void validateAllProductsAvailable(ShoppingCartDto cartDto) {
        warehouseClient.checkStock(cartDto);
    }

    private void validateUser(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Invalid username: " + username);
        }
    }

    private ShoppingCart getOrCreateShoppingCart(String username) {
        return cartRepository.findByUsername(username).orElseGet(() -> createShoppingCart(username));
    }

    private ShoppingCart createShoppingCart(String username) {
        ShoppingCart cart = ShoppingCart.builder()
                .cartId(uuidGenerator.generate())
                .username(username)
                .cartState(ShoppingCartState.ACTIVE)
                .products(new HashMap<>())
                .build();
        return cartRepository.save(cart);
    }

    private void validateCartIsActive(ShoppingCart cart) {
        if (cart.getCartState() == ShoppingCartState.DEACTIVATED) {
            throw new ShoppingCartModificationException("Cart is deactivated");
        }
    }

    private void validateProductsInTheCart(ShoppingCart cart, Set<UUID> products) {
        if (cart.getProducts().isEmpty() || !cart.getProducts().keySet().containsAll(products)) {
            throw new NoProductsInShoppingCartException("Shopping cart content: " + cart.getProducts());
        }
    }
}

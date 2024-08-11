package sia.tcloud3.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sia.tcloud3.entity.Cart;
import sia.tcloud3.entity.CartItem;
import sia.tcloud3.entity.Taco;
import sia.tcloud3.repositories.CartItemRepository;
import sia.tcloud3.repositories.CartRepository;
import sia.tcloud3.repositories.TacoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final TacoRepository tacoRepository;
    private final UserService userService;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository, TacoRepository tacoRepository, UserService userService, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.tacoRepository = tacoRepository;
        this.userService = userService;
        this.cartItemRepository = cartItemRepository;
    }

    public Cart getCart() {
        Long userId =  userService.retrieveCurrentUser().getId();
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() ->
            Cart.builder().userId(userId).cartItems(new ArrayList<>()).build());
        if (cart.getId() == null)
            cartRepository.save(cart);
//        log.info("Taco in cart {}", Objects.requireNonNull(cart.getCartItems().stream().findFirst().orElse(null)).getTaco().getName());
        return cart;
    }

    public CartItem getCartItem(Long id) {
        List<CartItem> cartItems = getCart().getCartItems();
        return cartItems.stream().filter(cartItem -> Objects.equals(cartItem.getId(), id)).findFirst().orElse(null);
    }

    public Cart addToCart(Long id) {
        Cart cart = getCart();
        CartItem cartItem = new CartItem();
        Taco taco = tacoRepository.findById(id).orElseThrow(() -> new NullPointerException("Taco with id " + id +
                " could not be found."));
        log.info("Taco is null? {}", taco == null);
        log.info("Taco content: {}", taco);
        cartItem.setTaco(taco);
        log.info("now Cart.set taco returns : {}", cartItem.getTaco());
        List<CartItem> cartItems = new ArrayList<>(cart.getCartItems());
        cartItems.add(cartItem);
        cartItemRepository.save(cartItem);
        cart.setCartItems(cartItems);
        log.info("The items in this cart are: {}", cart.getCartItems()); // remove this later

        cartRepository.save(cart);
        return cart;
    }

    @Transactional
    public Cart deleteCartItem(Long id) {
        Cart cart = getCart();
        List<CartItem> cartItems = cart.getCartItems();
        cartItems.stream().filter(cartItem -> Objects.equals(cartItem.getId(), id)).findFirst().ifPresent(cartItems::remove);
        cartItemRepository.deleteById(id);
        cartRepository.save(cart);
        return cart;
    }

    @Transactional
    public Cart deleteAll() {
        Cart cart = getCart();
        cart.setCartItems(new ArrayList<>());
        return cart;
    }
}

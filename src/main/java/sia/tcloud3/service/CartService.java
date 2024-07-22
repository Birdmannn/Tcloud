package sia.tcloud3.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import sia.tcloud3.entity.Cart;
import sia.tcloud3.entity.CartItem;
import sia.tcloud3.repositories.CartRepository;
import sia.tcloud3.repositories.TacoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final TacoRepository tacoRepository;
    private final UserService userService;

    public CartService(CartRepository cartRepository, TacoRepository tacoRepository, UserService userService) {
        this.cartRepository = cartRepository;
        this.tacoRepository = tacoRepository;
        this.userService = userService;
    }

    public Cart getCart() {
        Long userId =  userService.retrieveCurrentUser().getId();
        return cartRepository.findByUserId(userId).orElseGet(() ->
            Cart.builder().userId(userId).cartItems(new ArrayList<>()).build());
    }

    public CartItem getCartItem(Long id) {
        List<CartItem> cartItems = getCart().getCartItems();
        return cartItems.stream().filter(cartItem -> Objects.equals(cartItem.getId(), id)).findFirst().orElse(null);
    }

    public Cart addToCart(Long id) {
        Cart cart = getCart();
        CartItem cartItem = new CartItem();
        tacoRepository.findById(id).ifPresent(cartItem::setTaco);
        cart.addItem(cartItem);
        cartRepository.save(cart);
        return cart;
    }

    @Transactional
    public Cart deleteCartItem(Long id) {
        Cart cart = getCart();
        List<CartItem> cartItems = cart.getCartItems();
        cartItems.stream().filter(cartItem -> Objects.equals(cartItem.getId(), id)).findFirst().ifPresent(cartItems::remove);
        return cart;
    }

    @Transactional
    public Cart deleteAll() {
        Cart cart = getCart();
        cart.setCartItems(new ArrayList<>());
        return cart;
    }
}

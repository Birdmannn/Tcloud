package sia.tcloud3.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.tcloud3.dtos.requests.DesignRequest;
import sia.tcloud3.entity.Cart;
import sia.tcloud3.entity.CartItem;
import sia.tcloud3.entity.Taco;
import sia.tcloud3.service.CartService;

@Slf4j
@RestController
@RequestMapping("cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<Cart> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @GetMapping("{id}")
    public ResponseEntity<CartItem> getTacoInCart(@PathVariable Long id) {
        CartItem cartItem = cartService.getCartItem(id);
        if (cartItem == null)
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(cartItem);
    }

    @PutMapping("{id}")
    public ResponseEntity<Cart> addToCart(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.addToCart(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Cart> deleteItem(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.deleteCartItem(id));
    }

    @DeleteMapping
    public ResponseEntity<Cart> deleteAllItems() {
        return ResponseEntity.ok(cartService.deleteAll());
    }

}

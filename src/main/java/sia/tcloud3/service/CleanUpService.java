package sia.tcloud3.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sia.tcloud3.entity.Users;
import sia.tcloud3.repositories.CartRepository;
import sia.tcloud3.repositories.OrderRepository;
import sia.tcloud3.repositories.UserRepository;

@Service
public class CleanUpService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    public CleanUpService(CartRepository cartRepository, OrderRepository orderRepository) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    public void cleanUp(Long userId) {
        cartRepository.deleteByUserId(userId);
        orderRepository.deleteAllByUserId(userId);
    }
}

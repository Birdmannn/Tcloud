package sia.tcloud3.service;

import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import sia.tcloud3.dtos.requests.OrderRequest;
import sia.tcloud3.dtos.response.OrderResponse;
import sia.tcloud3.entity.Taco;
import sia.tcloud3.entity.TacoOrder;
import sia.tcloud3.mapper.OrderMapper;
import sia.tcloud3.repositories.OrderRepository;
import sia.tcloud3.repositories.TacoRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class OrderService {

    // TODO: To post a new Order, prepopulate
    //  check if the user has placed an order before, then prepopulate and return as Json, no?
    //  or it's just from the client, local storage? or can be saved to this database which I don't
    //  wish to. Perhaps the user has changed his/her location, yo.

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final TacoRepository tacoRepository;
    private final UserService userService;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, TacoRepository tacoRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.tacoRepository = tacoRepository;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<TacoOrder> getAllOrders(String sort, int page, int size) {
        return (List<TacoOrder>) orderRepository.findAll();
    }

    //This method should throw an error if the order could not be created.
    public OrderResponse createOrder(OrderRequest orderRequest) {
        TacoOrder order = orderMapper.toTacoOrder(orderRequest);
        int testAddition = 5000;
        BigDecimal orderCost = new BigDecimal(testAddition);

        List<Long> tacoIds = order.getTacoIds();
        List<String> tacoNames = new ArrayList<>();
        for (Long tacoId : tacoIds) {
            Taco taco = tacoRepository.findById(tacoId).orElseThrow(() -> new OrderCreationError("Error in creating order"));
            tacoNames.add(taco.getName());
            orderCost = orderCost.add(taco.getCost());
        }

        order.setPlacedAt(Instant.now());
        order.setCost(orderCost);
        order.setUserId(retrieveId());
        orderRepository.save(order);

        return OrderResponse.builder()
                .message("This is your order and it's net cost.")
                .orderId(order.getId())
                .createdAt(order.getPlacedAt())
                .tacoNames(tacoNames)
                .cost(orderCost)
                .build();
    }

    private Long retrieveId() {
        return userService.retrieveCurrentUser().getId();
    }

    public List<TacoOrder> deleteOrderById(Long id, boolean admin) {
        List<TacoOrder> userOrders = null;
        TacoOrder order = null;
        if (! admin) {
            userOrders = orderRepository.findAllByUserId(retrieveId());
            order = orderRepository.findById(id).orElse(null);
        }
        if (((order != null) && Objects.equals(order.getUserId(), retrieveId())) || admin) {
            try {
                orderRepository.deleteById(id);
            } catch (Exception e) {
                throw new OrderCreationError("Could not delete order fom repository");
            }
        }

        return userOrders;
    }

    public TacoOrder getOrderById(Long id) {

    }


    // ---------------------------------------- Error Class ---------------------------------------------------------

    public static class OrderCreationError extends RuntimeException {
        OrderCreationError(String message) {
            super(message);
        }
    }
}

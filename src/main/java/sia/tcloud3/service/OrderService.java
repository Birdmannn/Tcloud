package sia.tcloud3.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import sia.tcloud3.dtos.requests.OrderRequest;
import sia.tcloud3.dtos.response.OrderResponse;
import sia.tcloud3.entity.Taco;
import sia.tcloud3.entity.TacoOrder;
import sia.tcloud3.mapper.OrderMapper;
import sia.tcloud3.repositories.OrderRepository;
import sia.tcloud3.repositories.TacoRepository;
import sia.tcloud3.service.params.ParamsOrganizer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, TacoRepository tacoRepository,
                        UserService userService) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.tacoRepository = tacoRepository;
        this.userService = userService;
    }

    public List<TacoOrder> getAllOrders(String sort, int page, int size, boolean admin) {
        Pageable pageable = ParamsOrganizer.organizePage(sort, page, size);
        Page<TacoOrder> tacoOrdersPage = orderRepository.findAll(pageable);
        List<TacoOrder> tacoOrders = tacoOrdersPage.getContent();
        List<TacoOrder> userOrders = new ArrayList<>();

        // TODO: Put this admin issh into the Pageable method
        //   so it can page the user's orders if it surpasses a page, or not
        if (! admin) {
            Long userId = userService.retrieveCurrentUser().getId();
            for (TacoOrder order : tacoOrders)
                if (Objects.equals(order.getUserId(), userId))
                    userOrders.add(order);
            return userOrders;
        }
        return tacoOrders;
    }

    //This method should throw an error if the order could not be created.
    public OrderResponse createOrder(OrderRequest orderRequest) {
        TacoOrder order = orderMapper.toTacoOrder(orderRequest);
        int testAddition = 5000;
        BigDecimal orderCost = new BigDecimal(testAddition);

        List<Long> tacoIds = order.getTacoIds();

        for (Long tacoId : tacoIds) {
            Taco taco = tacoRepository.findById(tacoId).orElseThrow(() -> new OrderServiceException("Error in creating order."));
            orderCost = orderCost.add(taco.getCost());
        }

        order.setPlacedAt(Instant.now());
        order.setCost(orderCost);
        order.setStatus("pending");
        order.setUserId(retrieveId());
        orderRepository.save(order);

        return convertToOrderResponse(order);
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
                throw new OrderServiceException("Could not delete order from repository");
            }
        }

        return userOrders;
    }

    public TacoOrder getOrderById(Long id, boolean admin) {
        Optional<TacoOrder> order;
        order = orderRepository.findById(id);
        if (! admin && order.isPresent())
            if (! Objects.equals(order.get().getUserId(), retrieveId()))
                return null;
        return order.orElse(null);
    }

    // Migrate this to the OrderMapper ???
    public OrderResponse convertToOrderResponse(TacoOrder order) {
        List<Long> tacoIds = order.getTacoIds();
        List<String> tacoNames = tacoIds.stream().map(tacoId -> tacoRepository.findById(tacoId).orElseThrow(
                () -> new OrderServiceException("Error in converting order"))).map(Taco::getName).collect(Collectors.toList());
        return OrderResponse.builder()
                .message("This is your order and it's net cost.")
                .orderId(order.getId())
                .createdAt(order.getPlacedAt())
                .tacoNames(tacoNames)
                .cost(order.getCost())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<TacoOrder> findBySearchText(String searchText) {
        return orderRepository.findBySearchText(searchText);
        // Now here, you may filter by the status of the order
    }

    // ---------------------------------------- Error Class ---------------------------------------------------------

    public static class OrderServiceException extends RuntimeException {
        OrderServiceException(String message) {
            super(message);
        }
    }
}

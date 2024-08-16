package sia.tcloud3.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.tcloud3.dtos.requests.OrderRequest;
import sia.tcloud3.dtos.response.OrderResponse;
import sia.tcloud3.entity.TacoOrder;
import sia.tcloud3.service.OrderService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("orders")
public class OrderController {

    // TODO: For users, just post the order. well you can get this order by user id
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping()
    public ResponseEntity<List<TacoOrder>> getAllOrders(@RequestParam(value = "sort", defaultValue = "placedAt") String sort,
                                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        List<TacoOrder> orders = orderService.getAllOrders(sort, page, size, true);
        return orders != null ? ResponseEntity.ok(orders) : ResponseEntity.notFound().build();
    }

    @GetMapping("{id}")
    public ResponseEntity<TacoOrder> adminGetOrderById(@PathVariable Long id) {
        return getOrder(id, true);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> adminDeleteOrderById(@PathVariable Long id) {
        orderService.deleteOrderById(id, true);
        return ResponseEntity.ok("deleted.");
    }
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        return new ResponseEntity<>(orderService.createOrder(orderRequest), HttpStatus.CREATED);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> myOrders(@RequestParam(value = "sort", defaultValue = "placedAt") String sort,
                                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                                        @RequestParam(value = "size", defaultValue = "10") int size) {
        List<TacoOrder> order = orderService.getAllOrders(sort, page, size, false);
        if (order == null)
            return ResponseEntity.notFound().build();
        List<OrderResponse> orderResponseList;
        orderResponseList = order.stream().map(orderService::convertToOrderResponse).collect(Collectors.toList());
        return ResponseEntity.ok(orderResponseList);

    }

    // Check if a Taco order should be returned, or an OrderResponse
    @GetMapping("/my-orders/{id}")
    public ResponseEntity<TacoOrder> getOrderById(@PathVariable Long id) {
       return getOrder(id, false);
    }

    /* TODO: For Admin, the admin gets the orders and this controller may involve queries from the
    *   OrderRepository based on delivery street, and perhaps send it to the admin's dashboard, who may
    *   eventually send it to a Taco Kitchen app or not
    *   List of queries:  get order by delivery street, state, city and zip. */

    @DeleteMapping("/my-orders/{id}")
    public ResponseEntity<List<TacoOrder>> deleteOrderById(@PathVariable Long id) {
        List<TacoOrder> userOrders = orderService.deleteOrderById(id, false);
        if (userOrders == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(userOrders);
    }

    @GetMapping("search")
    @ResponseStatus(HttpStatus.OK)
    public List<TacoOrder> searchOrders(@RequestParam("t") String t) {
        return orderService.findBySearchText(t);
    }


    // In the future, you may add multiple deletion by putting all the ids in a list in a request.

    // ------------------------------------------- Private methods --------------------------------------------------------

    private ResponseEntity<TacoOrder> getOrder(Long id, boolean admin) {
        TacoOrder order = orderService.getOrderById(id, admin);
        return order == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(order);
    }


}

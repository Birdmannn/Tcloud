package sia.tcloud3.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.tcloud3.dtos.requests.OrderRequest;
import sia.tcloud3.dtos.response.OrderResponse;
import sia.tcloud3.entity.TacoOrder;
import sia.tcloud3.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("orders")
public class OrderController {

    // TODO: For users, just post the order. well you can get this order by user id
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<TacoOrder>> getAllOrders(@RequestParam("sort") String sort,
                                                        @RequestParam("page") int page,
                                                        @RequestParam("size") int size) {
        return ResponseEntity.ok(orderService.getAllOrders(sort, page, size));
    }

    @GetMapping("{id}")
    public TacoOrder adminGetOrderById(@PathVariable Long id) {

    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> adminDeleteOrderById(@PathVariable Long id) {
        orderService.deleteOrderById(id, true);
    }
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
        return new ResponseEntity<>(orderService.createOrder(orderRequest), HttpStatus.CREATED);
    }

    @GetMapping("my-orders")
    // Check pagination, sorting, for both admin and user
    public ResponseEntity<List<OrderResponse>> myOrders() {

    }

    @GetMapping("/my-orders/{id}")
    public ResponseEntity<TacoOrder> getOrderById(@PathVariable Long id) {
        TacoOrder order = orderService.getOrderById(id);
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

    // In the future, you may add multiple deletion by putting all the ids in a list in a request.
}

package sia.tcloud3.service.checkout;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sia.tcloud3.dtos.requests.checkout.CreatePlanRequest;
import sia.tcloud3.dtos.response.checkout.CreatePlanResponse;
import sia.tcloud3.dtos.response.checkout.InitializePaymentResponse;
import sia.tcloud3.dtos.response.checkout.PaymentVerificationResponse;
import sia.tcloud3.entity.TacoOrder;
import sia.tcloud3.entity.Users;
import sia.tcloud3.entity.checkout.PaymentPaystack;
import sia.tcloud3.mapper.PaymentMapper;
import sia.tcloud3.repositories.OrderRepository;
import sia.tcloud3.repositories.PaystackPaymentRepository;
import sia.tcloud3.service.UserService;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static sia.tcloud3.constants.APIConstants.PAYSTACK_INITIALIZE_PAY;
import static sia.tcloud3.constants.APIConstants.PAYSTACK_VERIFY;

@Slf4j
@Service
public class PaystackServiceImpl implements PaystackService {

    private final UserService userService;
    private final PaystackPaymentRepository paystackPaymentRepository;
    private final RestTemplate restTemplate;
    private final PaymentMapper paymentMapper;
    private final PaystackPaymentRepository paymentRepo;
    private final OrderRepository orderRepository;

    @Value("${applyform.paystack.secret.key}")
    private String paystackSecretKey;

    public PaystackServiceImpl(UserService userService, PaystackPaymentRepository paystackPaymentRepository,
                               RestTemplate restTemplate, PaymentMapper paymentMapper, PaystackPaymentRepository paymentRepo,
                               OrderRepository orderRepository) {
        this.userService = userService;
        this.paystackPaymentRepository = paystackPaymentRepository;
        this.restTemplate = restTemplate;
        this.paymentMapper = paymentMapper;
        this.paymentRepo = paymentRepo;
        this.orderRepository = orderRepository;
    }

    @Override
    public CreatePlanResponse createPlan(CreatePlanRequest request) throws Exception {
        return null;
    }

    @Override
    public InitializePaymentResponse initializePayment(@NotNull Long orderId) {
        ResponseEntity<InitializePaymentResponse> response = null;

        Users user =  userService.retrieveCurrentUser();
        Long userId = user.getId();
        Object[] values = resolveOrder(orderId, userId);
        boolean proceed = (boolean) values[0];
        String message = (String) values[1];

        if (proceed) {
            try {
                TacoOrder order = (TacoOrder) values[2];
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(paystackSecretKey);
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, String> request = new HashMap<>();
                BigDecimal amountToBePaid = order.getCost().multiply(new BigDecimal(100));
                request.put("email", user.getEmail());
                request.put("amount", String.valueOf(amountToBePaid));
                HttpEntity requestEntity = new HttpEntity(request, headers);
                response = restTemplate.exchange(PAYSTACK_INITIALIZE_PAY, HttpMethod.POST, requestEntity, InitializePaymentResponse.class);

                // TODO:Try to map and save this payment reference and code to a repository.

            } catch (Exception e) {
                e.printStackTrace();
                message = "Something went wrong with Initializing this payment response";
            }
        }
        return response != null ? response.getBody() : InitializePaymentResponse.builder().status(false)
                .message(message).build();
    }

    @Override
    @Transactional
    public PaymentVerificationResponse verifyPayment(@NotNull Long orderId, @NotNull String reference) {
        ResponseEntity<PaymentVerificationResponse> response = null;
        String url = PAYSTACK_VERIFY + reference;
        Users user = userService.retrieveCurrentUser();
        Long userId = user.getId();
        Object[] values = resolveOrder(orderId, userId);

        boolean proceed = (boolean) values[0];
        String message = (String) values[1];

        if (proceed) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(paystackSecretKey);
                HttpEntity request = new HttpEntity(headers);
                response = restTemplate.exchange(url, HttpMethod.GET, request, PaymentVerificationResponse.class);

                if (response.getBody() == null || response.getBody().getStatus().equals(false))
                    throw new PaymentException("An error occurred in verifying payment.");
                else if (response.getBody().getData().getStatus().equalsIgnoreCase("success")) {
                    PaymentPaystack paymentPaystack = new PaymentPaystack();
                    paymentMapper.saveVerifiedPayment(paymentPaystack, response.getBody().getData());
                    paymentPaystack.setUser(user);
                    paymentPaystack.setOrderId(orderId);
                    TacoOrder order = (TacoOrder) values[2];
                    order.setStatus("purchased");
                    orderRepository.save(order);
                    paymentRepo.save(paymentPaystack);
                }
            } catch (Exception e) {
                e.printStackTrace(new PrintStream(System.out));
                message = "Something went wrong with the Payment Verification Response.";
            }
        }

        return response != null ? response.getBody() : PaymentVerificationResponse.builder().status(false)
                .message(message).build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Iterable<PaymentPaystack> getPayments() {
        // TODO: Add the Pageable in the future
        return paystackPaymentRepository.findAll();
    }

    // ----------------------------------------------------------------------------------------------------------------------
    private Object[] resolveOrder(Long  orderId, Long userId) {
        Object[] returnValues = new Object[3];
        boolean proceed = true;
        String message = "?";
        Optional<TacoOrder> orderOpt = orderRepository.findById(orderId);

        if (! orderOpt.isPresent()) {
            message = "No order found with order id: " + orderId;
            proceed = false;
        }

        if (proceed) {
            TacoOrder order = orderOpt.get();
            if (order.getStatus().equals("purchased")) {
                message = "This order has already been purchased by you.";
                proceed = false;
            }

            if (! Objects.equals(order.getUserId(), userId)) {
                message = "Order user and current user don't match.";
                proceed = false;
            }
        }

        returnValues[0] = proceed;
        returnValues[1] = message;
        returnValues[2] = orderOpt.get();

        return returnValues;
    }


    // ----------------------------------------------------------------------------------------------------------------------

    public static class PaymentException extends Exception {
        PaymentException(String message) {
            super(message);
        }

        PaymentException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

package sia.tcloud3.controllers;

import feign.Response;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.tcloud3.dtos.requests.checkout.InitializePaymentRequest;
import sia.tcloud3.dtos.response.checkout.InitializePaymentResponse;
import sia.tcloud3.dtos.response.checkout.PaymentVerificationResponse;
import sia.tcloud3.service.checkout.PaystackServiceImpl;

@RestController
@RequestMapping("payment")
public class PaymentController {

    private final PaystackServiceImpl paystackService;

    public PaymentController(PaystackServiceImpl paystackService) {
        this.paystackService = paystackService;
    }

    @PostMapping("/initializePayment/{orderId}")
    public ResponseEntity<InitializePaymentResponse> initializePayment(@PathVariable Long orderId)  {
        InitializePaymentResponse response = paystackService.initializePayment(orderId);
        return ! response.getStatus()
                ? ResponseEntity.ok(response)
                : new ResponseEntity<>(response, HttpStatusCode.valueOf(502));
//        return ResponseEntity.ok(response);
    }

    @GetMapping("/verifyPayment/{reference}")
    public ResponseEntity<PaymentVerificationResponse> verify(@PathVariable("reference") String reference) {
        PaymentVerificationResponse response = paystackService.verifyPayment(reference);
        return ! response.getStatus()
                ? ResponseEntity.ok(response)
                : new ResponseEntity<>(response, HttpStatusCode.valueOf(502));

    }
}

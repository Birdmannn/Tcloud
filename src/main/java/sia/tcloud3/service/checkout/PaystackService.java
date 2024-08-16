package sia.tcloud3.service.checkout;

import sia.tcloud3.dtos.requests.checkout.CreatePlanRequest;
import sia.tcloud3.dtos.response.checkout.CreatePlanResponse;
import sia.tcloud3.dtos.response.checkout.InitializePaymentResponse;
import sia.tcloud3.dtos.response.checkout.PaymentVerificationResponse;
import sia.tcloud3.entity.checkout.PaymentPaystack;

public interface PaystackService {
    CreatePlanResponse createPlan(CreatePlanRequest request) throws Exception;
    InitializePaymentResponse initializePayment(Long orderId);
    PaymentVerificationResponse verifyPayment(Long orderId, String reference);
    Iterable<PaymentPaystack> getPayments();
}

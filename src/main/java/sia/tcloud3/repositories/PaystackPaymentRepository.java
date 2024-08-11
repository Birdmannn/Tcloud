package sia.tcloud3.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sia.tcloud3.entity.checkout.PaymentPaystack;

@Repository
public interface PaystackPaymentRepository extends JpaRepository<PaymentPaystack, Long> {

}

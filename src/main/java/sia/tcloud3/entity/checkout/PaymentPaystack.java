package sia.tcloud3.entity.checkout;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import sia.tcloud3.entity.Users;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
public class PaymentPaystack {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    Users user;

    String reference;
    BigDecimal amount;
    String gatewayResponse;
    String paidAt;
    String createdAt;
    String currency;
    String ipAddress;
//    PricingPlanType planType = PricingPlanType.BASIC;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, nullable = false)
    Date createdOn;
}

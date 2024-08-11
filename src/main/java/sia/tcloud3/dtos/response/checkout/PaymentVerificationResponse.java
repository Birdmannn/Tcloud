package sia.tcloud3.dtos.response.checkout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentVerificationResponse {
    Boolean status;
    String message;
    Data data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Data {
        String status;
        String reference;
        BigDecimal amount;
        String gatewayResponse;
        String paidAt;
        String orderedAt;
        String channel;
        String currency;
        String ipAddress;
        String pricingPlanType;

        Date createdOn = new Date();
        Date updatedOn = new Date();
    }
}

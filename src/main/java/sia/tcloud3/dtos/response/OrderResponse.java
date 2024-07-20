package sia.tcloud3.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderResponse {
    String message;
    Long orderId;
    Instant createdAt;
    List<String> tacoNames;
    BigDecimal cost; // add the charge here after the whole paystack issh
}

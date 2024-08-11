package sia.tcloud3.dtos.requests.checkout;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InitializePaymentRequest {
    @NotNull String amount;
    @NotNull String email;
    @NotNull String currency;
//    @NotNull String plan;
    @NotNull String[] channels;
}

package sia.tcloud3.dtos.requests.checkout;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

// TODO: WIPE THIS CLASS ENTIRELY.
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePlanRequest {
    @NotNull(message = "Plan name cannot be null.")
    String name;

    @NotNull(message = "Interval cannot be null.")
    String interval;

    @Digits(integer = 6, fraction = 2)
    @NotNull(message = "Amount cannot be null.")
    Integer amount;
}

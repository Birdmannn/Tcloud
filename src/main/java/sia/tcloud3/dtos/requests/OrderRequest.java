package sia.tcloud3.dtos.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;
import sia.tcloud3.entity.Taco;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    String deliveryName;
    String deliveryStreet;
    String deliveryCity;
    String deliveryState;
    String deliveryZip;
    List<Long> tacoIds;
}

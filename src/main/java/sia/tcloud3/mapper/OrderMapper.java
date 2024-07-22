package sia.tcloud3.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sia.tcloud3.dtos.requests.OrderRequest;
import sia.tcloud3.dtos.response.OrderResponse;
import sia.tcloud3.entity.TacoOrder;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "placedAt", ignore = true)
    @Mapping(target = "userId", ignore = true)
    TacoOrder toTacoOrder(OrderRequest orderRequest);

    OrderResponse toOrderResponse(TacoOrder order);
}

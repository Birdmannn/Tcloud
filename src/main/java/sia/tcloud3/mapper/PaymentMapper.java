package sia.tcloud3.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sia.tcloud3.entity.checkout.PaymentPaystack;
import sia.tcloud3.dtos.response.checkout.PaymentVerificationResponse.Data;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    void saveVerifiedPayment(@MappingTarget PaymentPaystack paymentPaystack, Data data);
}

package sia.tcloud3.dtos.response.checkout;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatePlanResponse {
    Boolean status;
    String message;
    PlanData data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class PlanData {
        String name;
        String amount;
        String interval;
        String integration;
        String planCode;
        String sendInvoices;
        String sendSms;
        String currency;
        String id;
        String createdAt;
        String updatedAt;
    }
}

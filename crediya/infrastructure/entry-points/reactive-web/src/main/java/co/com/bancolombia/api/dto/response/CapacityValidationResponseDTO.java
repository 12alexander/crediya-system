package co.com.bancolombia.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacityValidationResponseDTO {

    @JsonProperty("order_id")
    private String orderId;

    private String decision;

    @JsonProperty("max_debt_capacity")
    private BigDecimal maxDebtCapacity;

    @JsonProperty("current_monthly_debt")
    private BigDecimal currentMonthlyDebt;

    @JsonProperty("available_capacity")
    private BigDecimal availableCapacity;

    @JsonProperty("new_loan_payment")
    private BigDecimal newLoanPayment;

    @JsonProperty("payment_plan")
    private List<PaymentScheduleDTO> paymentPlan;

    @Builder.Default
    private String type = "CAPACITY_VALIDATION_RESULT";

    @JsonProperty("processed_date")
    private LocalDateTime processedDate;
}
package co.com.bancolombia.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class DebtCapacityResponseDTO {

    private String id;

    @JsonProperty("order_id")
    private String orderId;

    private String decision;

    @JsonProperty("available_capacity")
    private BigDecimal availableCapacity;

    @JsonProperty("monthly_payment")
    private BigDecimal monthlyPayment;

    @JsonProperty("current_monthly_debt")
    private BigDecimal currentMonthlyDebt;

    @JsonProperty("max_debt_capacity")
    private BigDecimal maxDebtCapacity;

    private String reason;

    @JsonProperty("payment_plan")
    private List<PaymentPlanItemDTO> paymentPlan;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("request_date")
    private LocalDateTime requestDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("processing_date")
    private LocalDateTime processingDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentPlanItemDTO {

        private Integer month;

        private BigDecimal capital;

        private BigDecimal interest;

        private BigDecimal total;

        private BigDecimal balance;
    }
}
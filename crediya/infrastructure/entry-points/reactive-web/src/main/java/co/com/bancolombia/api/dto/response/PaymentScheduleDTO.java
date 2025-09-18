package co.com.bancolombia.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentScheduleDTO {

    @JsonProperty("payment_number")
    private Integer paymentNumber;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("due_date")
    private LocalDate dueDate;

    @JsonProperty("principal_payment")
    private BigDecimal principalPayment;

    @JsonProperty("interest_payment")
    private BigDecimal interestPayment;

    @JsonProperty("total_payment")
    private BigDecimal totalPayment;

    @JsonProperty("remaining_balance")
    private BigDecimal remainingBalance;
}
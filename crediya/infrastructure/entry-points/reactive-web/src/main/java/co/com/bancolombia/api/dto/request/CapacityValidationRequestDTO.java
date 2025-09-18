package co.com.bancolombia.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacityValidationRequestDTO {

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("user_id")
    private String userId;

    private BigDecimal amount;

    private Integer deadline;

    @JsonProperty("email_address")
    private String emailAddress;

    @JsonProperty("base_salary")
    private BigDecimal baseSalary;

    @JsonProperty("interest_rate")
    private BigDecimal interestRate;

    @JsonProperty("loan_type_id")
    private String loanTypeId;

    @Builder.Default
    private String type = "CAPACITY_VALIDATION_REQUEST";

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
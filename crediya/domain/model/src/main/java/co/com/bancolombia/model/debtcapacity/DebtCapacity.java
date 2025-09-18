package co.com.bancolombia.model.debtcapacity;

import co.com.bancolombia.model.enums.StatusEnum;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class DebtCapacity {

    private String id;
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private Integer deadline;
    private String emailAddress;
    private BigDecimal baseSalary;
    private BigDecimal interestRate;
    private String loanTypeId;
    private StatusEnum status;
    private LocalDateTime requestDate;
    private LocalDateTime processingDate;
    private BigDecimal availableCapacity;
    private BigDecimal monthlyPayment;
    private BigDecimal currentMonthlyDebt;
    private BigDecimal maxDebtCapacity;
    private String reason;
    private List<PaymentPlanItem> paymentPlan;

    public static DebtCapacity fromValidationRequest(String orderId, String userId, BigDecimal amount, Integer deadline,
                                                   String emailAddress, BigDecimal baseSalary, BigDecimal interestRate, String loanTypeId) {
        return DebtCapacity.builder()
                .orderId(orderId)
                .userId(userId)
                .amount(amount)
                .deadline(deadline)
                .emailAddress(emailAddress)
                .baseSalary(baseSalary)
                .interestRate(interestRate)
                .loanTypeId(loanTypeId)
                .status(StatusEnum.PENDING)
                .requestDate(LocalDateTime.now())
                .build();
    }

    public void updateCalculationResult(StatusEnum decision, BigDecimal availableCapacity, BigDecimal monthlyPayment,
                                      BigDecimal currentDebt, BigDecimal maxCapacity, String reason, List<PaymentPlanItem> paymentPlan) {
        this.status = decision;
        this.availableCapacity = availableCapacity;
        this.monthlyPayment = monthlyPayment;
        this.currentMonthlyDebt = currentDebt;
        this.maxDebtCapacity = maxCapacity;
        this.reason = reason;
        this.paymentPlan = paymentPlan;
        this.processingDate = LocalDateTime.now();
    }

    public boolean isPending() {
        return StatusEnum.PENDING.equals(this.status);
    }

    public boolean isCompleted() {
        return StatusEnum.APPROVED.equals(this.status) ||
               StatusEnum.REJECTED.equals(this.status) ||
               StatusEnum.MANUAL_REVIEW.equals(this.status);
    }
}
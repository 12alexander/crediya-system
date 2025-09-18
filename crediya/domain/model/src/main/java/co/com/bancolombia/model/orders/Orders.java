package co.com.bancolombia.model.orders;
import co.com.bancolombia.model.constants.ValidationMessages;
import co.com.bancolombia.model.constants.BusinessRules;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Orders {
    private String id;
    private BigDecimal amount;
    private Integer deadline;
    private String emailAddress;
    private String idStatus;
    private String idLoanType;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;

    public static Orders createNew(BigDecimal amount, Integer deadline, 
                                 String emailAddress, String idLoanType, String pendingStatusId) {
        LocalDateTime now = LocalDateTime.now();
        return Orders.builder()
                .id(UUID.randomUUID().toString())
                .amount(amount)
                .deadline(deadline)
                .emailAddress(emailAddress)
                .idLoanType(idLoanType)
                .idStatus(pendingStatusId)
                .creationDate(now)
                .updateDate(now)
                .build();
    }

    public void validateForCreation() {
        validateAmount();
        validateDeadline();
        validateEmailAddress();
        validateLoanType();
    }

    private void validateAmount() {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(ValidationMessages.AMOUNT_REQUIRED);
        }
        if (amount.scale() > BusinessRules.MAX_AMOUNT_DECIMALS) {
            throw new IllegalArgumentException(ValidationMessages.AMOUNT_DECIMALS);
        }
    }

    private void validateDeadline() {
        if (deadline == null || deadline <= 0) {
            throw new IllegalArgumentException(ValidationMessages.DEADLINE_REQUIRED);
        }
        if (deadline > BusinessRules.MAX_DEADLINE_MONTHS) {
            throw new IllegalArgumentException(ValidationMessages.DEADLINE_MAX_EXCEEDED);
        }
    }

    private void validateEmailAddress() {
        if (emailAddress == null || emailAddress.trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationMessages.EMAIL_REQUIRED);
        }
        if (!emailAddress.matches(BusinessRules.EMAIL_REGEX)) {
            throw new IllegalArgumentException(ValidationMessages.EMAIL_INVALID_FORMAT);
        }
    }

    private void validateLoanType() {
        if (idLoanType == null || idLoanType.trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationMessages.LOAN_TYPE_REQUIRED);
        }
    }
}

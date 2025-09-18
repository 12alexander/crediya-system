package co.com.bancolombia.model.orders.exceptions;

import co.com.bancolombia.model.constants.ValidationMessages;
import java.math.BigDecimal;

public class InvalidLoanAmountException extends OrdersBusinessException {

    public InvalidLoanAmountException(BigDecimal amount, BigDecimal minAmount, BigDecimal maxAmount) {
        super("INVALID_LOAN_AMOUNT",
              ValidationMessages.INVALID_LOAN_AMOUNT);
    }
}
package co.com.bancolombia.model.orders.exceptions;

import co.com.bancolombia.model.constants.ValidationMessages;

public class LoanTypeNotFoundException extends OrdersBusinessException {

    public LoanTypeNotFoundException(String loanTypeId) {
        super("LOAN_TYPE_NOT_FOUND",
              ValidationMessages.LOAN_TYPE_NOT_FOUND + loanTypeId);
    }
}
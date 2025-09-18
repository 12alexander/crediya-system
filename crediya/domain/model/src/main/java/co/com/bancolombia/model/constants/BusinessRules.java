package co.com.bancolombia.model.constants;

import java.math.BigDecimal;

public final class BusinessRules {

    private BusinessRules() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final int MAX_DEADLINE_MONTHS = 360;
    public static final int MIN_DEADLINE_MONTHS = 1;
    public static final int MAX_AMOUNT_DECIMALS = 2;
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
}
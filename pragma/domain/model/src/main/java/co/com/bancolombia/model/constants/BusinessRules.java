package co.com.bancolombia.model.constants;

import java.math.BigDecimal;

public final class BusinessRules {

    private BusinessRules() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final BigDecimal MAX_SALARY = new BigDecimal("15000000");
    public static final int MIN_PASSWORD_LENGTH = 3;
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
}
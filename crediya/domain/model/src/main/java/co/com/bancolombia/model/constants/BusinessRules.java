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

    public static final BigDecimal MAX_DEBT_RATIO = new BigDecimal("0.35");
    public static final int MONTHLY_CALCULATION_PRECISION = 10;
    public static final int SALARY_MULTIPLIER_FOR_MANUAL_REVIEW = 5;
    public static final String CAPACITY_VALIDATION_TYPE = "CAPACITY_VALIDATION_REQUEST";
    public static final String DEBT_CAPACITY_RESULT_TYPE = "DEBT_CAPACITY_RESULT";
}
package co.com.bancolombia.model.constants;

public final class ValidationMessages {

    private ValidationMessages() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String FIELD_REQUIRED = "Este campo es obligatorio";
    public static final String FIELD_NULL_EMPTY = "Este campo no puede ser nulo o vacío";
    public static final String EMAIL_REQUIRED = "El correo electrónico es obligatorio";
    public static final String EMAIL_INVALID_FORMAT = "El formato del correo electrónico no es válido";
    public static final String AMOUNT_REQUIRED = "El monto debe ser mayor que 0";
    public static final String AMOUNT_DECIMALS = "El monto no puede tener más de 2 decimales";
    public static final String DEADLINE_REQUIRED = "El plazo debe ser mayor que 0";
    public static final String DEADLINE_MAX_EXCEEDED = "El plazo no puede ser mayor a 360 meses";
    public static final String LOAN_TYPE_REQUIRED = "El tipo de préstamo es obligatorio";
    public static final String ORDER_NOT_FOUND = "No se encontró la solicitud con ID: ";
    public static final String PENDING_STATUS_NOT_FOUND = "No se encontró el estado 'PENDING'";
    public static final String ORDER_ALREADY_PROCESSED = "La orden ya fue procesada y no puede modificarse";
    public static final String INVALID_DECISION = "Decisión inválida: ";
    public static final String LOAN_TYPE_NOT_FOUND = "Tipo de préstamo no encontrado: ";
    public static final String INVALID_LOAN_AMOUNT = "El monto solicitado no está dentro del rango permitido para este tipo de préstamo";
}
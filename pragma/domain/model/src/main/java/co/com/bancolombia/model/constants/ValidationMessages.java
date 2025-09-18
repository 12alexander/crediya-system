package co.com.bancolombia.model.constants;

public final class ValidationMessages {

    private ValidationMessages() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String FIELD_REQUIRED = "Este campo es obligatorio";
    public static final String FIELD_NULL_EMPTY = "Este campo no puede ser nulo o vacío";
    public static final String EMAIL_REQUIRED = "El correo electrónico es obligatorio";
    public static final String EMAIL_INVALID_FORMAT = "El formato del correo electrónico no es válido";
    public static final String NAME_REQUIRED = "El nombre no puede ser nulo o vacío";
    public static final String LASTNAME_REQUIRED = "Los apellidos no pueden ser nulos o vacíos";
    public static final String SALARY_NULL = "El salario base no puede ser nulo";
    public static final String SALARY_ZERO = "El salario base debe ser mayor a 0";
    public static final String SALARY_MAX_EXCEEDED = "El salario base no puede ser mayor a 15,000,000";
    public static final String ROLE_ID_REQUIRED = "El ID del rol no puede ser nulo o vacío";
    public static final String PASSWORD_REQUIRED = "El password no puede ser nulo o vacío";
    public static final String PASSWORD_MIN_LENGTH = "El password debe tener al menos 3 caracteres";
    public static final String USER_NOT_FOUND_EMAIL = "Usuario no encontrado con Email: ";
    public static final String USER_NOT_FOUND_ID = "Usuario no encontrado con ID: ";
    public static final String USER_NOT_FOUND_LOGIN = "User not found for email: ";
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    public static final String ROLE_NOT_EXISTS = "El rol con ID ";
    public static final String ROLE_NOT_EXISTS_SUFFIX = " no existe";
    public static final String ERROR_SAVING_USER = "Error interno al guardar usuario";
    public static final String ERROR_GETTING_USERS = "Error interno al obtener usuarios";
    public static final String ERROR_GETTING_USER = "Error interno al obtener usuario";
    public static final String ERROR_DELETING_USER = "Error interno al eliminar usuario";
}
package exception;

public class UserValidationException extends Exception {
    private final String field;

    public UserValidationException(String message) {
        super(message);
        this.field = null;
    }

    public UserValidationException(String field, String message) {
        super("Validation error for field '" + field + "': " + message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
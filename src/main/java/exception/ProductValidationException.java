package exception;

public class ProductValidationException extends Exception {
    private final String fieldName;
    private final Object invalidValue;

    public ProductValidationException(String fieldName, Object invalidValue, String reason) {
        super("Invalid " + fieldName + ": " + invalidValue + " - " + reason);
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
    }

    public ProductValidationException(String message) {
        super(message);
        this.fieldName = "unknown";
        this.invalidValue = null;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }
}
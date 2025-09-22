package exception;

public class DatabaseOperationException extends Exception {
    private final String operation;

    public DatabaseOperationException(String operation, String message) {
        super("Database operation failed [" + operation + "]: " + message);
        this.operation = operation;
    }

    public DatabaseOperationException(String operation, String message, Throwable cause) {
        super("Database operation failed [" + operation + "]: " + message, cause);
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }
}
package exception;

public class DatabaseConnectionException extends Exception {
    private final String connectionDetails;

    public DatabaseConnectionException(String message) {
        super("Database connection failed: " + message);
        this.connectionDetails = message;
    }

    public DatabaseConnectionException(String message, Throwable cause) {
        super("Database connection failed: " + message, cause);
        this.connectionDetails = message;
    }

    public String getConnectionDetails() {
        return connectionDetails;
    }
}
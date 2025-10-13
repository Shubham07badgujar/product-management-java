package exception;

public class UserNotFoundException extends Exception {
    private final int userId;

    public UserNotFoundException(int userId) {
        super("User with ID " + userId + " not found in the database");
        this.userId = userId;
    }

    public UserNotFoundException(String message) {
        super(message);
        this.userId = -1;
    }

    public int getUserId() {
        return userId;
    }
}
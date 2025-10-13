package model;

public class User {
    private int id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;
    private String role; // "Admin" or "User"

    // Constructor for registration (without ID)
    public User(String firstName, String lastName, String email, String phoneNumber, String password, String role) {
        this.id = 0; // Will be auto-generated
        this.username = email; // Using email as username
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber != null ? phoneNumber : "";
        this.password = password != null ? password : "";
        this.role = role != null ? role : "User";
    }

    public User(int id, String username, String email, String firstName, String lastName, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = "";
        this.password = password != null ? password : "";
        this.role = "User"; // Default role
    }

    public User(int id, String username, String email, String firstName, String lastName, String password,
            String phoneNumber) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber != null ? phoneNumber : "";
        this.password = password != null ? password : "";
        this.role = "User"; // Default role
    }

    // Constructor with role
    public User(int id, String username, String email, String firstName, String lastName, String password,
            String phoneNumber, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber != null ? phoneNumber : "";
        this.password = password != null ? password : "";
        this.role = role != null ? role : "User";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber != null ? phoneNumber : "";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password != null ? password : "";
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role != null ? role : "User";
    }

    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(role);
    }

    public boolean isUser() {
        return "User".equalsIgnoreCase(role);
    }

    // Business methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean hasPhoneNumber() {
        return phoneNumber != null && !phoneNumber.trim().isEmpty();
    }

    // Required methods as per user request with database integration
    public boolean addUser() {
        // First validate the user data
        if (!validateUserData()) {
            System.err.println("User validation failed - cannot add user to database");
            return false;
        }

        // Try to add user to database using UserService
        try {
            service.UserService userService = new service.UserService();

            // Check if user already exists
            if (userService.userExists(this.id)) {
                System.err.println("User with ID " + this.id + " already exists in database");
                return false;
            }

            // Add user to database
            boolean success = userService.addUser(this);
            if (success) {
                System.out.println("✅ User successfully added to database: " + this.username);
            } else {
                System.err.println("❌ Failed to add user to database");
            }
            return success;

        } catch (Exception e) {
            System.err.println("Error adding user to database: " + e.getMessage());
            return false;
        }
    }

    public User getUser() {
        // Get user from database by ID using UserService
        try {
            service.UserService userService = new service.UserService();
            User dbUser = userService.getUserById(this.id);

            if (dbUser != null) {
                System.out.println("✅ User retrieved from database: " + dbUser.username);
                return dbUser;
            } else {
                System.out.println("ℹ️  User not found in database, returning current instance");
                return this;
            }

        } catch (Exception e) {
            System.err.println("Error retrieving user from database: " + e.getMessage());
            System.out.println("Returning current user instance");
            return this;
        }
    }

    // Helper method for validation
    private boolean validateUserData() {
        return username != null && !username.trim().isEmpty()
                && email != null && !email.trim().isEmpty()
                && firstName != null && !firstName.trim().isEmpty()
                && lastName != null && !lastName.trim().isEmpty()
                && password != null && !password.trim().isEmpty()
                && password.length() >= 6; // Minimum password length
    }

    // Password utility methods
    public boolean isPasswordValid() {
        return password != null && password.length() >= 6;
    }

    public boolean checkPassword(String inputPassword) {
        return password != null && password.equals(inputPassword);
    }

    @Override
    public String toString() {
        return String.format(
                "User [ID=%d, Username='%s', Email='%s', Name='%s', Phone='%s', Role='%s']",
                id, username, email, getFullName(),
                hasPhoneNumber() ? phoneNumber : "Not provided",
                role);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        User user = (User) obj;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
package service;

import dao.UserDao;
import dao.UserDaoImpl;
import exception.DatabaseOperationException;
import exception.UserValidationException;
import jakarta.mail.MessagingException;
import model.User;

/**
 * AuthService handles user authentication and registration
 * Provides methods for login, signup, and email validation
 */
public class AuthService {
    private final UserDao userDao;
    private final OTPService otpService;
    private final UserDaoImpl userDaoImpl;

    public AuthService() {
        this.userDao = new UserDaoImpl();
        this.otpService = new OTPService();
        this.userDaoImpl = new UserDaoImpl();
    }

    /**
     * Register a new user
     * 
     * @param user User object with registration details
     * @return true if registration successful, false otherwise
     */
    public boolean registerUser(User user) {
        if (user == null) {
            System.out.println("❌ User cannot be null");
            return false;
        }

        // Validate user data
        if (!validateUserData(user)) {
            return false;
        }

        // Check if username already exists
        if (isUsernameRegistered(user.getUsername())) {
            System.out.println("❌ Username already taken! Please choose a different username.");
            return false;
        }

        // Check if email already exists
        if (isEmailRegistered(user.getEmail())) {
            System.out.println("❌ Email already registered! Please use a different email.");
            return false;
        }

        // Generate new ID
        try {
            int newId = generateNewUserId();
            user.setId(newId);

            // Create user in database
            boolean success = userDao.create(user);
            if (success) {
                System.out.println("✅ Registration successful! Welcome, " + user.getFirstName() + "!");
                return true;
            } else {
                System.out.println("❌ Registration failed. Please try again.");
                return false;
            }

        } catch (DatabaseOperationException | UserValidationException e) {
            System.out.println("❌ Error during registration: " + e.getMessage());
            return false;
        }
    }

    /**
     * Authenticate user login
     * 
     * @param username User's username
     * @param email    User's email
     * @param password User's password
     * @return User object if login successful, null otherwise
     */
    public User loginUser(String username, String email, String password) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("❌ Username cannot be empty");
            return null;
        }

        if (email == null || email.trim().isEmpty()) {
            System.out.println("❌ Email cannot be empty");
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            System.out.println("❌ Password cannot be empty");
            return null;
        }

        try {
            // Authenticate using username, email and password
            User user = userDao.authenticateWithUsernameAndEmail(username, email, password);
            if (user != null) {
                // Check if email is verified
                if (!user.isVerified()) {
                    System.out.println("⚠️  Your email is not verified. Please verify your email before logging in.");
                    return null;
                }

                System.out.println("✅ Login successful! Welcome back, " + user.getFirstName() + "!");
                return user;
            } else {
                System.out.println("❌ Invalid username, email or password!");
                return null;
            }
        } catch (Exception e) {
            System.out.println("❌ Login failed: Invalid credentials");
            return null;
        }
    }

    /**
     * Send verification email with OTP
     * 
     * @param email User's email address
     * @return true if email sent successfully, false otherwise
     */
    public boolean sendVerificationEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            System.out.println("❌ Email cannot be empty");
            return false;
        }

        // Check if user exists
        User user = getUserByEmail(email);
        if (user == null) {
            System.out.println("❌ No account found with this email address");
            return false;
        }

        // Check if already verified
        if (user.isVerified()) {
            System.out.println("ℹ️  This email is already verified!");
            return false;
        }

        try {
            // Generate OTP
            String otp = otpService.generateOTP();

            // Store OTP
            otpService.storeOTP(email, otp);

            // Send OTP via email
            EmailService.sendOTP(email, otp);

            System.out.println("✅ Verification code sent to " + email);
            System.out.println("📧 Please check your email for the 6-digit code");
            return true;

        } catch (MessagingException e) {
            System.out.println("❌ Failed to send verification email: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("❌ Error sending verification email: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verify email with OTP
     * 
     * @param email      User's email address
     * @param enteredOTP OTP entered by user
     * @return true if verification successful, false otherwise
     */
    public boolean verifyEmail(String email, String enteredOTP) {
        if (email == null || email.trim().isEmpty()) {
            System.out.println("❌ Email cannot be empty");
            return false;
        }

        if (enteredOTP == null || enteredOTP.trim().isEmpty()) {
            System.out.println("❌ OTP cannot be empty");
            return false;
        }

        // Verify OTP
        if (otpService.verifyOTP(email, enteredOTP)) {
            // Update user's verified status in database
            try {
                boolean updated = userDaoImpl.updateVerified(email, true);
                if (updated) {
                    System.out.println("✅ Email verified successfully!");
                    System.out.println("🎉 You can now log in to your account");
                    return true;
                } else {
                    System.out.println("❌ Failed to update verification status");
                    return false;
                }
            } catch (DatabaseOperationException e) {
                System.out.println("❌ Database error: " + e.getMessage());
                return false;
            }
        }

        return false;
    }

    /**
     * Check if email is already registered
     * 
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public boolean isEmailRegistered(String email) {
        try {
            var users = userDao.findByEmail(email);
            return users != null && !users.isEmpty();
        } catch (DatabaseOperationException e) {
            System.err.println("Error checking email: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if username is already registered
     * 
     * @param username Username to check
     * @return true if username exists, false otherwise
     */
    public boolean isUsernameRegistered(String username) {
        try {
            var users = userDao.findByUsername(username);
            return users != null && !users.isEmpty();
        } catch (DatabaseOperationException e) {
            System.err.println("Error checking username: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validate user data before registration
     * 
     * @param user User to validate
     * @return true if valid, false otherwise
     */
    private boolean validateUserData(User user) {
        // Validate username
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            System.out.println("❌ Username is required");
            return false;
        }

        if (user.getUsername().length() < 3) {
            System.out.println("❌ Username must be at least 3 characters long");
            return false;
        }

        // Validate first name
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            System.out.println("❌ First name is required");
            return false;
        }

        // Validate last name
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            System.out.println("❌ Last name is required");
            return false;
        }

        // Validate email
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            System.out.println("❌ Email is required");
            return false;
        }

        if (!isValidEmail(user.getEmail())) {
            System.out.println("❌ Invalid email format");
            return false;
        }

        // Validate phone number
        if (user.getPhoneNumber() == null || user.getPhoneNumber().trim().isEmpty()) {
            System.out.println("❌ Phone number is required");
            return false;
        }

        // Validate password
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            System.out.println("❌ Password is required");
            return false;
        }

        if (user.getPassword().length() < 6) {
            System.out.println("❌ Password must be at least 6 characters long");
            return false;
        }

        // Validate role
        String role = user.getRole();
        if (role == null || (!role.equalsIgnoreCase("Admin") && !role.equalsIgnoreCase("User"))) {
            System.out.println("❌ Invalid role. Role must be 'Admin' or 'User'");
            return false;
        }

        return true;
    }

    /**
     * Simple email validation
     * 
     * @param email Email to validate
     * @return true if valid format, false otherwise
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Generate new user ID (simple increment strategy)
     * 
     * @return New user ID
     * @throws DatabaseOperationException
     */
    private int generateNewUserId() throws DatabaseOperationException {
        int maxId = 0;
        var allUsers = userDao.findAll();
        if (allUsers != null && !allUsers.isEmpty()) {
            for (User user : allUsers) {
                if (user.getId() > maxId) {
                    maxId = user.getId();
                }
            }
        }
        return maxId + 1;
    }

    /**
     * Get user by email
     * 
     * @param email User's email
     * @return User object if found, null otherwise
     */
    public User getUserByEmail(String email) {
        try {
            var users = userDao.findByEmail(email);
            return (users != null && !users.isEmpty()) ? users.get(0) : null;
        } catch (DatabaseOperationException e) {
            System.err.println("Error finding user by email: " + e.getMessage());
            return null;
        }
    }
}

package service;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.User;

/**
 * JUnit Test Cases for AuthService
 * Tests registration, login, and role-based authentication
 */
public class AuthServiceTest {

    private AuthService authService;
    private static int testUserCounter = 1000; // Start from 1000 to avoid conflicts

    @Before
    public void setUp() {
        authService = new AuthService();
        testUserCounter++; // Increment for each test
    }

    @After
    public void tearDown() {
        authService = null;
    }

    /**
     * Test Case 1: Successful user registration
     */
    @Test
    public void testSuccessfulRegistration() {
        // Arrange
        String uniqueEmail = "newuser" + testUserCounter + "@test.com";
        User newUser = new User("John", "Doe", uniqueEmail, "1234567890", "password123", "User");

        // Act
        boolean result = authService.registerUser(newUser);

        // Assert
        assertTrue("Registration should succeed with valid data", result);
    }

    /**
     * Test Case 2: Duplicate email handling (should fail)
     */
    @Test
    public void testDuplicateEmailRegistration() {
        // Arrange
        String duplicateEmail = "duplicate" + testUserCounter + "@test.com";
        User firstUser = new User("First", "User", duplicateEmail, "1111111111", "pass123456", "User");
        User secondUser = new User("Second", "User", duplicateEmail, "2222222222", "pass654321", "Admin");

        // Act
        boolean firstRegistration = authService.registerUser(firstUser);
        boolean secondRegistration = authService.registerUser(secondUser);

        // Assert
        assertTrue("First registration should succeed", firstRegistration);
        assertFalse("Second registration with duplicate email should fail", secondRegistration);
    }

    /**
     * Test Case 3: Successful login with valid credentials
     */
    @Test
    public void testSuccessfulLogin() {
        // Arrange
        String email = "logintest" + testUserCounter + "@test.com";
        String password = "securepass123";
        User newUser = new User("Login", "Test", email, "5555555555", password, "User");
        authService.registerUser(newUser);

        // Act
        User loggedInUser = authService.loginUser(email, password);

        // Assert
        assertNotNull("Login should return a user object", loggedInUser);
        assertEquals("Email should match", email, loggedInUser.getEmail());
        assertEquals("First name should match", "Login", loggedInUser.getFirstName());
    }

    /**
     * Test Case 4: Failed login with invalid credentials
     */
    @Test
    public void testInvalidLoginCredentials() {
        // Arrange
        String email = "invalid" + testUserCounter + "@test.com";
        String correctPassword = "correct123";
        String wrongPassword = "wrong123";
        User newUser = new User("Invalid", "Test", email, "6666666666", correctPassword, "User");
        authService.registerUser(newUser);

        // Act
        User loggedInUser = authService.loginUser(email, wrongPassword);

        // Assert
        assertNull("Login should fail with wrong password", loggedInUser);
    }

    /**
     * Test Case 5: Login with non-existent email
     */
    @Test
    public void testLoginNonExistentUser() {
        // Arrange
        String nonExistentEmail = "nonexistent" + testUserCounter + "@test.com";
        String password = "anypassword";

        // Act
        User loggedInUser = authService.loginUser(nonExistentEmail, password);

        // Assert
        assertNull("Login should fail for non-existent user", loggedInUser);
    }

    /**
     * Test Case 6: Role-based registration - Admin role
     */
    @Test
    public void testAdminRoleRegistration() {
        // Arrange
        String email = "admin" + testUserCounter + "@test.com";
        User adminUser = new User("Admin", "User", email, "7777777777", "adminpass", "Admin");

        // Act
        boolean result = authService.registerUser(adminUser);
        User registeredUser = authService.getUserByEmail(email);

        // Assert
        assertTrue("Admin registration should succeed", result);
        assertNotNull("Should find registered admin", registeredUser);
        assertTrue("User should have admin role", registeredUser.isAdmin());
        assertEquals("Role should be Admin", "Admin", registeredUser.getRole());
    }

    /**
     * Test Case 7: Role-based registration - User role
     */
    @Test
    public void testUserRoleRegistration() {
        // Arrange
        String email = "regularuser" + testUserCounter + "@test.com";
        User regularUser = new User("Regular", "User", email, "8888888888", "userpass", "User");

        // Act
        boolean result = authService.registerUser(regularUser);
        User registeredUser = authService.getUserByEmail(email);

        // Assert
        assertTrue("User registration should succeed", result);
        assertNotNull("Should find registered user", registeredUser);
        assertTrue("User should have user role", registeredUser.isUser());
        assertEquals("Role should be User", "User", registeredUser.getRole());
    }

    /**
     * Test Case 8: Role-based menu redirection (Admin)
     */
    @Test
    public void testAdminMenuAccess() {
        // Arrange
        String email = "adminmenu" + testUserCounter + "@test.com";
        User adminUser = new User("Menu", "Admin", email, "9999999999", "menupass", "Admin");
        authService.registerUser(adminUser);

        // Act
        User loggedInUser = authService.loginUser(email, "menupass");

        // Assert
        assertNotNull("Admin should be able to login", loggedInUser);
        assertTrue("Logged in user should be admin", loggedInUser.isAdmin());
        assertFalse("Admin should not be regular user", loggedInUser.isUser());
    }

    /**
     * Test Case 9: Role-based menu redirection (User)
     */
    @Test
    public void testUserMenuAccess() {
        // Arrange
        String email = "usermenu" + testUserCounter + "@test.com";
        User regularUser = new User("Menu", "User", email, "1010101010", "menupass", "User");
        authService.registerUser(regularUser);

        // Act
        User loggedInUser = authService.loginUser(email, "menupass");

        // Assert
        assertNotNull("User should be able to login", loggedInUser);
        assertTrue("Logged in user should be regular user", loggedInUser.isUser());
        assertFalse("User should not be admin", loggedInUser.isAdmin());
    }

    /**
     * Test Case 10: Registration with invalid email format
     */
    @Test
    public void testInvalidEmailFormat() {
        // Arrange
        User invalidUser = new User("Invalid", "Email", "not-an-email", "1212121212", "pass123", "User");

        // Act
        boolean result = authService.registerUser(invalidUser);

        // Assert
        assertFalse("Registration should fail with invalid email", result);
    }

    /**
     * Test Case 11: Registration with short password
     */
    @Test
    public void testShortPassword() {
        // Arrange
        String email = "shortpass" + testUserCounter + "@test.com";
        User userWithShortPass = new User("Short", "Pass", email, "1313131313", "12345", "User");

        // Act
        boolean result = authService.registerUser(userWithShortPass);

        // Assert
        assertFalse("Registration should fail with password less than 6 characters", result);
    }

    /**
     * Test Case 12: Registration with empty fields
     */
    @Test
    public void testRegistrationWithEmptyFields() {
        // Arrange
        User emptyUser = new User("", "", "", "", "pass123", "User");

        // Act
        boolean result = authService.registerUser(emptyUser);

        // Assert
        assertFalse("Registration should fail with empty fields", result);
    }

    /**
     * Test Case 13: Login with empty email
     */
    @Test
    public void testLoginWithEmptyEmail() {
        // Act
        User result = authService.loginUser("", "password123");

        // Assert
        assertNull("Login should fail with empty email", result);
    }

    /**
     * Test Case 14: Login with empty password
     */
    @Test
    public void testLoginWithEmptyPassword() {
        // Arrange
        String email = "emptypass" + testUserCounter + "@test.com";

        // Act
        User result = authService.loginUser(email, "");

        // Assert
        assertNull("Login should fail with empty password", result);
    }

    /**
     * Test Case 15: Check if email is registered
     */
    @Test
    public void testEmailRegisteredCheck() {
        // Arrange
        String email = "checkmail" + testUserCounter + "@test.com";
        User newUser = new User("Check", "Mail", email, "1414141414", "checkpass", "User");
        authService.registerUser(newUser);

        // Act
        boolean isRegistered = authService.isEmailRegistered(email);
        boolean notRegistered = authService.isEmailRegistered("notexist" + testUserCounter + "@test.com");

        // Assert
        assertTrue("Email should be registered", isRegistered);
        assertFalse("Email should not be registered", notRegistered);
    }
}

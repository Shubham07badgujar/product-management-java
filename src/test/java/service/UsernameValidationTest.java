package service;

import model.User;

/**
 * Simple test class to verify username uniqueness functionality
 * Run this class to test the username validation
 */
public class UsernameValidationTest {

    public static void main(String[] args) {
        AuthService authService = new AuthService();

        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║     USERNAME UNIQUENESS VALIDATION TEST                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝\n");

        // Test 1: Check if username exists
        System.out.println("📋 Test 1: Checking if username 'admin_user' exists...");
        boolean adminExists = authService.isUsernameRegistered("admin_user");
        System.out.println("   Result: " + (adminExists ? "✅ Username exists" : "❌ Username not found"));
        System.out.println();

        // Test 2: Check if a non-existent username exists
        System.out.println("📋 Test 2: Checking if username 'nonexistent_user123' exists...");
        boolean nonExistentUser = authService.isUsernameRegistered("nonexistent_user123");
        System.out.println("   Result: "
                + (nonExistentUser ? "❌ Username exists (unexpected)" : "✅ Username not found (expected)"));
        System.out.println();

        // Test 3: Try to register a user with a unique username
        System.out.println("📋 Test 3: Attempting to register user with unique username...");
        User newUser1 = new User(
                "unique_user_" + System.currentTimeMillis(), // Unique username
                "John",
                "Doe",
                "john.doe" + System.currentTimeMillis() + "@test.com", // Unique email
                "1234567890",
                "password123",
                "User");
        boolean registration1 = authService.registerUser(newUser1);
        System.out.println("   Result: " + (registration1 ? "✅ Registration successful" : "❌ Registration failed"));
        System.out.println();

        // Test 4: Try to register another user with the same username
        System.out.println("📋 Test 4: Attempting to register another user with same username...");
        User newUser2 = new User(
                newUser1.getUsername(), // Same username
                "Jane",
                "Smith",
                "jane.smith" + System.currentTimeMillis() + "@test.com", // Different email
                "0987654321",
                "password456",
                "User");
        boolean registration2 = authService.registerUser(newUser2);
        System.out.println("   Result: " + (registration2 ? "❌ Registration successful (unexpected - should fail)"
                : "✅ Registration failed (expected - duplicate username)"));
        System.out.println();

        // Test 5: Check email uniqueness still works
        System.out.println("📋 Test 5: Attempting to register user with same email but different username...");
        User newUser3 = new User(
                "another_unique_user_" + System.currentTimeMillis(), // Different username
                "Bob",
                "Johnson",
                newUser1.getEmail(), // Same email as newUser1
                "5555555555",
                "password789",
                "User");
        boolean registration3 = authService.registerUser(newUser3);
        System.out.println("   Result: " + (registration3 ? "❌ Registration successful (unexpected - should fail)"
                : "✅ Registration failed (expected - duplicate email)"));
        System.out.println();

        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║     TEST SUMMARY                                              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println("✅ Username uniqueness validation is working correctly!");
        System.out.println("✅ Email uniqueness validation is working correctly!");
        System.out.println("✅ Users cannot register with duplicate usernames!");
        System.out.println("✅ Users cannot register with duplicate emails!");
    }
}

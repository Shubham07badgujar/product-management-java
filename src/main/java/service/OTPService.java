package service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * OTPService handles One-Time Password generation and verification
 * Stores OTPs temporarily with expiration time
 */
public class OTPService {
    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXPIRY_TIME = 5 * 60 * 1000; // 5 minutes in milliseconds

    // Store OTP with email as key and OTPData as value
    private static final Map<String, OTPData> otpStore = new HashMap<>();

    /**
     * Generate a 6-digit OTP
     * 
     * @return String representing a 6-digit OTP
     */
    public String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generates number between 100000 and 999999
        return String.valueOf(otp);
    }

    /**
     * Store OTP for a specific email with expiration time
     * 
     * @param email User's email address
     * @param otp   Generated OTP
     */
    public void storeOTP(String email, String otp) {
        long expiryTime = System.currentTimeMillis() + OTP_EXPIRY_TIME;
        otpStore.put(email.toLowerCase(), new OTPData(otp, expiryTime));
    }

    /**
     * Verify if the provided OTP matches the stored OTP for the email
     * 
     * @param email      User's email address
     * @param enteredOTP OTP entered by user
     * @return true if OTP is valid and not expired, false otherwise
     */
    public boolean verifyOTP(String email, String enteredOTP) {
        String emailKey = email.toLowerCase();
        OTPData otpData = otpStore.get(emailKey);

        if (otpData == null) {
            System.out.println("❌ No OTP found for this email");
            return false;
        }

        // Check if OTP has expired
        if (System.currentTimeMillis() > otpData.expiryTime) {
            otpStore.remove(emailKey); // Remove expired OTP
            System.out.println("❌ OTP has expired. Please request a new one.");
            return false;
        }

        // Check if OTP matches
        if (otpData.otp.equals(enteredOTP)) {
            otpStore.remove(emailKey); // Remove used OTP
            return true;
        } else {
            System.out.println("❌ Invalid OTP. Please try again.");
            return false;
        }
    }

    /**
     * Check if an OTP exists for the given email
     * 
     * @param email User's email address
     * @return true if OTP exists and not expired, false otherwise
     */
    public boolean hasOTP(String email) {
        String emailKey = email.toLowerCase();
        OTPData otpData = otpStore.get(emailKey);

        if (otpData == null) {
            return false;
        }

        // Check if expired
        if (System.currentTimeMillis() > otpData.expiryTime) {
            otpStore.remove(emailKey);
            return false;
        }

        return true;
    }

    /**
     * Remove OTP for a specific email (used for cleanup or cancellation)
     * 
     * @param email User's email address
     */
    public void removeOTP(String email) {
        otpStore.remove(email.toLowerCase());
    }

    /**
     * Clean up expired OTPs from the store
     */
    public void cleanupExpiredOTPs() {
        long currentTime = System.currentTimeMillis();
        otpStore.entrySet().removeIf(entry -> currentTime > entry.getValue().expiryTime);
    }

    /**
     * Inner class to store OTP data with expiration time
     */
    private static class OTPData {
        String otp;
        long expiryTime;

        OTPData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }
}

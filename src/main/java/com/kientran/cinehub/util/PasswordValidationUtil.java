package com.kientran.cinehub.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class PasswordValidationUtil {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$"
    );

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 128;

    /**
     * Validate password strength
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        // Check length
        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            return false;
        }

        // Check pattern (at least one uppercase, one lowercase, one digit)
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Get password validation error message
     */
    public static String getPasswordValidationMessage(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "Password should not be blank";
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            return "Password should be at least " + MIN_PASSWORD_LENGTH + " characters";
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            return "Password should not exceed " + MAX_PASSWORD_LENGTH + " characters";
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return "Password must contain at least one uppercase letter, one lowercase letter, and one number";
        }

        return null; // Valid password
    }

    /**
     * Check if passwords match
     */
    public static boolean doPasswordsMatch(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }
}
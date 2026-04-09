package com.example.booklibraryapp;

import java.util.regex.Pattern;

public class ValidationUtils {
    // A more robust email regex that prevents consecutive dots in the domain
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@(?:[A-Z0-9-]+\\.)+[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE
    );

    /**
     * Validates if the provided string is a valid email format.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Checks if a string is not null and not just whitespace.
     */
    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }
}

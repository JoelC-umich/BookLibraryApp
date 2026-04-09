package com.example.booklibraryapp;

public class BookUtils {
    /**
     * Calculates the available quantity of books.
     * Available = Total - Borrowed.
     */
    public static int calculateAvailable(int total, int borrowed) {
        return Math.max(0, total - borrowed);
    }
    
    /**
     * Safely parses an integer from a string, returning a default value on failure.
     */
    public static int safeParseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

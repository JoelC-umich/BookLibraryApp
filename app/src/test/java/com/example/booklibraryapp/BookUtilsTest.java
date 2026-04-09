package com.example.booklibraryapp;

import org.junit.Test;
import static org.junit.Assert.*;

public class BookUtilsTest {

    @Test
    public void calculateAvailable_correctCalculation() {
        assertEquals(5, BookUtils.calculateAvailable(10, 5));
        assertEquals(0, BookUtils.calculateAvailable(10, 10));
        assertEquals(10, BookUtils.calculateAvailable(10, 0));
    }

    @Test
    public void calculateAvailable_negativeResultReturnsZero() {
        // If somehow borrowed > total, it should return 0, not negative.
        assertEquals(0, BookUtils.calculateAvailable(5, 10));
    }

    @Test
    public void safeParseInt_validInput() {
        assertEquals(10, BookUtils.safeParseInt("10", 0));
        assertEquals(-5, BookUtils.safeParseInt("-5", 0));
    }

    @Test
    public void safeParseInt_invalidInputReturnsDefault() {
        assertEquals(0, BookUtils.safeParseInt("abc", 0));
        assertEquals(1, BookUtils.safeParseInt("", 1));
        assertEquals(5, BookUtils.safeParseInt(null, 5));
    }
}

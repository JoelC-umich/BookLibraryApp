package com.example.booklibraryapp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ValidationUtilsTest {

    @Test
    public void testIsValidEmail_Valid() {
        assertTrue(ValidationUtils.isValidEmail("test@example.com"));
        assertTrue(ValidationUtils.isValidEmail("user.name@domain.co.uk"));
        assertTrue(ValidationUtils.isValidEmail("first.last@sub.domain.org"));
    }

    @Test
    public void testIsValidEmail_Invalid() {
        assertFalse(ValidationUtils.isValidEmail(null));
        assertFalse(ValidationUtils.isValidEmail(""));
        assertFalse(ValidationUtils.isValidEmail("plainaddress"));
        assertFalse(ValidationUtils.isValidEmail("#@%^%#$@#$@#.com"));
        assertFalse(ValidationUtils.isValidEmail("@example.com"));
        assertFalse(ValidationUtils.isValidEmail("Joe Smith <email@example.com>"));
        assertFalse(ValidationUtils.isValidEmail("email.example.com"));
        assertFalse(ValidationUtils.isValidEmail("email@example@example.com"));
        assertFalse(ValidationUtils.isValidEmail(".email@example.com"));
        assertFalse(ValidationUtils.isValidEmail("email.@example.com"));
        assertFalse(ValidationUtils.isValidEmail("email..email@example.com"));
    }

    @Test
    public void testIsNotEmpty_Valid() {
        assertTrue(ValidationUtils.isNotEmpty("a"));
        assertTrue(ValidationUtils.isNotEmpty("abc"));
        assertTrue(ValidationUtils.isNotEmpty("  abc  "));
    }

    @Test
    public void testIsNotEmpty_Invalid() {
        assertFalse(ValidationUtils.isNotEmpty(null));
        assertFalse(ValidationUtils.isNotEmpty(""));
        assertFalse(ValidationUtils.isNotEmpty(" "));
        assertFalse(ValidationUtils.isNotEmpty("\n"));
        assertFalse(ValidationUtils.isNotEmpty("\t"));
    }
}

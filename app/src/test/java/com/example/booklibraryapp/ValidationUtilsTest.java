package com.example.booklibraryapp;

import org.junit.Test;
import static org.junit.Assert.*;

public class ValidationUtilsTest {

    @Test
    public void isValidEmail_validEmails() {
        assertTrue(ValidationUtils.isValidEmail("test@example.com"));
        assertTrue(ValidationUtils.isValidEmail("user.name@domain.co.uk"));
        assertTrue(ValidationUtils.isValidEmail("id+label@provider.net"));
    }

    @Test
    public void isValidEmail_invalidEmails() {
        assertFalse(ValidationUtils.isValidEmail("plainaddress"));
        assertFalse(ValidationUtils.isValidEmail("@missingusername.com"));
        assertFalse(ValidationUtils.isValidEmail("username@.com"));
        assertFalse(ValidationUtils.isValidEmail("username@domain..com"));
        assertFalse(ValidationUtils.isValidEmail(null));
        assertFalse(ValidationUtils.isValidEmail(""));
    }

    @Test
    public void isNotEmpty_validStrings() {
        assertTrue(ValidationUtils.isNotEmpty("hello"));
        assertTrue(ValidationUtils.isNotEmpty("  word  "));
    }

    @Test
    public void isNotEmpty_invalidStrings() {
        assertFalse(ValidationUtils.isNotEmpty(null));
        assertFalse(ValidationUtils.isNotEmpty(""));
        assertFalse(ValidationUtils.isNotEmpty("   "));
    }
}

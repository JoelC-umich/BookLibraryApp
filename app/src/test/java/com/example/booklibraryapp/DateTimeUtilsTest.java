package com.example.booklibraryapp;

import org.junit.Test;
import static org.junit.Assert.*;

public class DateTimeUtilsTest {

    @Test
    public void slotToTime_validSlots() {
        assertEquals("8:00 AM", DateTimeUtils.slotToTime(1));
        assertEquals("10:00 AM", DateTimeUtils.slotToTime(2));
        assertEquals("12:00 PM", DateTimeUtils.slotToTime(3));
        assertEquals("2:00 PM", DateTimeUtils.slotToTime(4));
        assertEquals("4:00 PM", DateTimeUtils.slotToTime(5));
    }

    @Test
    public void slotToTime_invalidSlots() {
        assertEquals("Unknown", DateTimeUtils.slotToTime(0));
        assertEquals("Unknown", DateTimeUtils.slotToTime(6));
        assertEquals("Unknown", DateTimeUtils.slotToTime(-1));
    }
}

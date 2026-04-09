package com.example.booklibraryapp;

public class DateTimeUtils {
    public static String slotToTime(int slot) {
        switch (slot) {
            case 1: return "8:00 AM";
            case 2: return "10:00 AM";
            case 3: return "12:00 PM";
            case 4: return "2:00 PM";
            case 5: return "4:00 PM";
            default: return "Unknown";
        }
    }
}

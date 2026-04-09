package com.example.booklibraryapp;

import org.junit.Test;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.junit.Assert.*;

public class RoomUtilsTest {

    @Test
    public void getAvailableSlots_filtersReservedCorrectly() {
        List<String> allRooms = Arrays.asList("1", "2");
        Set<String> reserved = new HashSet<>();
        reserved.add("1:1"); // Room 1, Slot 1 is reserved
        reserved.add("2:5"); // Room 2, Slot 5 is reserved

        List<RoomUtils.AvailableSlot> available = RoomUtils.getAvailableSlots(allRooms, reserved, 5);

        // Total possible slots = 2 rooms * 5 slots = 10
        // Available should be 10 - 2 = 8
        assertEquals(8, available.size());

        // Verify specific available slots
        boolean foundRoom1Slot1 = false;
        boolean foundRoom1Slot2 = false;
        for (RoomUtils.AvailableSlot slot : available) {
            if (slot.key.equals("1:1")) foundRoom1Slot1 = true;
            if (slot.key.equals("1:2")) foundRoom1Slot2 = true;
        }

        assertFalse("Room 1 Slot 1 should be reserved", foundRoom1Slot1);
        assertTrue("Room 1 Slot 2 should be available", foundRoom1Slot2);
    }

    @Test
    public void getAvailableSlots_emptyRoomsReturnsEmpty() {
        List<String> allRooms = Arrays.asList();
        Set<String> reserved = new HashSet<>();
        List<RoomUtils.AvailableSlot> available = RoomUtils.getAvailableSlots(allRooms, reserved, 5);
        assertTrue(available.isEmpty());
    }
}

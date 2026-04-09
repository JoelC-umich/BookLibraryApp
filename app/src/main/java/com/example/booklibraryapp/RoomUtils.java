package com.example.booklibraryapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RoomUtils {
    
    public static class AvailableSlot {
        public final String label;
        public final String key;

        public AvailableSlot(String label, String key) {
            this.label = label;
            this.key = key;
        }
    }

    /**
     * Filters available room slots based on total rooms and already reserved slots.
     */
    public static List<AvailableSlot> getAvailableSlots(
            List<String> allRoomIDs, 
            Set<String> reservedSet, 
            int maxSlotsPerRoom) {
        
        List<AvailableSlot> available = new ArrayList<>();
        for (String roomID : allRoomIDs) {
            for (int slot = 1; slot <= maxSlotsPerRoom; slot++) {
                String key = roomID + ":" + slot;
                if (!reservedSet.contains(key)) {
                    String label = "Room " + roomID + " — " + DateTimeUtils.slotToTime(slot);
                    available.add(new AvailableSlot(label, key));
                }
            }
        }
        return available;
    }
}

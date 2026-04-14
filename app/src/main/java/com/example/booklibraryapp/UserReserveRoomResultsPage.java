package com.example.booklibraryapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserReserveRoomResultsPage extends Fragment {

    private static final int MAX_SLOTS_PER_ROOM = 5;

    // Tracks which list item the user tapped
    private int selectedIndex = -1;
    // The actual slot entries built from available combinations
    private List<String> availableSlotLabels = new ArrayList<>();
    // Parallel list storing "ROOM_ID:SLOT" so we know what to book
    private List<String> availableSlotKeys = new ArrayList<>();

    public UserReserveRoomResultsPage() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_reserve_room_results_page, container, false);

        ListView listView = view.findViewById(R.id.listUserReserveRoomResults);
        Button buttonReserve = view.findViewById(R.id.buttonReserveRoomReserve);

        // Pull the date the previous fragment passed in
        String selectedDate = getArguments() != null
                ? getArguments().getString("selectedDate", "") : "";

        if (selectedDate.isEmpty()) {
            Toast.makeText(getContext(), "No date selected.", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Run DB work off the main thread, then update UI on main thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {

            // 1. Get all room IDs that exist
            List<String> allRoomIDs = QueryConnectorPlusHelper.getRoomIDsQuery();

            // 2. Get every reserved ROOM_ID:SLOT pair for the chosen date
            List<String> reservedPairs = QueryConnectorPlusHelper.getRoomIDsReservedOnDateQuery(selectedDate);

            // Load reserved pairs into a Set for O(1) lookup
            Set<String> reservedSet = new HashSet<>(reservedPairs);

            // 3. Build the full list of available room+slot combinations
            List<String> labels = new ArrayList<>();
            List<String> keys = new ArrayList<>();

            for (String roomID : allRoomIDs) {
                for (int slot = 1; slot <= MAX_SLOTS_PER_ROOM; slot++) {
                    String key = roomID + ":" + slot;
                    if (!reservedSet.contains(key)) {
                        labels.add("Room " + roomID + " — " + slotToTime(slot));
                        keys.add(key);
                    }
                }
            }

            // 4. Update UI back on the main thread
            requireActivity().runOnUiThread(() -> {
                availableSlotLabels.clear();
                availableSlotKeys.clear();
                availableSlotLabels.addAll(labels);
                availableSlotKeys.addAll(keys);

                if (availableSlotLabels.isEmpty()) {
                    Toast.makeText(getContext(),
                            "No rooms available for " + selectedDate, Toast.LENGTH_LONG).show();
                    // Disable the reserve button since there's nothing to select
                    buttonReserve.setEnabled(false);
                } else {
                    buttonReserve.setEnabled(true);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_list_item_single_choice,
                        availableSlotLabels
                );
                listView.setAdapter(adapter);
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                listView.setOnItemClickListener((parent, v, position, id) -> {
                    selectedIndex = position;
                });
            });
        });
        executor.shutdown();

        buttonReserve.setOnClickListener(v -> {
            if (selectedIndex < 0) {
                Toast.makeText(getContext(), "Please select a time slot.", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] parts = availableSlotKeys.get(selectedIndex).split(":");
            String roomID = parts[0];
            String slot = parts[1];
            String userID = QueryConnectorPlusHelper.IDWhenLoggingIn;

            QueryConnectorPlusHelper.insertRoomReservationQuery(roomID, userID, selectedDate, slot);

            Toast.makeText(getContext(),
                    "Room " + roomID + " — " + slotToTime(Integer.parseInt(slot)) + " reserved for " + selectedDate + "!",
                    Toast.LENGTH_LONG).show();

            // Use v (the button itself) instead of the stale view reference
            Navigation.findNavController(v)
                    .navigate(R.id.action_userReserveRoomResultsPage_to_userPage);
        });

        return view;
    }

    private String slotToTime(int slot) {
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
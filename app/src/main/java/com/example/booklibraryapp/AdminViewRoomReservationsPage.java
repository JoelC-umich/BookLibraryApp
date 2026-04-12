package com.example.booklibraryapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminViewRoomReservationsPage extends Fragment {

    private ListView listView;
    private CalendarView calendarView;
    private String selectedDate;

    public AdminViewRoomReservationsPage() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_view_room_reservations_page, container, false);

        listView = view.findViewById(R.id.listViewAdminRoomReservations);
        calendarView = view.findViewById(R.id.calendarViewAdminRoomReservations);

        // Set default date to today
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        updateListView(selectedDate);

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            updateListView(selectedDate);
        });

        return view;
    }

    private void updateListView(String date) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<String> displayList = new ArrayList<>();
            try {
                Connection connection = QueryConnectorPlusHelper.Connector();
                if (connection != null) {
                    Statement statement = connection.createStatement();
                    String query = "SELECT RR.USER_ID, RR.SLOT, RR.RESERVE_STATUS, U.FIRST_NAME, U.LAST_NAME " +
                                   "FROM ROOMS_RESERVED RR " +
                                   "JOIN USERS U ON RR.USER_ID = U.ID " +
                                   "WHERE RR.DATE = '" + date + "'";
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        String firstName = resultSet.getString("FIRST_NAME");
                        String lastName = resultSet.getString("LAST_NAME");
                        int slot = resultSet.getInt("SLOT");
                        String status = resultSet.getString("RESERVE_STATUS");
                        
                        displayList.add(firstName + " " + lastName + " - Slot: " + slotToTime(slot) + " (" + status + ")");
                    }
                    resultSet.close();
                    statement.close();
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_list_item_1, displayList);
                    listView.setAdapter(adapter);
                });
            }
        });
        executor.shutdown();
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

package com.example.booklibraryapp;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Toast;

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

public class UserViewReservedRoomsPage extends Fragment {
    private ListView listUserViewReservedRooms;
    private CalendarView calendarUserViewReservedRooms;
    private String selectedDate;
    String loggedInUserID = QueryConnectorPlusHelper.IDWhenLoggingIn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_view_reserved_rooms_page, container, false);
        listUserViewReservedRooms = view.findViewById(R.id.listUserViewReservedRooms);
        calendarUserViewReservedRooms = view.findViewById(R.id.calendarUserViewReservedRooms);
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        updateListView(selectedDate);

        calendarUserViewReservedRooms.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            updateListView(selectedDate);
        });

        listUserViewReservedRooms.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedRequest = (String) parent.getItemAtPosition(position);
            String[] splittedSelectedRequest = selectedRequest.split(" ");
            String roomID = splittedSelectedRequest[1];
            String time = splittedSelectedRequest[3] + " " + splittedSelectedRequest[4];
            String slot = timeToSlot(time);
            String reservedStatus = splittedSelectedRequest[5].replace("(", "").replace(")","");
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            dialogBuilder.setTitle("Cancel Room " + roomID + " Reservation?");
            dialogBuilder.setMessage("Would you like to cancel your " + time + " reservation for Room " + roomID + "?");

            dialogBuilder.setPositiveButton("Yes", (dialog, which) ->
            {
                QueryConnectorPlusHelper.runQuery("UPDATE ROOMS_RESERVED SET RESERVE_STATUS = 'Canceled' WHERE ROOM_ID = '" + roomID + "' " +
                        "AND USER_ID = '" + loggedInUserID + "' AND RESERVE_STATUS = '" +reservedStatus+ "' AND SLOT = '" +slot+ "' AND DATE = '" +selectedDate+ "'");
                Toast.makeText(getContext(), "Room " + roomID + " " + time + " is canceled", Toast.LENGTH_SHORT).show();
            });

            dialogBuilder.setNegativeButton("No", (dialog, which) ->
            {
                dialog.dismiss();
            });

            dialogBuilder.setNeutralButton("Cancel", (dialog, which) ->
            {
                dialog.dismiss();
            });

            AlertDialog handleRoomRequestDialog = dialogBuilder.create();
            handleRoomRequestDialog.show();
            //FIND A WAY TO REFRESH THE VIEW AFTER APPROVING/DECLINING REQUESTS
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
                    String query = "SELECT RR.USER_ID, RR.ROOM_ID, RR.SLOT, RR.RESERVE_STATUS " +
                            "FROM ROOMS_RESERVED RR " +
                            "JOIN USERS U ON RR.USER_ID = U.ID " +
                            "WHERE RR.USER_ID = '" + loggedInUserID + "' AND RR.DATE = '" + date + "' " +
                            "AND RR.RESERVE_STATUS != 'Canceled'";
                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        String roomID = resultSet.getString("ROOM_ID");
                        int slot = resultSet.getInt("SLOT");
                        String status = resultSet.getString("RESERVE_STATUS");

                        displayList.add("Room: " + roomID + " Slot: " + slotToTime(slot) + " (" + status + ")");
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, displayList);
                    listUserViewReservedRooms.setAdapter(adapter);
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

    private String timeToSlot(String time) {
        switch (time) {
            case "8:00 AM": return "1";
            case "10:00 AM": return "2";
            case "12:00 PM": return "3";
            case "2:00 PM": return "4";
            case "4:00 PM": return "5";
            default: return "Unknown";
        }
    }
}
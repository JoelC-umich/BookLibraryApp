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
            // "Room: 101 Slot: 1 (8:00 AM) (Reserved)"
            String[] splittedSelectedRequest = selectedRequest.split(" ");
            String roomID = splittedSelectedRequest[1];
            String slotStr = splittedSelectedRequest[3];
            String time = splittedSelectedRequest[4].replace("(", "").replace(")", "") + " " + splittedSelectedRequest[5].replace("(", "").replace(")", "");
            String reservedStatus = splittedSelectedRequest[6].replace("(", "").replace(")","");
            
            if (getContext() == null) return;
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            dialogBuilder.setTitle("Cancel Room " + roomID + " Reservation?");
            dialogBuilder.setMessage("Would you like to cancel your " + time + " reservation for Room " + roomID + "?");

            dialogBuilder.setPositiveButton("Yes", (dialog, which) ->
            {
                QueryConnectorPlusHelper.runQuery("UPDATE ROOMS_RESERVED SET RESERVE_STATUS = 'Canceled' WHERE ROOM_ID = '" + roomID + "' " +
                        "AND USER_ID = '" + loggedInUserID + "' AND RESERVE_STATUS = '" +reservedStatus+ "' AND SLOT = '" +slotStr+ "' AND DATE = '" +selectedDate+ "'", () -> {
                    if (isAdded() && getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            updateListView(selectedDate);
                            Toast.makeText(getContext(), "Room " + roomID + " " + time + " is canceled", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
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
        });

        return view;
    }

    private void updateListView(String date) {
        QueryConnectorPlusHelper.executor.execute(() -> {
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

                        displayList.add("Room: " + roomID + " Slot: " + slot + " (" + QueryConnectorPlusHelper.slotToTime(slot) + ") (" + status + ")");
                    }
                    resultSet.close();
                    statement.close();
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (getContext() != null) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, displayList);
                        listUserViewReservedRooms.setAdapter(adapter);
                    }
                });
            }
        });
    }
}

package com.example.booklibraryapp;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class AdminViewRoomRequestsPage extends Fragment
{
    ListView listViewAdminRoomRequests;
    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_admin_view_room_requests_page, container, false);
        listViewAdminRoomRequests = view.findViewById(R.id.listViewAdminRoomRequests);
        List<String> pendingRoomsReservedQuery = QueryConnectorPlusHelper.getPendingRoomsReservedQuery();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, pendingRoomsReservedQuery);
        listViewAdminRoomRequests.setAdapter(adapter);
        listViewAdminRoomRequests.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedRequestID = (String) parent.getItemAtPosition(position);
            String roomID = QueryConnectorPlusHelper.getRoomIDFromRoomReserveID(selectedRequestID);
            String userID = QueryConnectorPlusHelper.getUserIDFromRoomReserveID(selectedRequestID);
            String username = QueryConnectorPlusHelper.getUsernameFromID(userID);
            String userFullName = (QueryConnectorPlusHelper.getFirstNameFromIDQuery(userID)+" "+QueryConnectorPlusHelper.getLastNameFromIDQuery(userID));
            String userEmail = QueryConnectorPlusHelper.getEmailFromIDQuery(userID);
            String slot = QueryConnectorPlusHelper.getSlotFromRoomReserveID(selectedRequestID);
            String date = QueryConnectorPlusHelper.getDateFromRoomReserveID(selectedRequestID);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            dialogBuilder.setTitle("Room Request "+selectedRequestID);
            dialogBuilder.setMessage(
                    "Room ID: "+roomID+
                    "\nUser ID: "+userID+
                    "\nUsername: "+username+
                    "\nFull Name: "+userFullName+
                    "\nEmail: "+userEmail+
                    "\nSlot: "+slot+
                    "\nDate: "+date+
                    "\n\nApprove?");

            dialogBuilder.setPositiveButton("Yes", (dialog, which) ->
            {
                QueryConnectorPlusHelper.runQuery("UPDATE ROOMS_RESERVED SET RESERVE_STATUS = 'Reserved' WHERE ID = '"+selectedRequestID+"'");
                Toast.makeText(getContext(), "Request "+selectedRequestID+" is successfully approved", Toast.LENGTH_SHORT).show();
            });

            dialogBuilder.setNegativeButton("No", (dialog, which) ->
            {
                QueryConnectorPlusHelper.runQuery("UPDATE ROOMS_RESERVED SET RESERVE_STATUS = 'Available' WHERE ID = '"+selectedRequestID+"'"); //PROBABLY USE DECLINED INSTEAD OF AVAILABLE, IF ROOM IS NOT RESERVED, IT IS AVAILABLE LOGIC
                Toast.makeText(getContext(), "Request "+selectedRequestID+" has been denied", Toast.LENGTH_SHORT).show();
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
}
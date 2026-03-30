package com.example.booklibraryapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.util.List;

public class AdminViewBookRequestsPage extends Fragment {

    ListView listAdminViewBookRequests;

    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_admin_view_book_requests_page, container, false);
        // Correct ID (VERY IMPORTANT)
        listAdminViewBookRequests = view.findViewById(R.id.listAdminViewBookRequests);
        List<String> pendingBooksReservedQuery = QueryConnectorPlusHelper.getPendingBooksReservedQuery();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, pendingBooksReservedQuery);
        listAdminViewBookRequests.setAdapter(adapter);
        // Click logic
        listAdminViewBookRequests.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedRequestID = (String) parent.getItemAtPosition(position);
            String bookID = QueryConnectorPlusHelper.getBookIDFromBorrowedBooksID(selectedRequestID);
            String userID = QueryConnectorPlusHelper.getUserIDFromBorrowedBooksID(selectedRequestID);
            String bookTitle = QueryConnectorPlusHelper.getBookTitleFromBookID(bookID);
            String userFullName = (QueryConnectorPlusHelper.getFirstNameFromIDQuery(userID) + " " + QueryConnectorPlusHelper.getLastNameFromIDQuery(userID));
            String userEmail = QueryConnectorPlusHelper.getEmailFromIDQuery(userID);
            String dateBorrowing = QueryConnectorPlusHelper.getDateFromBorrowedBooksID(bookID);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            dialogBuilder.setTitle("Book Request " + selectedRequestID);
            dialogBuilder.setMessage(
                    "Book ID: " + bookID +
                            "\nBook Title: " + bookTitle +
                            "\nUser ID: " + userID +
                            "\nFull Name: " + userFullName +
                            "\nEmail: " + userEmail +
                            "\nDate Borrowing: " + dateBorrowing +
                            "\n\nApprove?");

            dialogBuilder.setPositiveButton("Yes", (dialog, which) ->
            {
                QueryConnectorPlusHelper.runQuery("UPDATE BOOKS_RESERVED SET RESERVE_STATUS = 'Reserved' WHERE ID = '" + selectedRequestID + "'");
                Toast.makeText(getContext(), "Request " + selectedRequestID + " is successfully approved", Toast.LENGTH_SHORT).show();
            });

            dialogBuilder.setNegativeButton("No", (dialog, which) ->
            {
                QueryConnectorPlusHelper.runQuery("UPDATE BOOKS_RESERVED SET RESERVE_STATUS = 'Available' WHERE ID = '" + selectedRequestID + "'"); //PROBABLY USE DECLINED INSTEAD OF AVAILABLE, IF ROOM IS NOT RESERVED, IT IS AVAILABLE LOGIC
                Toast.makeText(getContext(), "Request " + selectedRequestID + " has been denied", Toast.LENGTH_SHORT).show();
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
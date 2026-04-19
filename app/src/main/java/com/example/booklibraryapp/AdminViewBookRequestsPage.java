package com.example.booklibraryapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
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
        listAdminViewBookRequests = view.findViewById(R.id.listAdminViewBookRequests);
        
        refreshList();

        // FR10 Admin book request approval
        listAdminViewBookRequests.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            // Parse the ID from "Request #ID: First Last - BookName"
            String selectedRequestID = selectedItem.substring(selectedItem.indexOf("#") + 1, selectedItem.indexOf(":"));
            
            String bookID = QueryConnectorPlusHelper.getBookIDFromBorrowedBooksID(selectedRequestID);
            String userID = QueryConnectorPlusHelper.getUserIDFromBorrowedBooksID(selectedRequestID);
            String bookTitle = QueryConnectorPlusHelper.getBookTitleFromBookID(bookID);
            String userFullName = (QueryConnectorPlusHelper.getFirstNameFromIDQuery(userID) + " " + QueryConnectorPlusHelper.getLastNameFromIDQuery(userID));
            String userEmail = QueryConnectorPlusHelper.getEmailFromIDQuery(userID);
            String dateBorrowing = QueryConnectorPlusHelper.getDateFromBorrowedBooksID(selectedRequestID);
            
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
                QueryConnectorPlusHelper.runQuery("UPDATE BOOKS_BORROWED SET RESERVE_STATUS = 'Reserved' WHERE ID = '" + selectedRequestID + "'", () -> {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Request " + selectedRequestID + " is successfully approved", Toast.LENGTH_SHORT).show();
                            refreshList();
                        });
                    }
                });
            });

            // FR10 Admin book request decline
            dialogBuilder.setNegativeButton("No", (dialog, which) ->
            {
                // When denying, we should also return the book to stock
                QueryConnectorPlusHelper.runQuery("UPDATE BOOKS SET QUANTITY_AVAILABLE = QUANTITY_AVAILABLE + 1, QUANTITY_BORROWED = QUANTITY_BORROWED - 1 WHERE ID = '" + bookID + "'", () -> {
                    QueryConnectorPlusHelper.runQuery("UPDATE BOOKS_BORROWED SET RESERVE_STATUS = 'Denied' WHERE ID = '" + selectedRequestID + "'", () -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Request " + selectedRequestID + " has been denied", Toast.LENGTH_SHORT).show();
                                refreshList();
                            });
                        }
                    });
                });
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

    private void refreshList() {
        List<String> pendingBooksReservedQuery = QueryConnectorPlusHelper.getPendingBooksReservedWithDetailsQuery();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, pendingBooksReservedQuery);
        listAdminViewBookRequests.setAdapter(adapter);
    }
}

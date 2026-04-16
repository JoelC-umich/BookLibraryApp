package com.example.booklibraryapp;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.booklibraryapp.databinding.FragmentUserViewReservedBooksPageBinding;
import java.util.ArrayList;
import java.util.List;

public class UserViewReservedBooksPage extends Fragment
{
    ListView listViewReservedBooks;
    SearchView searchUserViewReservedBooks;
    private FragmentUserViewReservedBooksPageBinding binding;
    String loggedInUserID = QueryConnectorPlusHelper.IDWhenLoggingIn;
    
    List<String> detailedBookData = new ArrayList<>();
    List<String> displayNames = new ArrayList<>();
    ArrayAdapter<String> adapter;

    public UserViewReservedBooksPage() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentUserViewReservedBooksPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        listViewReservedBooks = view.findViewById(R.id.listViewUserViewReservedBooks);
        searchUserViewReservedBooks = view.findViewById(R.id.searchUserViewReservedBooks);
        
        refreshList();

        listViewReservedBooks.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            
            // Find the corresponding detailed record
            String record = null;
            for (String r : detailedBookData) {
                String[] parts = r.split(";;;");
                if (parts.length >= 4) {
                    String displayName = parts[2] + " (" + parts[3] + ")";
                    if (displayName.equals(selectedItem)) {
                        record = r;
                        break;
                    }
                }
            }

            if (record != null) {
                String[] parts = record.split(";;;");
                String borrowedID = parts[0];
                String bookID = parts[1];
                String bookName = parts[2];
                String status = parts[3];
                String date = parts.length > 4 ? parts[4] : "N/A";

                if (getContext() == null) return;
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                dialogBuilder.setTitle("Book Details");
                dialogBuilder.setMessage("Book: " + bookName + 
                                       "\nBorrowed Date: " + (date != null && !date.equals("null") ? date : "N/A") + 
                                       "\nStatus: " + status);

                if ("Reserved".equals(status)) {
                    dialogBuilder.setPositiveButton("Return Book", (dialog, which) -> {
                        QueryConnectorPlusHelper.returnBook(borrowedID, bookID, () -> {
                            if (isAdded() && getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), "Book " + bookName + " returned successfully", Toast.LENGTH_SHORT).show();
                                    refreshList();
                                });
                            }
                        });
                    });
                } else if ("Pending".equals(status)) {
                    dialogBuilder.setPositiveButton("Cancel Request", (dialog, which) -> {
                        QueryConnectorPlusHelper.returnBook(borrowedID, bookID, () -> {
                            if (isAdded() && getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), "Reservation request canceled", Toast.LENGTH_SHORT).show();
                                    refreshList();
                                });
                            }
                        });
                    });
                }

                dialogBuilder.setNegativeButton("Close", (dialog, which) -> dialog.dismiss());
                dialogBuilder.show();
            }
        });

        searchUserViewReservedBooks.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void refreshList() {
        QueryConnectorPlusHelper.executor.execute(() -> {
            List<String> data = QueryConnectorPlusHelper.getBorrowedBooksDetailedQuery(loggedInUserID);
            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    detailedBookData = data;
                    displayNames.clear();
                    for (String record : detailedBookData) {
                        String[] parts = record.split(";;;");
                        if (parts.length >= 4) {
                            displayNames.add(parts[2] + " (" + parts[3] + ")");
                        }
                    }
                    if (getContext() != null) {
                        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, displayNames);
                        listViewReservedBooks.setAdapter(adapter);
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

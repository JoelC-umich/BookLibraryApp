package com.example.booklibraryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class AdminViewBookRequestsPage extends Fragment {

    private ListView bookRequestsListView;

    private String[] bookRequests = {
            "The History Boys - Requested by User1",
            "What If It's Us - Requested by User2",
            "The Liar's Dictionary - Requested by User3"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_admin_view_book_requests_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Correct ID (VERY IMPORTANT)
        bookRequestsListView = view.findViewById(R.id.bookRequestsList);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                bookRequests
        );

        bookRequestsListView.setAdapter(adapter);

        // Click logic
        bookRequestsListView.setOnItemClickListener((parent, v, position, id) -> {
            Toast.makeText(requireContext(),
                    "Selected: " + bookRequests[position],
                    Toast.LENGTH_SHORT).show();
        });
    }
}
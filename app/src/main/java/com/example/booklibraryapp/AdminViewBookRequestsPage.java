package com.example.booklibraryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class AdminViewBookRequestsPage extends Fragment {

    ListView listView;

    String[] bookRequests = {
            "The History Boys - Requested by User1",
            "What If It's Us - Requested by User2",
            "The Liar's Dictionary - Requested by User3"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_view_book_requests_page, container, false);

        listView = view.findViewById(R.id.requestList);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                bookRequests
        );

        listView.setAdapter(adapter);

        return view;
    }
}
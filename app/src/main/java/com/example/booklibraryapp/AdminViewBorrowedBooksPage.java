package com.example.booklibraryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class AdminViewBorrowedBooksPage extends Fragment {

    ListView listView;

    String[] borrowedBooks = {
            "The History Boys - Borrowed by User1",
            "What If It's Us - Borrowed by User2",
            "The Liar's Dictionary - Borrowed by User3"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_view_borrowed_books_page, container, false);

        listView = view.findViewById(R.id.borrowedList);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                borrowedBooks
        );

        listView.setAdapter(adapter);

        return view;
    }
}
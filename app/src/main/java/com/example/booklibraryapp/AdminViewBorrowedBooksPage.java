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

public class AdminViewBorrowedBooksPage extends Fragment {

    private ListView borrowedBooksListView;

    private String[] borrowedBooks = {
            "The History Boys - Borrowed by User1",
            "What If It's Us - Borrowed by User2",
            "The Liar's Dictionary - Borrowed by User3"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_admin_view_borrowed_books_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Correct ID (VERY IMPORTANT)
        borrowedBooksListView = view.findViewById(R.id.borrowedBooksList);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                borrowedBooks
        );

        borrowedBooksListView.setAdapter(adapter);

        // Click logic
        borrowedBooksListView.setOnItemClickListener((parent, v, position, id) -> {
            Toast.makeText(requireContext(),
                    "Borrowed: " + borrowedBooks[position],
                    Toast.LENGTH_SHORT).show();
        });
    }
}
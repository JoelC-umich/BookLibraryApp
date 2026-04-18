package com.example.booklibraryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.booklibraryapp.databinding.FragmentAdminViewBorrowedBooksPageBinding;

import java.util.List;

public class AdminViewBorrowedBooksPage extends Fragment {

    ListView listAdminViewBorrowedBooks;
    SearchView searchAdminViewBorrowedBooks;
    private FragmentAdminViewBorrowedBooksPageBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentAdminViewBorrowedBooksPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // FR11 Admin view borrowed books details
        listAdminViewBorrowedBooks = view.findViewById(R.id.listAdminViewBorrowedBooks);
        searchAdminViewBorrowedBooks = view.findViewById(R.id.searchAdminViewBorrowedBooks);
        List<String> borrowedBooks = QueryConnectorPlusHelper.getBorrowedBooksWithDetailsQuery();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, borrowedBooks);
        listAdminViewBorrowedBooks.setAdapter(adapter);

        searchAdminViewBorrowedBooks.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
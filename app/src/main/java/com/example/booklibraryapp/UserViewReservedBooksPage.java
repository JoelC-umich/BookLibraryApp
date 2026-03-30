package com.example.booklibraryapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import com.example.booklibraryapp.databinding.FragmentUserViewReservedBooksPageBinding;
import java.util.List;

public class UserViewReservedBooksPage extends Fragment
{
    ListView listViewReservedBooks;
    SearchView searchUserViewReservedBooks;
    private FragmentUserViewReservedBooksPageBinding binding;
    String loggedInUserID = QueryConnectorPlusHelper.IDWhenLoggingIn;

    public UserViewReservedBooksPage()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

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
        List<String> reservedBookNamesQuery = QueryConnectorPlusHelper.getReservedBookNamesFromUserQuery(loggedInUserID);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, reservedBookNamesQuery);
        listViewReservedBooks.setAdapter(adapter);
        searchUserViewReservedBooks.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
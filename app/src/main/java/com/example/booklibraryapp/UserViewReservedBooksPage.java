package com.example.booklibraryapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

public class UserViewReservedBooksPage extends Fragment
{
    ListView listViewReservedBooks;
    TextView valueUserBookQuantityReserved, valueUserBookQuantityAvailable;
    public UserViewReservedBooksPage()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_view_reserved_books_page, container, false);
        listViewReservedBooks = view.findViewById(R.id.listViewUserViewReservedBooks);
        valueUserBookQuantityReserved = view.findViewById(R.id.valueUserBookQuantityReserved);
        valueUserBookQuantityAvailable = view.findViewById(R.id.valueUserBookQuantityAvailable);
        valueUserBookQuantityReserved.setText("0");
        valueUserBookQuantityAvailable.setText("0");
        List<String> reservedBookNamesQuery = QueryConnectorPlusHelper.getReservedBookNamesQuery();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, reservedBookNamesQuery);
        listViewReservedBooks.setAdapter(adapter);
        listViewReservedBooks.setOnItemClickListener((parent, view1, position, id) ->
        {
            String bookSelected = (String) parent.getItemAtPosition(position);
            String bookID = QueryConnectorPlusHelper.getBookIDFromBookNameQuery(bookSelected);
            String quantityAvailable = QueryConnectorPlusHelper.getQuantityAvailableFromBookID(bookID);
            String quantityReserved = QueryConnectorPlusHelper.getQuantityBorrowedFromBookID(bookID);
            valueUserBookQuantityReserved.setText(quantityReserved);
            valueUserBookQuantityAvailable.setText(quantityAvailable);
        });
        return view;
    }
}
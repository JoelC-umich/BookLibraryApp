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
        List<String> bookNames = QueryConnectorPlusHelper.getBookNamesReservedPendingPlusStatusFromUserQuery(loggedInUserID);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, bookNames);
        listViewReservedBooks.setAdapter(adapter);

//        listViewReservedBooks.setOnItemClickListener((parent, view1, position, id) -> {
//            String selectedBookName = (String) parent.getItemAtPosition(position);
//            String bookID = QueryConnectorPlusHelper.getBookIDFromBookNameQuery(selectedBookName);
//            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
//            dialogBuilder.setTitle("Return Book Selected?");
//            dialogBuilder.setMessage("Would you like to return "+selectedBookName+"?");
//
//            dialogBuilder.setPositiveButton("Yes", (dialog, which) ->
//            {
//                QueryConnectorPlusHelper.runQuery("UPDATE BOOKS_BORROWED SET RESERVE_STATUS = 'Returned' WHERE BOOK_ID = '"+bookID+"' AND USER_ID = '"+loggedInUserID+"'");
//                Toast.makeText(getContext(), "Book "+selectedBookName+" is successfully returned\nThank you!", Toast.LENGTH_SHORT).show();
//            });
//
//            dialogBuilder.setNegativeButton("No", (dialog, which) ->
//            {
//                dialog.dismiss();
//            });
//
//            dialogBuilder.setNeutralButton("Cancel", (dialog, which) ->
//            {
//                dialog.dismiss();
//            });
//
//            AlertDialog handleRoomRequestDialog = dialogBuilder.create();
//            handleRoomRequestDialog.show();
//            //FIND A WAY TO REFRESH THE VIEW AFTER APPROVING/DECLINING REQUESTS
//        });

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
package com.example.booklibraryapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import java.util.List;

public class UserReserveBookPage extends Fragment
{
    ListView listUserReserveBookPage;
    SearchView searchUserReserveBookPage;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_user_reserve_book_page, container, false);
        listUserReserveBookPage = view.findViewById(R.id.listUserReserveBookPage);
        searchUserReserveBookPage = view.findViewById(R.id.searchUserReserveBookPage);
        
        List<String> availableBooks = QueryConnectorPlusHelper.getAvailableBooksQuery();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, availableBooks);
        listUserReserveBookPage.setAdapter(adapter);

        searchUserReserveBookPage.setOnQueryTextListener(new SearchView.OnQueryTextListener()
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

        listUserReserveBookPage.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            // Extract book name from "Book Name by Author"
            String bookName = selectedItem;
            if (selectedItem.contains(" by ")) {
                bookName = selectedItem.substring(0, selectedItem.lastIndexOf(" by "));
            }
            
            Bundle availableBookSelected = new Bundle();
            availableBookSelected.putString("availableBook", bookName);
            getParentFragmentManager().setFragmentResult("availableBookInfo", availableBookSelected);
            NavHostFragment.findNavController(UserReserveBookPage.this).navigate(R.id.action_userReserveBookPage_to_userReserveBookResultsPage);
        });
        
        return view;
    }
}
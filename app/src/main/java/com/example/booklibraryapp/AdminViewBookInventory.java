package com.example.booklibraryapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.List;

public class AdminViewBookInventory extends Fragment {
    ListView listViewAdminViewBookInventory;
    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_view_book_inventory, container, false);

        listViewAdminViewBookInventory = view.findViewById(R.id.listViewAdminViewBookInventory);
        List<String> bookNamesQuery = QueryConnectorPlusHelper.getBookNamesQuery();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, bookNamesQuery);
        listViewAdminViewBookInventory.setAdapter(adapter);
        listViewAdminViewBookInventory.setOnItemClickListener((parent, view1, position, id) -> {
            String item = (String) parent.getItemAtPosition(position);
            Bundle bookNameSelected = new Bundle();
            bookNameSelected.putString("bookName", item.toString());
            getParentFragmentManager().setFragmentResult("bookNameInfo", bookNameSelected);
            NavHostFragment.findNavController(AdminViewBookInventory.this).navigate(R.id.action_adminViewBookInventory_to_adminViewBookInventoryEditBook);
        });
        return view;
    }
}
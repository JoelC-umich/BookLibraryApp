package com.example.booklibraryapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import com.example.booklibraryapp.databinding.FragmentAdminViewRoomsBinding;
import java.util.List;

public class AdminViewRooms extends Fragment
{
    ListView listViewAdminViewRooms;
    SearchView searchAdminViewRooms;
    private FragmentAdminViewRoomsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState)
    { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentAdminViewRoomsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        listViewAdminViewRooms = view.findViewById(R.id.listViewAdminViewRooms);
        searchAdminViewRooms = view.findViewById(R.id.searchAdminViewRooms);
        searchAdminViewRooms.setInputType(InputType.TYPE_CLASS_NUMBER);
        List<String> roomIDsQuery = QueryConnectorPlusHelper.getRoomIDsQuery();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, roomIDsQuery);
        listViewAdminViewRooms.setAdapter(adapter);

        searchAdminViewRooms.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
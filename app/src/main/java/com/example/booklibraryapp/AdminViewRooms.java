package com.example.booklibraryapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.booklibraryapp.databinding.FragmentAdminViewRoomsBinding;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class AdminViewRooms extends Fragment
{

    ListView listViewAdminViewRooms;
    TextView valueAdminViewRoomSelected;
    TextInputEditText inputAdminViewRoomSelectedChangeValue;
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
        valueAdminViewRoomSelected = view.findViewById(R.id.valueAdminViewRoomSelected);
        inputAdminViewRoomSelectedChangeValue = view.findViewById(R.id.inputAdminViewRoomSelectedChangeValue);
        listViewAdminViewRooms = view.findViewById(R.id.listViewAdminViewRooms);
        List<String> roomIDsQuery = QueryConnectorPlusHelper.getRoomIDsQuery();
        valueAdminViewRoomSelected.setText(null);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, roomIDsQuery);
        listViewAdminViewRooms.setAdapter(adapter);
        listViewAdminViewRooms.setOnItemClickListener((parent, view1, position, id) ->
        {
            String item = (String) parent.getItemAtPosition(position);
            valueAdminViewRoomSelected.setText(item);
        });

        binding.btnAdminViewRoomsEdit.setOnClickListener(v ->
        {
            String valueRoomSelected = valueAdminViewRoomSelected.getText().toString();
            String inputRoomSelected = inputAdminViewRoomSelectedChangeValue.getText().toString();
            if (valueRoomSelected.isBlank())
            {
                Toast.makeText(getContext(), "Please select a room number to change", Toast.LENGTH_SHORT).show();
            }
            else if(inputRoomSelected.isBlank())
            {
                Toast.makeText(getContext(), "Please enter a new room number value", Toast.LENGTH_SHORT).show();
            }
            else if (QueryConnectorPlusHelper.getRoomIDsQuery().contains(inputAdminViewRoomSelectedChangeValue.getText().toString()))
            {
                Toast.makeText(getContext(), "The number chosen exists\nPlease enter a different room number", Toast.LENGTH_SHORT).show();
            }
            else
            {
                QueryConnectorPlusHelper.runQuery("UPDATE ROOMS SET ID = '"+inputRoomSelected+"' WHERE ID = '"+valueRoomSelected+"'");
                Toast.makeText(getContext(), "Room number successfully updated", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(AdminViewRooms.this).navigate(R.id.action_adminViewRooms_to_AdminPage);
                valueAdminViewRoomSelected.setText("");
                inputAdminViewRoomSelectedChangeValue.getText().clear();
            }
        });
    }
}
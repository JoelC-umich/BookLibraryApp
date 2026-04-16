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
import android.widget.SearchView;
import java.util.ArrayList;
import java.util.List;

public class AdminAccountsInfoPage extends Fragment
{
    ListView userSearchResult;
    SearchView userSearchInput;
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_admin_accounts_info_page, container, false);
        userSearchResult = view.findViewById(R.id.userSearchResult);
        userSearchInput = view.findViewById(R.id.userSearchInput);

        refreshList();

        userSearchInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        userSearchResult.setOnItemClickListener((parent, view1, position, id) ->
        {
            String selectedItem = (String) parent.getItemAtPosition(position);
            // Extract the username from the display string "FirstName LastName (Username)"
            int startIndex = selectedItem.lastIndexOf("(") + 1;
            int endIndex = selectedItem.lastIndexOf(")");
            if (startIndex > 0 && endIndex > startIndex) {
                String username = selectedItem.substring(startIndex, endIndex);
                
                Bundle userNameSelected = new Bundle();
                userNameSelected.putString("Username", username);
                getParentFragmentManager().setFragmentResult("userNameInfo", userNameSelected);
                NavHostFragment.findNavController(AdminAccountsInfoPage.this).navigate(R.id.action_adminAccountsInfoPage_to_adminChangeAccounInfoforUserPage);
            }
        });

        return view;
    }

    private void refreshList() {
        QueryConnectorPlusHelper.executor.execute(() -> {
            List<String> rawUserData = QueryConnectorPlusHelper.getUsersFullNamesWithUsernamesQuery();
            List<String> displayList = new ArrayList<>();
            for (String user : rawUserData) {
                String[] parts = user.split(";;;");
                if (parts.length >= 2) {
                    // Display format: First Last (Username)
                    displayList.add(parts[0] + " (" + parts[1] + ")");
                }
            }
            
            if (isAdded() && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (getContext() != null) {
                        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, displayList);
                        userSearchResult.setAdapter(adapter);
                    }
                });
            }
        });
    }
}

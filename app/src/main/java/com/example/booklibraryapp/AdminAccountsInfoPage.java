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

public class AdminAccountsInfoPage extends Fragment
{
    ListView userSearchResult;

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
        List<String> usernamesQuery = QueryConnectorPlusHelper.getUsernamesQuery();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, usernamesQuery);
        userSearchResult.setAdapter(adapter);
        userSearchResult.setOnItemClickListener((parent, view1, position, id) ->
        {
            String userSelected = (String) parent.getItemAtPosition(position);
            Bundle userNameSelected = new Bundle();
            userNameSelected.putString("Username", userSelected);
            getParentFragmentManager().setFragmentResult("userNameInfo", userNameSelected);
            NavHostFragment.findNavController(AdminAccountsInfoPage.this).navigate(R.id.action_adminAccountsInfoPage_to_adminChangeAccounInfoforUserPage);
        });

        return view;
    }
}
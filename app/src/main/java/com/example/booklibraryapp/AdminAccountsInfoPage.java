package com.example.booklibraryapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.booklibraryapp.databinding.FragmentAdminAccountsInfoPageBinding;
import java.util.ArrayList;
import java.util.List;

public class AdminAccountsInfoPage extends Fragment {

    RecyclerView recyclerView;
    AdapterForView.Adapter adapter;
    public AdminAccountsInfoPage()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_accounts_info_page, container, false);

        recyclerView = view.findViewById(R.id.userSearchResult);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // External list of Strings
        List<String> externalList = QueryConnectorPlusHelper.getUsernamesQuery();

        // Convert to stringStencil list
        List<AdapterForView.stringStencil> converted = new ArrayList<>();
        for (String s : externalList) {
            converted.add(new AdapterForView.stringStencil(s));
        }

        // Use converted list
        adapter = new AdapterForView.Adapter(converted, (item, position) -> {
            Bundle userNameSelected = new Bundle();
            userNameSelected.putString("Username", item.getClickedItem());
            getParentFragmentManager().setFragmentResult("userNameInfo", userNameSelected);

            NavHostFragment.findNavController(AdminAccountsInfoPage.this).navigate(R.id.action_adminAccountsInfoPage_to_adminChangeAccounInfoforUserPage);
        });

        recyclerView.setAdapter(adapter);

        return view;
    }
}
package com.example.booklibraryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.booklibraryapp.databinding.AdminPageBinding;

public class AdminPage extends Fragment {

    private AdminPageBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = AdminPageBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnAdminViewAccounts.setOnClickListener(v ->
                NavHostFragment.findNavController(AdminPage.this)
                        .navigate(R.id.action_AdminPage_to_adminAccountsInfoPage)
        );

        binding.btnAdminViewBorrowedBooks.setOnClickListener(v ->
                NavHostFragment.findNavController(AdminPage.this)
                        .navigate(R.id.action_AdminPage_to_adminViewBorrowedBooksPage)
        );

        binding.btnAdminViewRoomReservations.setOnClickListener(v ->
                NavHostFragment.findNavController(AdminPage.this)
                        .navigate(R.id.action_AdminPage_to_adminViewRoomRequestsPage)
        );

        binding.btnAdminViewRoomRequests.setOnClickListener(v ->
                NavHostFragment.findNavController(AdminPage.this)
                        .navigate(R.id.action_AdminPage_to_adminViewRoomRequestsPage)
        );

        binding.btnAdminViewBookRequests.setOnClickListener(v ->
                NavHostFragment.findNavController(AdminPage.this)
                        .navigate(R.id.action_AdminPage_to_adminViewBookRequestsPage)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
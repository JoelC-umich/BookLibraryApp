package com.example.booklibraryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.booklibraryapp.databinding.FragmentUserPageBinding;

public class UserPage extends Fragment {

    private FragmentUserPageBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentUserPageBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnUserPageReserveBook.setOnClickListener(v ->
                NavHostFragment.findNavController(UserPage.this)
                        .navigate(R.id.action_userPage_to_userReserveBookPage)
        );

        binding.btnUserPageReserveRoom.setOnClickListener(v ->
                NavHostFragment.findNavController(UserPage.this)
                        .navigate(R.id.action_userPage_to_userReserveRoomPage)
        );

        binding.btnUserPageViewReservedBooks.setOnClickListener(v ->
                NavHostFragment.findNavController(UserPage.this)
                        .navigate(R.id.action_userPage_to_userViewReservedBooksPage)
        );

        binding.btnUserPageViewRoomReservation.setOnClickListener(v ->
                NavHostFragment.findNavController(UserPage.this)
                        .navigate(R.id.action_userPage_to_userViewReservedRoomsPage)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
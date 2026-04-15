package com.example.booklibraryapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.booklibraryapp.databinding.FragmentUserPageBinding;

public class UserPage extends Fragment {

    private FragmentUserPageBinding binding;
    TextView textUserWelcomeText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when UserPage is at the top of the back stack
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                logout();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState
    )
    {
        binding = FragmentUserPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        String userID = QueryConnectorPlusHelper.IDWhenLoggingIn;
        textUserWelcomeText = view.findViewById(R.id.textUserWelcome);
        String firstUserName = QueryConnectorPlusHelper.getFirstNameFromIDQuery(userID);
        textUserWelcomeText.setText("Welcome "+firstUserName+"!");

        binding.btnUserPageReserveBook.setOnClickListener(v ->
                NavHostFragment.findNavController(UserPage.this).navigate(R.id.action_userPage_to_userReserveBookPage)
        );

        binding.btnUserPageReserveRoom.setOnClickListener(v ->
                NavHostFragment.findNavController(UserPage.this).navigate(R.id.action_userPage_to_userReserveRoomPage)
        );

        binding.btnUserPageViewReservedBooks.setOnClickListener(v ->
                NavHostFragment.findNavController(UserPage.this).navigate(R.id.action_userPage_to_userViewReservedBooksPage)
        );

        binding.btnUserPageViewRoomReservation.setOnClickListener(v ->
                NavHostFragment.findNavController(UserPage.this).navigate(R.id.action_userPage_to_userViewReservedRoomsPage)
        );

        binding.btnUserLogout.setOnClickListener(v -> {
            logout();
        });
    }

    private void logout() {
        Toast.makeText(requireContext(), "You are successfully logged out", Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(UserPage.this).navigate(R.id.action_userPage_to_LoginPage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
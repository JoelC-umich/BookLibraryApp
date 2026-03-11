package com.example.booklibraryapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.booklibraryapp.databinding.LoginPageBinding;
import com.google.android.material.textfield.TextInputEditText;

public class LoginPage extends Fragment {
    private LoginPageBinding binding;

    TextInputEditText userInputUsername, userInputPassword;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {
        binding = LoginPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userInputUsername = view.findViewById(R.id.inputLoginUsername);
        userInputPassword = view.findViewById(R.id.inputLoginPassword);
        binding.btnLogin.setOnClickListener(v -> {
            String username = userInputUsername.getText().toString();
            String password = userInputPassword.getText().toString();

            int listIndex = QueryConnectorPlusHelper.getUsernamesQuery().indexOf(username);
            if(listIndex != -1) //no username exists in any index of list
            {
                String ID = QueryConnectorPlusHelper.getUsernameIDQuery(username);
                if (QueryConnectorPlusHelper.getPasswordFromID(ID).equals(password))
                {
                    if(QueryConnectorPlusHelper.getUserTypeFromID(ID).equals("User"))
                    {
                        NavHostFragment.findNavController(LoginPage.this).navigate(R.id.action_LoginPage_to_userPage);
                    } else if (QueryConnectorPlusHelper.getUserTypeFromID(ID).equals("Admin"))
                    {
                        NavHostFragment.findNavController(LoginPage.this).navigate(R.id.action_LoginPage_to_AdminPage);
                    }

                }
                else
                {
                    Toast.makeText(getContext(), "Username or Password is incorrect\nPlease try again", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getContext(), "Username does not exist\nPlease try again", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnLoginCreateAccount.setOnClickListener(v -> {
            NavHostFragment.findNavController(LoginPage.this)
                    .navigate(R.id.action_LoginPage_to_createUserPage);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
package com.example.booklibraryapp;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.booklibraryapp.databinding.LoginPageBinding;
import com.google.android.material.textfield.TextInputEditText;

public class LoginPage extends Fragment {
    private LoginPageBinding binding;

    TextInputEditText userInputUsername, userInputPassword;
    CheckBox checkLoginPageShowPassword;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = LoginPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        userInputUsername = view.findViewById(R.id.inputLoginUsername);
        userInputPassword = view.findViewById(R.id.inputLoginPassword);
        checkLoginPageShowPassword = view.findViewById(R.id.checkLoginPageShowPassword);
        binding.btnLogin.setOnClickListener(v -> {
            String username = userInputUsername.getText().toString().toLowerCase();
            String password = userInputPassword.getText().toString();

            int listIndex = QueryConnectorPlusHelper.getUsernamesQuery().indexOf(username);
            if(listIndex != -1) //no username exists in any index of list
            {
                String ID = QueryConnectorPlusHelper.getUsernameIDQuery(username);
                if (QueryConnectorPlusHelper.getPasswordFromID(ID).equals(password))
                {
                    Bundle userInfoBundle = new Bundle();
                    userInfoBundle.putString("User_ID", ID);
                    getParentFragmentManager().setFragmentResult("userInfo", userInfoBundle);
                    QueryConnectorPlusHelper.IDWhenLoggingIn = ID;
                    if(QueryConnectorPlusHelper.getUserTypeFromID(ID).equals("User"))
                    {
                        userInputUsername.getText().clear();
                        userInputPassword.getText().clear();
                        NavHostFragment.findNavController(LoginPage.this).navigate(R.id.action_LoginPage_to_userPage);
                    } else if (QueryConnectorPlusHelper.getUserTypeFromID(ID).equals("Admin"))
                    {
                        userInputUsername.getText().clear();
                        userInputPassword.getText().clear();
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

        binding.btnLoginCreateAccount.setOnClickListener(v ->
        {
            NavHostFragment.findNavController(LoginPage.this).navigate(R.id.action_LoginPage_to_createUserPage);
        });

        binding.checkLoginPageShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userInputPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    userInputPassword.setSelection(userInputPassword.getText().length());
                }
                else {
                    userInputPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    userInputPassword.setSelection(userInputPassword.getText().length());
                }
            }
        });
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }

}
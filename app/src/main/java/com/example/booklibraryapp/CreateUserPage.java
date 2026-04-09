package com.example.booklibraryapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.booklibraryapp.databinding.FragmentCreateUserPageBinding;
import com.google.android.material.textfield.TextInputEditText;

public class CreateUserPage extends Fragment {
    private FragmentCreateUserPageBinding binding;

    public CreateUserPage()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentCreateUserPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        TextInputEditText inputCreateUserFirstName = view.findViewById(R.id.inputCreateUserFirstName);
        TextInputEditText inputCreateUserLastName = view.findViewById(R.id.inputCreateUserLastName);
        TextInputEditText inputCreateUserUsername = view.findViewById(R.id.inputCreateUserUsername);
        TextInputEditText inputCreateUserPassword = view.findViewById(R.id.inputCreateUserPassword);
        TextInputEditText inputCreateUserEmail = view.findViewById(R.id.inputCreateUserEmail);
        TextInputEditText inputCreateUserSchool = view.findViewById(R.id.inputCreateUserSchool);

        binding.btnCreateUser.setOnClickListener(v -> {
            String ID = QueryConnectorPlusHelper.getLastIDPlus1UsersQuery();
            String firstName = inputCreateUserFirstName.getText().toString();
            String lastName = inputCreateUserLastName.getText().toString();
            String userType = "User";
            String username = inputCreateUserUsername.getText().toString();
            String password = inputCreateUserPassword.getText().toString();
            String email = inputCreateUserEmail.getText().toString();
            String school = inputCreateUserSchool.getText().toString();

            if (!ValidationUtils.isNotEmpty(firstName) || !ValidationUtils.isNotEmpty(lastName) ||
                !ValidationUtils.isNotEmpty(username) || !ValidationUtils.isNotEmpty(password) ||
                !ValidationUtils.isNotEmpty(email)) {
                Toast.makeText(getContext(), "One of the fields are blank\nPlease fill and try again", Toast.LENGTH_SHORT).show();
            } else if (!ValidationUtils.isValidEmail(email)) {
                Toast.makeText(getContext(), "Invalid email format\nPlease enter a valid email", Toast.LENGTH_SHORT).show();
            } else if (QueryConnectorPlusHelper.getUsernamesQuery().toString().contains(username.toLowerCase())) {
                Toast.makeText(getContext(), "Username already exists\nPlease choose another username", Toast.LENGTH_SHORT).show();
            } else {
                QueryConnectorPlusHelper.runQuery("INSERT INTO USERS VALUES (" + ID + ", '" + firstName + "', '" + lastName + "', '" + userType + "', '" + email + "', '" + username + "', '" + password + "', '" + school + "')");
                Toast.makeText(getContext(), "Account Successfully Created", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(CreateUserPage.this).navigate(R.id.action_createUserPage_to_LoginPage);
            }
        });
    }
}

package com.example.booklibraryapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CheckBox;
import com.example.booklibraryapp.databinding.FragmentCreateUserPageAdminBinding;
import com.google.android.material.textfield.TextInputEditText;

public class CreateUserPageAdmin extends Fragment {
    private FragmentCreateUserPageAdminBinding binding;
    private CheckBox checkUserCheckbox, checkAdminCheckbox;
    private boolean isUserChecked = true; //initially checked true
    private boolean isAdminChecked = false; //initially checked false

    public CreateUserPageAdmin()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateUserPageAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        TextInputEditText inputCreateUserFirstNameAdmin = view.findViewById(R.id.inputCreateUserFirstNameAdmin);
        TextInputEditText inputCreateUserLastNameAdmin = view.findViewById(R.id.inputCreateUserLastNameAdmin);
        TextInputEditText inputCreateUserUsernameAdmin = view.findViewById(R.id.inputCreateUserUsernameAdmin);
        TextInputEditText inputCreateUserPasswordAdmin = view.findViewById(R.id.inputCreateUserPasswordAdmin);
        TextInputEditText inputCreateUserEmailAdmin = view.findViewById(R.id.inputCreateUserEmailAdmin);
        TextInputEditText inputCreateUserSchoolAdmin = view.findViewById(R.id.inputCreateUserSchoolAdmin);
        checkUserCheckbox = view.findViewById(R.id.checkAdminViewCreateAccountIsUser);
        checkAdminCheckbox = view.findViewById(R.id.checkAdminViewCreateAccountIsAdmin);
        
        binding.checkAdminViewCreateAccountIsUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isUserChecked = true;
                    checkAdminCheckbox.setChecked(false);
                }
                else {
                    isUserChecked = false;
                }
            }
        });

        binding.checkAdminViewCreateAccountIsAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isAdminChecked = true;
                    checkUserCheckbox.setChecked(false);
                }
                else {
                    isAdminChecked = false;
                }
            }
        });

        binding.btnCreateUserAdmin.setOnClickListener(v -> {
            String ID = QueryConnectorPlusHelper.getLastIDPlus1UsersQuery();
            String firstName = inputCreateUserFirstNameAdmin.getText().toString();
            String lastName = inputCreateUserLastNameAdmin.getText().toString();
            String username = inputCreateUserUsernameAdmin.getText().toString();
            String password = inputCreateUserPasswordAdmin.getText().toString();
            String email = inputCreateUserEmailAdmin.getText().toString();
            String school = inputCreateUserSchoolAdmin.getText().toString();
            String userType = null;
            if (isAdminChecked)
            {
                userType = "Admin";
            } else if (isUserChecked) {
                userType = "User";
            }

            if (!ValidationUtils.isNotEmpty(firstName) || !ValidationUtils.isNotEmpty(lastName) ||
                !ValidationUtils.isNotEmpty(username) || !ValidationUtils.isNotEmpty(password) ||
                !ValidationUtils.isNotEmpty(email)) {
                Toast.makeText(getContext(), "One of the fields are blank\nPlease fill and try again", Toast.LENGTH_SHORT).show();
            } else if (!ValidationUtils.isValidEmail(email)) {
                Toast.makeText(getContext(), "Invalid email format\nPlease enter a valid email", Toast.LENGTH_SHORT).show();
            } else if (QueryConnectorPlusHelper.getUsernamesQuery().toString().contains(username.toLowerCase())) {
                Toast.makeText(getContext(), "Username already exists\nPlease choose another username", Toast.LENGTH_SHORT).show();
            } else {
                QueryConnectorPlusHelper.runQuery("INSERT INTO USERS VALUES ("+ID+", '"+firstName+"', '"+lastName+"', '"+userType+"', '"+email+"', '"+username+"', '"+password+"', '"+school+"')");
                Toast.makeText(getContext(), "Account Successfully Created", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(CreateUserPageAdmin.this).navigate(R.id.action_createUserPageAdmin_to_AdminPage);
            }
        });
    }
}

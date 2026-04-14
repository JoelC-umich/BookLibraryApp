package com.example.booklibraryapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import com.example.booklibraryapp.databinding.FragmentAdminChangeAccountInfoforUserPageBinding;
import com.google.android.material.textfield.TextInputEditText;

public class AdminChangeAccountInfoforUserPage extends Fragment {
    private FragmentAdminChangeAccountInfoforUserPageBinding binding;
    String ID, FName, LName, type, email, username, password, school;
    String userLoggedIn = QueryConnectorPlusHelper.IDWhenLoggingIn;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentAdminChangeAccountInfoforUserPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextInputEditText userInfoUserID = view.findViewById(R.id.userInfoUserID);
        TextInputEditText userInfoUserFirstName = view.findViewById(R.id.userInfoUserFirstName);
        TextInputEditText userInfoUserLastName = view.findViewById(R.id.userInfoUserLastName);
        TextInputEditText userInfoUserType = view.findViewById(R.id.userInfoUserType);
        TextInputEditText userInfoUserEmail = view.findViewById(R.id.userInfoUserEmail);
        TextInputEditText userInfoUserUsername = view.findViewById(R.id.userInfoUserUsername);
        TextInputEditText userInfoUserPassword = view.findViewById(R.id.userInfoUserPassword);
        TextInputEditText userInfoUserSchool = view.findViewById(R.id.userInfoUserSchool);
        CheckBox checkUserInfoUserTypeUser = view.findViewById(R.id.checkAdminChangeAccountUserCheckbox);
        CheckBox checkUserInfoUserTypeAdmin = view.findViewById(R.id.checkAdminChangeAccountAdminCheckbox);
        getParentFragmentManager().setFragmentResultListener("userNameInfo", this, (requestKey, bundle) -> {
            String selectedUserName = bundle.getString("Username");
            String userID = QueryConnectorPlusHelper.getUsernameIDQuery(selectedUserName);
            String userFirstName = QueryConnectorPlusHelper.getFirstNameFromIDQuery(userID);
            String userLastName = QueryConnectorPlusHelper.getLastNameFromIDQuery(userID);
            String userType = QueryConnectorPlusHelper.getUserTypeFromID(userID);
            String userEmail = QueryConnectorPlusHelper.getEmailFromIDQuery(userID);
            String userPassword = QueryConnectorPlusHelper.getPasswordFromID(userID);
            String userSchool = QueryConnectorPlusHelper.getSchoolFromIDQuery(userID);
            userInfoUserID.setText(userID);
            userInfoUserFirstName.setText(userFirstName);
            userInfoUserLastName.setText(userLastName);
            userInfoUserType.setText(userType);
            userInfoUserEmail.setText(userEmail);
            userInfoUserUsername.setText(selectedUserName);
            userInfoUserPassword.setText(userPassword);
            userInfoUserSchool.setText(userSchool);

            if (userType.equals("User")){
                checkUserInfoUserTypeUser.setChecked(true);
                checkUserInfoUserTypeAdmin.setChecked(false);
            } else if (userType.equals("Admin")){
                checkUserInfoUserTypeAdmin.setChecked(true);
                checkUserInfoUserTypeUser.setChecked(false);
            }

        });

        binding.checkAdminChangeAccountUserCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkUserInfoUserTypeUser.setChecked(true);
                    checkUserInfoUserTypeAdmin.setChecked(false);
                    userInfoUserType.setText("User");
                }
            }
        });

        binding.checkAdminChangeAccountAdminCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkUserInfoUserTypeAdmin.setChecked(true);
                    checkUserInfoUserTypeUser.setChecked(false);
                    userInfoUserType.setText("Admin");
                }
            }
        });

        binding.btnUserInfoSave.setOnClickListener(v -> {
            if (userInfoUserID.getText().toString().isBlank() || userInfoUserFirstName.getText().toString().isBlank() || userInfoUserLastName.getText().toString().isBlank()
                    || userInfoUserEmail.getText().toString().isBlank() || userInfoUserUsername.getText().toString().isBlank() || userInfoUserPassword.getText().toString().isBlank()){
                Toast.makeText(getContext(), "Error saving information\nOne of the fields is blank or missing", Toast.LENGTH_SHORT).show();
            } else if (userInfoUserID.getText().toString().equals("0")) {
                if (checkUserInfoUserTypeUser.isChecked()){
                    Toast.makeText(getContext(), "Error saving information\nCannot change primary admin to user", Toast.LENGTH_SHORT).show();
                    userInfoUserType.setText("Admin");
                    checkUserInfoUserTypeUser.setChecked(false);
                    checkUserInfoUserTypeAdmin.setChecked(true);
                }
                else {
                    ID = userInfoUserID.getText().toString();
                    FName = userInfoUserFirstName.getText().toString();
                    LName = userInfoUserLastName.getText().toString();
                    type = userInfoUserType.getText().toString();
                    email = userInfoUserEmail.getText().toString();
                    username = userInfoUserUsername.getText().toString();
                    password = userInfoUserPassword.getText().toString();
                    school = userInfoUserSchool.getText().toString();
                    QueryConnectorPlusHelper.runQuery("UPDATE USERS SET FIRST_NAME = '"+FName+"', LAST_NAME = '"+LName+"', USER_TYPE = '"+type+"', USER_EMAIL = '"+email+"', USER_NAME = '"+username+"', USER_PASSWORD = '"+password+"', SCHOOL = '"+school+"' WHERE ID = '"+ID+"'");
                    Toast.makeText(getContext(), username + " information saved", Toast.LENGTH_SHORT).show();
                }

            } else if (userInfoUserID.getText().toString().equals(userLoggedIn)) {
                if (checkUserInfoUserTypeUser.isChecked()){
                    ID = userInfoUserID.getText().toString();
                    FName = userInfoUserFirstName.getText().toString();
                    LName = userInfoUserLastName.getText().toString();
                    type = userInfoUserType.getText().toString();
                    email = userInfoUserEmail.getText().toString();
                    username = userInfoUserUsername.getText().toString();
                    password = userInfoUserPassword.getText().toString();
                    school = userInfoUserSchool.getText().toString();
                    QueryConnectorPlusHelper.runQuery("UPDATE USERS SET FIRST_NAME = '"+FName+"', LAST_NAME = '"+LName+"', USER_TYPE = '"+type+"', USER_EMAIL = '"+email+"', USER_NAME = '"+username+"', USER_PASSWORD = '"+password+"', SCHOOL = '"+school+"' WHERE ID = '"+ID+"'");
                    Toast.makeText(getContext(), "Your information is saved\nLogging you out of admin view", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(AdminChangeAccountInfoforUserPage.this).navigate(R.id.action_adminChangeAccounInfoforUserPage_to_LoginPage);
                    userInfoUserID.getText().clear();
                    userInfoUserFirstName.getText().clear();
                    userInfoUserLastName.getText().clear();
                    userInfoUserType.getText().clear();
                    userInfoUserEmail.getText().clear();
                    userInfoUserUsername.getText().clear();
                    userInfoUserPassword.getText().clear();
                    userInfoUserSchool.getText().clear();
                } else{
                    ID = userInfoUserID.getText().toString();
                    FName = userInfoUserFirstName.getText().toString();
                    LName = userInfoUserLastName.getText().toString();
                    type = userInfoUserType.getText().toString();
                    email = userInfoUserEmail.getText().toString();
                    username = userInfoUserUsername.getText().toString();
                    password = userInfoUserPassword.getText().toString();
                    school = userInfoUserSchool.getText().toString();
                    QueryConnectorPlusHelper.runQuery("UPDATE USERS SET FIRST_NAME = '"+FName+"', LAST_NAME = '"+LName+"', USER_TYPE = '"+type+"', USER_EMAIL = '"+email+"', USER_NAME = '"+username+"', USER_PASSWORD = '"+password+"', SCHOOL = '"+school+"' WHERE ID = '"+ID+"'");
                    Toast.makeText(getContext(), username + " information saved", Toast.LENGTH_SHORT).show();
                }
            } else {
                ID = userInfoUserID.getText().toString();
                FName = userInfoUserFirstName.getText().toString();
                LName = userInfoUserLastName.getText().toString();
                type = userInfoUserType.getText().toString();
                email = userInfoUserEmail.getText().toString();
                username = userInfoUserUsername.getText().toString();
                password = userInfoUserPassword.getText().toString();
                school = userInfoUserSchool.getText().toString();
                QueryConnectorPlusHelper.runQuery("UPDATE USERS SET FIRST_NAME = '"+FName+"', LAST_NAME = '"+LName+"', USER_TYPE = '"+type+"', USER_EMAIL = '"+email+"', USER_NAME = '"+username+"', USER_PASSWORD = '"+password+"', SCHOOL = '"+school+"' WHERE ID = '"+ID+"'");
                Toast.makeText(getContext(), username + " information saved", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnAdminChangeAccountUserDelete.setOnClickListener(v -> {
            ID = userInfoUserID.getText().toString();
            username = userInfoUserUsername.getText().toString();
            if (ID.equals("0"))
            {
                Toast.makeText(getContext(), "Cannot delete primary admin account", Toast.LENGTH_SHORT).show();
            } else if (ID.equals(userLoggedIn)) {
                QueryConnectorPlusHelper.runQuery("DELETE FROM USERS WHERE ID = '"+ID+"'");
                Toast.makeText(getContext(), "Your account has been deleted successfully\nLogging you out", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(AdminChangeAccountInfoforUserPage.this).navigate(R.id.action_adminChangeAccounInfoforUserPage_to_LoginPage);
            } else {
                QueryConnectorPlusHelper.runQuery("DELETE FROM USERS WHERE ID = '"+ID+"'");
                Toast.makeText(getContext(), username + " has been deleted", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(AdminChangeAccountInfoforUserPage.this).navigate(R.id.action_adminChangeAccounInfoforUserPage_to_adminAccountsInfoPage);
                userInfoUserID.getText().clear();
                userInfoUserFirstName.getText().clear();
                userInfoUserLastName.getText().clear();
                userInfoUserType.getText().clear();
                userInfoUserEmail.getText().clear();
                userInfoUserUsername.getText().clear();
                userInfoUserPassword.getText().clear();
                userInfoUserSchool.getText().clear();
                checkUserInfoUserTypeUser.setChecked(false);
                checkUserInfoUserTypeAdmin.setChecked(false);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
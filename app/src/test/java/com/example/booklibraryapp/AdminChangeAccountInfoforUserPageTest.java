package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.textfield.TextInputEditText;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.annotation.Config;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class AdminChangeAccountInfoforUserPageTest {

    @Test
    public void testFragmentUIInitialization() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUsernameIDQuery(anyString())).thenReturn("1");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getFirstNameFromIDQuery("1")).thenReturn("John");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getLastNameFromIDQuery("1")).thenReturn("Doe");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUserTypeFromID("1")).thenReturn("User");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getEmailFromIDQuery("1")).thenReturn("john@example.com");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getPasswordFromID("1")).thenReturn("password");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getSchoolFromIDQuery("1")).thenReturn("UMD");

            try (FragmentScenario<AdminChangeAccountInfoforUserPage> scenario = FragmentScenario.launchInContainer(AdminChangeAccountInfoforUserPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Bundle result = new Bundle();
                    result.putString("Username", "johndoe");
                    fragment.getParentFragmentManager().setFragmentResult("userNameInfo", result);

                    TextInputEditText firstNameField = fragment.requireView().findViewById(R.id.userInfoUserFirstName);
                    assertNotNull(firstNameField);
                    assertEquals("John", firstNameField.getText().toString());
                    
                    Button saveBtn = fragment.requireView().findViewById(R.id.btnUserInfoSave);
                    assertNotNull(saveBtn);
                });
            }
        }
    }

    // FR2 Admin test User account update
    @Test
    public void testSaveInfoWithBlankFields() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUsernameIDQuery(anyString())).thenReturn("1");

            try (FragmentScenario<AdminChangeAccountInfoforUserPage> scenario = FragmentScenario.launchInContainer(AdminChangeAccountInfoforUserPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    TextInputEditText firstNameField = fragment.requireView().findViewById(R.id.userInfoUserFirstName);
                    Button saveBtn = fragment.requireView().findViewById(R.id.btnUserInfoSave);

                    firstNameField.setText(""); // Blank field

                    saveBtn.performClick();

                    // Verify update query is NEVER run
                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(anyString()), never());
                });
            }
        }
    }

    // FR2 Admin test User account update
    @Test
    public void testChangePrimaryAdminToUser() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            // Primary Admin has ID "0"
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUsernameIDQuery("admin")).thenReturn("0");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUserTypeFromID("0")).thenReturn("Admin");

            try (FragmentScenario<AdminChangeAccountInfoforUserPage> scenario = FragmentScenario.launchInContainer(AdminChangeAccountInfoforUserPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Bundle result = new Bundle();
                    result.putString("Username", "admin");
                    fragment.getParentFragmentManager().setFragmentResult("userNameInfo", result);

                    CheckBox userCheck = fragment.requireView().findViewById(R.id.checkAdminChangeAccountUserCheckbox);
                    Button saveBtn = fragment.requireView().findViewById(R.id.btnUserInfoSave);

                    userCheck.setChecked(true); // Try to change ID 0 to User
                    saveBtn.performClick();

                    // Verify update query is NEVER run for this case
                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(anyString()), never());
                });
            }
        }
    }

    // FR2 Admin test User account delete
    @Test
    public void testDeletePrimaryAdmin() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUsernameIDQuery("admin")).thenReturn("0");

            try (FragmentScenario<AdminChangeAccountInfoforUserPage> scenario = FragmentScenario.launchInContainer(AdminChangeAccountInfoforUserPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Bundle result = new Bundle();
                    result.putString("Username", "admin");
                    fragment.getParentFragmentManager().setFragmentResult("userNameInfo", result);

                    Button deleteBtn = fragment.requireView().findViewById(R.id.btnAdminChangeAccountUserDelete);
                    deleteBtn.performClick();

                    // Verify delete query is NEVER run
                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(org.mockito.ArgumentMatchers.contains("DELETE FROM USERS")), never());
                });
            }
        }
    }
}

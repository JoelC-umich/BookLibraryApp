package com.example.booklibraryapp;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import android.widget.Button;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.textfield.TextInputEditText;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class CreateUserPageTest {

    @Test
    public void testFragmentUIInitialization() {
        try (FragmentScenario<CreateUserPage> scenario = FragmentScenario.launchInContainer(CreateUserPage.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                assertNotNull(fragment.requireView().findViewById(R.id.inputCreateUserFirstName));
                assertNotNull(fragment.requireView().findViewById(R.id.inputCreateUserLastName));
                assertNotNull(fragment.requireView().findViewById(R.id.inputCreateUserUsername));
                assertNotNull(fragment.requireView().findViewById(R.id.inputCreateUserPassword));
                assertNotNull(fragment.requireView().findViewById(R.id.btnCreateUser));
            });
        }
    }

    // FR1 test User registration and account creation
    @Test
    public void testCreateUserAttemptSuccess() {
        NavController mockNavController = mock(NavController.class);
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getLastIDPlus1UsersQuery).thenReturn("10");
            mockedHelper.when(QueryConnectorPlusHelper::getUsernamesQuery).thenReturn(new ArrayList<>());

            try (FragmentScenario<CreateUserPage> scenario = FragmentScenario.launchInContainer(CreateUserPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);
                    
                    TextInputEditText firstName = fragment.requireView().findViewById(R.id.inputCreateUserFirstName);
                    TextInputEditText lastName = fragment.requireView().findViewById(R.id.inputCreateUserLastName);
                    TextInputEditText username = fragment.requireView().findViewById(R.id.inputCreateUserUsername);
                    TextInputEditText password = fragment.requireView().findViewById(R.id.inputCreateUserPassword);
                    TextInputEditText email = fragment.requireView().findViewById(R.id.inputCreateUserEmail);
                    Button createBtn = fragment.requireView().findViewById(R.id.btnCreateUser);

                    firstName.setText("John");
                    lastName.setText("Doe");
                    username.setText("johndoe");
                    password.setText("password123");
                    email.setText("john@example.com");

                    createBtn.performClick();
                    
                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(org.mockito.ArgumentMatchers.contains("INSERT INTO USERS")));
                    verify(mockNavController).navigate(R.id.action_createUserPage_to_LoginPage);
                });
            }
        }
    }

    // FR1 negative test User registration and account creation with blank fields
    @Test
    public void testCreateUserAttemptWithBlankFields() {
        NavController mockNavController = mock(NavController.class);
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getLastIDPlus1UsersQuery).thenReturn("10");
            mockedHelper.when(QueryConnectorPlusHelper::getUsernamesQuery).thenReturn(new ArrayList<>());

            try (FragmentScenario<CreateUserPage> scenario = FragmentScenario.launchInContainer(CreateUserPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);
                    
                    TextInputEditText firstName = fragment.requireView().findViewById(R.id.inputCreateUserFirstName);
                    Button createBtn = fragment.requireView().findViewById(R.id.btnCreateUser);

                    firstName.setText(""); // Blank

                    createBtn.performClick();
                    
                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(anyString()), never());
                    verify(mockNavController, never()).navigate(anyInt());
                });
            }
        }
    }

    // FR1 negative test User registration and account creation with same username in another account
    @Test
    public void testCreateUserAttemptWithExistingUsername() {
        NavController mockNavController = mock(NavController.class);
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getLastIDPlus1UsersQuery).thenReturn("10");
            mockedHelper.when(QueryConnectorPlusHelper::getUsernamesQuery).thenReturn(Arrays.asList("johndoe"));

            try (FragmentScenario<CreateUserPage> scenario = FragmentScenario.launchInContainer(CreateUserPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);
                    
                    TextInputEditText firstName = fragment.requireView().findViewById(R.id.inputCreateUserFirstName);
                    TextInputEditText lastName = fragment.requireView().findViewById(R.id.inputCreateUserLastName);
                    TextInputEditText username = fragment.requireView().findViewById(R.id.inputCreateUserUsername);
                    TextInputEditText password = fragment.requireView().findViewById(R.id.inputCreateUserPassword);
                    TextInputEditText email = fragment.requireView().findViewById(R.id.inputCreateUserEmail);
                    Button createBtn = fragment.requireView().findViewById(R.id.btnCreateUser);

                    firstName.setText("John");
                    lastName.setText("Doe");
                    username.setText("johndoe");
                    password.setText("password123");
                    email.setText("john@example.com");

                    createBtn.performClick();
                    
                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(anyString()), never());
                    verify(mockNavController, never()).navigate(anyInt());
                });
            }
        }
    }

    // FR1 negative test User registration and account creation with Invalid email address
    @Test
    public void testCreateUserAttemptWithInvalidEmail() {
        NavController mockNavController = mock(NavController.class);
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getLastIDPlus1UsersQuery).thenReturn("10");
            mockedHelper.when(QueryConnectorPlusHelper::getUsernamesQuery).thenReturn(new ArrayList<>());

            try (FragmentScenario<CreateUserPage> scenario = FragmentScenario.launchInContainer(CreateUserPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);
                    
                    TextInputEditText firstName = fragment.requireView().findViewById(R.id.inputCreateUserFirstName);
                    TextInputEditText lastName = fragment.requireView().findViewById(R.id.inputCreateUserLastName);
                    TextInputEditText username = fragment.requireView().findViewById(R.id.inputCreateUserUsername);
                    TextInputEditText password = fragment.requireView().findViewById(R.id.inputCreateUserPassword);
                    TextInputEditText email = fragment.requireView().findViewById(R.id.inputCreateUserEmail);
                    Button createBtn = fragment.requireView().findViewById(R.id.btnCreateUser);

                    firstName.setText("John");
                    lastName.setText("Doe");
                    username.setText("johndoe");
                    password.setText("password123");
                    email.setText("invalid-email"); // Missing @ and .com

                    createBtn.performClick();
                    
                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(anyString()), never());
                    verify(mockNavController, never()).navigate(anyInt());
                });
            }
        }
    }
}

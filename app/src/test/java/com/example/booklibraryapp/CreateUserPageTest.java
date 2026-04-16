package com.example.booklibraryapp;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
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

    @Test
    public void testCreateUserAttempt() {
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
                    
                    // Verify that the helper was called to run the insert query
                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(org.mockito.ArgumentMatchers.contains("INSERT INTO USERS")));
                    verify(mockNavController).navigate(R.id.action_createUserPage_to_LoginPage);
                });
            }
        }
    }
}

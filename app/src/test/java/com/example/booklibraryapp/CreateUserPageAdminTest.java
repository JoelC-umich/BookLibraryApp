package com.example.booklibraryapp;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

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

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class CreateUserPageAdminTest {

    @Test
    public void testFragmentUIInitialization() {
        try (FragmentScenario<CreateUserPageAdmin> scenario = FragmentScenario.launchInContainer(CreateUserPageAdmin.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                assertNotNull(fragment.requireView().findViewById(R.id.inputCreateUserFirstNameAdmin));
                assertNotNull(fragment.requireView().findViewById(R.id.inputCreateUserLastNameAdmin));
                assertNotNull(fragment.requireView().findViewById(R.id.checkAdminViewCreateAccountIsUser));
                assertNotNull(fragment.requireView().findViewById(R.id.checkAdminViewCreateAccountIsAdmin));
                assertNotNull(fragment.requireView().findViewById(R.id.btnCreateUserAdmin));
            });
        }
    }

    @Test
    public void testCreateAdminAttempt() {
        NavController mockNavController = mock(NavController.class);
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getLastIDPlus1UsersQuery).thenReturn("20");
            mockedHelper.when(QueryConnectorPlusHelper::getUsernamesQuery).thenReturn(new ArrayList<>());

            try (FragmentScenario<CreateUserPageAdmin> scenario = FragmentScenario.launchInContainer(CreateUserPageAdmin.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);

                    TextInputEditText firstName = fragment.requireView().findViewById(R.id.inputCreateUserFirstNameAdmin);
                    TextInputEditText lastName = fragment.requireView().findViewById(R.id.inputCreateUserLastNameAdmin);
                    TextInputEditText username = fragment.requireView().findViewById(R.id.inputCreateUserUsernameAdmin);
                    TextInputEditText password = fragment.requireView().findViewById(R.id.inputCreateUserPasswordAdmin);
                    TextInputEditText email = fragment.requireView().findViewById(R.id.inputCreateUserEmailAdmin);
                    CheckBox checkAdmin = fragment.requireView().findViewById(R.id.checkAdminViewCreateAccountIsAdmin);
                    Button createBtn = fragment.requireView().findViewById(R.id.btnCreateUserAdmin);

                    firstName.setText("Admin");
                    lastName.setText("User");
                    username.setText("adminuser");
                    password.setText("adminpass");
                    email.setText("admin@example.com");
                    checkAdmin.setChecked(true);

                    createBtn.performClick();

                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(org.mockito.ArgumentMatchers.contains("Admin")));
                    verify(mockNavController).navigate(R.id.action_createUserPageAdmin_to_AdminPage);
                });
            }
        }
    }
}

package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.textfield.TextInputEditText;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import java.util.Collections;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class CreateUserPageTest {

    @Test
    public void testCreateUserSuccess() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getLastIDPlus1UsersQuery).thenReturn("10");
            mockedHelper.when(QueryConnectorPlusHelper::getUsernamesQuery).thenReturn(Collections.singletonList("existing"));

            try (FragmentScenario<CreateUserPage> scenario = FragmentScenario.launchInContainer(CreateUserPage.class, null, R.style.Theme_BookLibraryApp)) {
                NavController mockNavController = mock(NavController.class);
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);
                    
                    View view = fragment.requireView();
                    ((TextInputEditText) view.findViewById(R.id.inputCreateUserFirstName)).setText("John");
                    ((TextInputEditText) view.findViewById(R.id.inputCreateUserLastName)).setText("Doe");
                    ((TextInputEditText) view.findViewById(R.id.inputCreateUserUsername)).setText("johndoe");
                    ((TextInputEditText) view.findViewById(R.id.inputCreateUserPassword)).setText("password");
                    ((TextInputEditText) view.findViewById(R.id.inputCreateUserEmail)).setText("john@example.com");
                    ((TextInputEditText) view.findViewById(R.id.inputCreateUserSchool)).setText("UMD");
                    
                    view.findViewById(R.id.btnCreateUser).performClick();

                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(contains("INSERT INTO USERS VALUES (10, 'John', 'Doe', 'User', 'john@example.com', 'johndoe', 'password', 'UMD')")));
                    assertEquals("Account Successfully Created", ShadowToast.getTextOfLatestToast());
                    verify(mockNavController).navigate(R.id.action_createUserPage_to_LoginPage);
                });
            }
        }
    }

    @Test
    public void testCreateUserFailure_BlankFields() {
        try (FragmentScenario<CreateUserPage> scenario = FragmentScenario.launchInContainer(CreateUserPage.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                fragment.requireView().findViewById(R.id.btnCreateUser).performClick();
                assertEquals("One of the fields are blank\nPlease fill and try again", ShadowToast.getTextOfLatestToast());
            });
        }
    }

    @Test
    public void testCreateUserFailure_InvalidEmail() {
        try (FragmentScenario<CreateUserPage> scenario = FragmentScenario.launchInContainer(CreateUserPage.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                View view = fragment.requireView();
                ((TextInputEditText) view.findViewById(R.id.inputCreateUserFirstName)).setText("John");
                ((TextInputEditText) view.findViewById(R.id.inputCreateUserLastName)).setText("Doe");
                ((TextInputEditText) view.findViewById(R.id.inputCreateUserUsername)).setText("johndoe");
                ((TextInputEditText) view.findViewById(R.id.inputCreateUserPassword)).setText("password");
                ((TextInputEditText) view.findViewById(R.id.inputCreateUserEmail)).setText("invalid-email");
                
                view.findViewById(R.id.btnCreateUser).performClick();

                assertEquals("Invalid email format\nPlease enter a valid email", ShadowToast.getTextOfLatestToast());
            });
        }
    }

    @Test
    public void testCreateUserFailure_UsernameExists() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getUsernamesQuery).thenReturn(Collections.singletonList("johndoe"));

            try (FragmentScenario<CreateUserPage> scenario = FragmentScenario.launchInContainer(CreateUserPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    View view = fragment.requireView();
                    ((TextInputEditText) view.findViewById(R.id.inputCreateUserFirstName)).setText("John");
                    ((TextInputEditText) view.findViewById(R.id.inputCreateUserLastName)).setText("Doe");
                    ((TextInputEditText) view.findViewById(R.id.inputCreateUserUsername)).setText("johndoe");
                    ((TextInputEditText) view.findViewById(R.id.inputCreateUserPassword)).setText("password");
                    ((TextInputEditText) view.findViewById(R.id.inputCreateUserEmail)).setText("john@example.com");
                    
                    view.findViewById(R.id.btnCreateUser).performClick();

                    assertEquals("Username already exists\nPlease choose another username", ShadowToast.getTextOfLatestToast());
                });
            }
        }
    }
}

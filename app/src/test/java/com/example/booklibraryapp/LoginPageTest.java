package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import android.os.Bundle;

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
public class LoginPageTest {

    @Test
    public void testLoginSuccess_User() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getUsernamesQuery).thenReturn(Collections.singletonList("testuser"));
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUsernameIDQuery("testuser")).thenReturn("1");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getPasswordFromID("1")).thenReturn("password");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUserTypeFromID("1")).thenReturn("User");

            try (FragmentScenario<LoginPage> scenario = FragmentScenario.launchInContainer(LoginPage.class, null, R.style.Theme_BookLibraryApp)) {
                NavController mockNavController = mock(NavController.class);
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);
                    
                    ((TextInputEditText) fragment.requireView().findViewById(R.id.inputLoginUsername)).setText("testuser");
                    ((TextInputEditText) fragment.requireView().findViewById(R.id.inputLoginPassword)).setText("password");
                    
                    fragment.requireView().findViewById(R.id.btnLogin).performClick();

                    verify(mockNavController).navigate(R.id.action_LoginPage_to_userPage);
                    assertEquals("1", QueryConnectorPlusHelper.IDWhenLoggingIn);
                });
            }
        }
    }

    @Test
    public void testLoginSuccess_Admin() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getUsernamesQuery).thenReturn(Collections.singletonList("admin"));
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUsernameIDQuery("admin")).thenReturn("0");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getPasswordFromID("0")).thenReturn("adminpass");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUserTypeFromID("0")).thenReturn("Admin");

            try (FragmentScenario<LoginPage> scenario = FragmentScenario.launchInContainer(LoginPage.class, null, R.style.Theme_BookLibraryApp)) {
                NavController mockNavController = mock(NavController.class);
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);
                    
                    ((TextInputEditText) fragment.requireView().findViewById(R.id.inputLoginUsername)).setText("admin");
                    ((TextInputEditText) fragment.requireView().findViewById(R.id.inputLoginPassword)).setText("adminpass");
                    
                    fragment.requireView().findViewById(R.id.btnLogin).performClick();

                    verify(mockNavController).navigate(R.id.action_LoginPage_to_AdminPage);
                });
            }
        }
    }

    @Test
    public void testLoginFailure_WrongPassword() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getUsernamesQuery).thenReturn(Collections.singletonList("testuser"));
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUsernameIDQuery("testuser")).thenReturn("1");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getPasswordFromID("1")).thenReturn("correctpassword");

            try (FragmentScenario<LoginPage> scenario = FragmentScenario.launchInContainer(LoginPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ((TextInputEditText) fragment.requireView().findViewById(R.id.inputLoginUsername)).setText("testuser");
                    ((TextInputEditText) fragment.requireView().findViewById(R.id.inputLoginPassword)).setText("wrongpassword");
                    
                    fragment.requireView().findViewById(R.id.btnLogin).performClick();

                    assertEquals("Username or Password is incorrect\nPlease try again", ShadowToast.getTextOfLatestToast());
                });
            }
        }
    }

    @Test
    public void testLoginFailure_UserNotFound() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getUsernamesQuery).thenReturn(Collections.emptyList());

            try (FragmentScenario<LoginPage> scenario = FragmentScenario.launchInContainer(LoginPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ((TextInputEditText) fragment.requireView().findViewById(R.id.inputLoginUsername)).setText("nonexistent");
                    ((TextInputEditText) fragment.requireView().findViewById(R.id.inputLoginPassword)).setText("any");
                    
                    fragment.requireView().findViewById(R.id.btnLogin).performClick();

                    assertEquals("Username does not exist\nPlease try again", ShadowToast.getTextOfLatestToast());
                });
            }
        }
    }

    @Test
    public void testNavigateToCreateAccount() {
        try (FragmentScenario<LoginPage> scenario = FragmentScenario.launchInContainer(LoginPage.class, null, R.style.Theme_BookLibraryApp)) {
            NavController mockNavController = mock(NavController.class);
            scenario.onFragment(fragment -> {
                Navigation.setViewNavController(fragment.requireView(), mockNavController);
                fragment.requireView().findViewById(R.id.btnLoginCreateAccount).performClick();
                verify(mockNavController).navigate(R.id.action_LoginPage_to_createUserPage);
            });
        }
    }
}

package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.textfield.TextInputEditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class AdminChangeAccountInfoforUserPageTest {

    private MockedStatic<QueryConnectorPlusHelper> mockedHelper;

    @Before
    public void setUp() {
        mockedHelper = mockStatic(QueryConnectorPlusHelper.class);
    }

    @After
    public void tearDown() {
        if (mockedHelper != null) {
            mockedHelper.close();
        }
    }

    @Test
    public void testUserDetailsPopulated() {
        mockedHelper.when(() -> QueryConnectorPlusHelper.getUsernameIDQuery("johndoe")).thenReturn("1");
        mockedHelper.when(() -> QueryConnectorPlusHelper.getFirstNameFromIDQuery("1")).thenReturn("John");
        mockedHelper.when(() -> QueryConnectorPlusHelper.getLastNameFromIDQuery("1")).thenReturn("Doe");
        mockedHelper.when(() -> QueryConnectorPlusHelper.getUserTypeFromID("1")).thenReturn("User");
        mockedHelper.when(() -> QueryConnectorPlusHelper.getEmailFromIDQuery("1")).thenReturn("john@example.com");
        mockedHelper.when(() -> QueryConnectorPlusHelper.getPasswordFromID("1")).thenReturn("password");
        mockedHelper.when(() -> QueryConnectorPlusHelper.getSchoolFromIDQuery("1")).thenReturn("UMD");

        try (FragmentScenario<AdminChangeAccountInfoforUserPage> scenario = FragmentScenario.launchInContainer(AdminChangeAccountInfoforUserPage.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                Bundle bundle = new Bundle();
                bundle.putString("Username", "johndoe");
                fragment.getParentFragmentManager().setFragmentResult("userNameInfo", bundle);

                assertEquals("1", ((TextInputEditText) fragment.requireView().findViewById(R.id.userInfoUserID)).getText().toString());
                assertEquals("John", ((TextInputEditText) fragment.requireView().findViewById(R.id.userInfoUserFirstName)).getText().toString());
                assertEquals("User", ((TextInputEditText) fragment.requireView().findViewById(R.id.userInfoUserType)).getText().toString());
            });
        }
    }

    @Test
    public void testSaveUser_Success() {
        try (FragmentScenario<AdminChangeAccountInfoforUserPage> scenario = FragmentScenario.launchInContainer(AdminChangeAccountInfoforUserPage.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                ((TextInputEditText) fragment.requireView().findViewById(R.id.userInfoUserID)).setText("1");
                ((TextInputEditText) fragment.requireView().findViewById(R.id.userInfoUserFirstName)).setText("Jane");
                ((TextInputEditText) fragment.requireView().findViewById(R.id.userInfoUserLastName)).setText("Doe");
                ((TextInputEditText) fragment.requireView().findViewById(R.id.userInfoUserEmail)).setText("jane@example.com");
                ((TextInputEditText) fragment.requireView().findViewById(R.id.userInfoUserUsername)).setText("janedoe");
                ((TextInputEditText) fragment.requireView().findViewById(R.id.userInfoUserPassword)).setText("newpass");
                ((TextInputEditText) fragment.requireView().findViewById(R.id.userInfoUserSchool)).setText("UMD");
                ((TextInputEditText) fragment.requireView().findViewById(R.id.userInfoUserType)).setText("User");

                fragment.requireView().findViewById(R.id.btnUserInfoSave).performClick();

                mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(contains("UPDATE USERS SET FIRST_NAME = 'Jane'")));
                assertEquals("janedoe information saved", ShadowToast.getTextOfLatestToast());
            });
        }
    }

    @Test
    public void testDeleteUser_Success() {
        try (FragmentScenario<AdminChangeAccountInfoforUserPage> scenario = FragmentScenario.launchInContainer(AdminChangeAccountInfoforUserPage.class, null, R.style.Theme_BookLibraryApp)) {
            NavController mockNavController = mock(NavController.class);
            scenario.onFragment(fragment -> {
                Navigation.setViewNavController(fragment.requireView(), mockNavController);
                ((TextInputEditText) fragment.requireView().findViewById(R.id.userInfoUserID)).setText("1");
                ((TextInputEditText) fragment.requireView().findViewById(R.id.userInfoUserUsername)).setText("janedoe");

                fragment.requireView().findViewById(R.id.btnAdminChangeAccountUserDelete).performClick();

                mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(contains("DELETE FROM USERS WHERE ID = '1'")));
                assertEquals("janedoe has been deleted", ShadowToast.getTextOfLatestToast());
                verify(mockNavController).navigate(R.id.action_adminChangeAccountInfoforUserPage_to_adminAccountsInfoPage);
            });
        }
    }
}

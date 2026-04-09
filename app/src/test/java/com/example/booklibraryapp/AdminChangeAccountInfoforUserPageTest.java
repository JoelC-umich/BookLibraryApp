package com.example.booklibraryapp;

import android.os.Bundle;
import android.view.View;
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
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class AdminChangeAccountInfoforUserPageTest {

    @Test
    public void testDisplayUserInfoOnResult() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUsernameIDQuery("testUser")).thenReturn("123");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getFirstNameFromIDQuery("123")).thenReturn("First");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getLastNameFromIDQuery("123")).thenReturn("Last");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUserTypeFromID("123")).thenReturn("User");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getEmailFromIDQuery("123")).thenReturn("test@email.com");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getPasswordFromID("123")).thenReturn("pass");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getSchoolFromIDQuery("123")).thenReturn("UMD");

            try (FragmentScenario<AdminChangeAccountInfoforUserPage> scenario = FragmentScenario.launchInContainer(AdminChangeAccountInfoforUserPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Bundle result = new Bundle();
                    result.putString("Username", "testUser");
                    fragment.getParentFragmentManager().setFragmentResult("userNameInfo", result);

                    View view = fragment.requireView();
                    assertEquals("123", ((TextInputEditText) view.findViewById(R.id.userInfoUserID)).getText().toString());
                    assertEquals("First", ((TextInputEditText) view.findViewById(R.id.userInfoUserFirstName)).getText().toString());
                    assertEquals("Last", ((TextInputEditText) view.findViewById(R.id.userInfoUserLastName)).getText().toString());
                    assertEquals("User", ((TextInputEditText) view.findViewById(R.id.userInfoUserType)).getText().toString());
                    assertEquals("test@email.com", ((TextInputEditText) view.findViewById(R.id.userInfoUserEmail)).getText().toString());
                    assertEquals("testUser", ((TextInputEditText) view.findViewById(R.id.userInfoUserUsername)).getText().toString());
                    assertEquals("pass", ((TextInputEditText) view.findViewById(R.id.userInfoUserPassword)).getText().toString());
                    assertEquals("UMD", ((TextInputEditText) view.findViewById(R.id.userInfoUserSchool)).getText().toString());

                    assertTrue(((CheckBox) view.findViewById(R.id.checkAdminChangeAccountUserCheckbox)).isChecked());
                    assertFalse(((CheckBox) view.findViewById(R.id.checkAdminChangeAccountAdminCheckbox)).isChecked());
                });
            }
        }
    }

    @Test
    public void testCheckboxLogic() {
        try (FragmentScenario<AdminChangeAccountInfoforUserPage> scenario = FragmentScenario.launchInContainer(AdminChangeAccountInfoforUserPage.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                View view = fragment.requireView();
                CheckBox userCheck = view.findViewById(R.id.checkAdminChangeAccountUserCheckbox);
                CheckBox adminCheck = view.findViewById(R.id.checkAdminChangeAccountAdminCheckbox);
                TextInputEditText typeInput = view.findViewById(R.id.userInfoUserType);

                adminCheck.setChecked(true);
                assertTrue(adminCheck.isChecked());
                assertFalse(userCheck.isChecked());
                assertEquals("Admin", typeInput.getText().toString());

                userCheck.setChecked(true);
                assertTrue(userCheck.isChecked());
                assertFalse(adminCheck.isChecked());
                assertEquals("User", typeInput.getText().toString());
            });
        }
    }

    @Test
    public void testSaveButtonValidation() {
        try (FragmentScenario<AdminChangeAccountInfoforUserPage> scenario = FragmentScenario.launchInContainer(AdminChangeAccountInfoforUserPage.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                View view = fragment.requireView();
                view.findViewById(R.id.btnUserInfoSave).performClick();

                assertEquals("Error saving information\nOne of the fields is blank or missing", ShadowToast.getTextOfLatestToast());
            });
        }
    }

    @Test
    public void testSavePrimaryAdminRestriction() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            try (FragmentScenario<AdminChangeAccountInfoforUserPage> scenario = FragmentScenario.launchInContainer(AdminChangeAccountInfoforUserPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    View view = fragment.requireView();
                    ((TextInputEditText) view.findViewById(R.id.userInfoUserID)).setText("0");
                    ((TextInputEditText) view.findViewById(R.id.userInfoUserFirstName)).setText("Admin");
                    ((TextInputEditText) view.findViewById(R.id.userInfoUserLastName)).setText("Main");
                    ((TextInputEditText) view.findViewById(R.id.userInfoUserEmail)).setText("admin@lib.com");
                    ((TextInputEditText) view.findViewById(R.id.userInfoUserUsername)).setText("primary");
                    ((TextInputEditText) view.findViewById(R.id.userInfoUserPassword)).setText("root");

                    CheckBox userCheck = view.findViewById(R.id.checkAdminChangeAccountUserCheckbox);
                    userCheck.setChecked(true);

                    view.findViewById(R.id.btnUserInfoSave).performClick();

                    assertEquals("Error saving information\nCannot change primary admin to user", ShadowToast.getTextOfLatestToast());
                    assertTrue(((CheckBox) view.findViewById(R.id.checkAdminChangeAccountAdminCheckbox)).isChecked());
                });
            }
        }
    }

    @Test
    public void testDeletePrimaryAdminRestriction() {
        try (FragmentScenario<AdminChangeAccountInfoforUserPage> scenario = FragmentScenario.launchInContainer(AdminChangeAccountInfoforUserPage.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                View view = fragment.requireView();
                ((TextInputEditText) view.findViewById(R.id.userInfoUserID)).setText("0");

                view.findViewById(R.id.btnAdminChangeAccountUserDelete).performClick();

                assertEquals("Cannot delete primary admin account", ShadowToast.getTextOfLatestToast());
            });
        }
    }

    @Test
    public void testSuccessfulSaveAndNavigationForLoggedInUser() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            QueryConnectorPlusHelper.IDWhenLoggingIn = "10";
            
            try (FragmentScenario<AdminChangeAccountInfoforUserPage> scenario = FragmentScenario.launchInContainer(AdminChangeAccountInfoforUserPage.class, null, R.style.Theme_BookLibraryApp)) {
                NavController mockNavController = mock(NavController.class);
                
                scenario.onFragment(fragment -> {
                    View view = fragment.requireView();
                    Navigation.setViewNavController(view, mockNavController);

                    ((TextInputEditText) view.findViewById(R.id.userInfoUserID)).setText("10");
                    ((TextInputEditText) view.findViewById(R.id.userInfoUserFirstName)).setText("Me");
                    ((TextInputEditText) view.findViewById(R.id.userInfoUserLastName)).setText("Myself");
                    ((TextInputEditText) view.findViewById(R.id.userInfoUserEmail)).setText("me@me.com");
                    ((TextInputEditText) view.findViewById(R.id.userInfoUserUsername)).setText("meme");
                    ((TextInputEditText) view.findViewById(R.id.userInfoUserPassword)).setText("123");
                    
                    ((CheckBox) view.findViewById(R.id.checkAdminChangeAccountUserCheckbox)).setChecked(true);

                    view.findViewById(R.id.btnUserInfoSave).performClick();

                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(contains("UPDATE USERS")));
                    assertEquals("Your information is saved\nLogging you out of admin view", ShadowToast.getTextOfLatestToast());
                    verify(mockNavController).navigate(R.id.action_adminChangeAccounInfoforUserPage_to_LoginPage);
                });
            }
        }
    }

    @Test
    public void testDeleteOtherUser() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            QueryConnectorPlusHelper.IDWhenLoggingIn = "0";

            try (FragmentScenario<AdminChangeAccountInfoforUserPage> scenario = FragmentScenario.launchInContainer(AdminChangeAccountInfoforUserPage.class, null, R.style.Theme_BookLibraryApp)) {
                NavController mockNavController = mock(NavController.class);

                scenario.onFragment(fragment -> {
                    View view = fragment.requireView();
                    Navigation.setViewNavController(view, mockNavController);

                    ((TextInputEditText) view.findViewById(R.id.userInfoUserID)).setText("5");
                    ((TextInputEditText) view.findViewById(R.id.userInfoUserUsername)).setText("otherUser");

                    view.findViewById(R.id.btnAdminChangeAccountUserDelete).performClick();

                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery("DELETE FROM USERS WHERE ID = '5'"));
                    assertEquals("otherUser has been deleted", ShadowToast.getTextOfLatestToast());
                    verify(mockNavController).navigate(R.id.action_adminChangeAccounInfoforUserPage_to_adminAccountsInfoPage);
                });
            }
        }
    }
}

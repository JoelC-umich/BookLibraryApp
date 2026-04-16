package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import android.os.Bundle;
import android.widget.Button;

import androidx.fragment.app.testing.FragmentScenario;
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
}

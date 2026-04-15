package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import android.widget.TextView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.annotation.Config;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class UserPageTest {

    @Test
    public void testFragmentUIInitialization() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            QueryConnectorPlusHelper.IDWhenLoggingIn = "1";
            mockedHelper.when(() -> QueryConnectorPlusHelper.getFirstNameFromIDQuery(anyString())).thenReturn("John");

            try (FragmentScenario<UserPage> scenario = FragmentScenario.launchInContainer(UserPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    TextView welcomeText = fragment.requireView().findViewById(R.id.textUserWelcome);
                    assertNotNull(welcomeText);
                    assertEquals("Welcome John!", welcomeText.getText().toString());
                });
            }
        }
    }
}

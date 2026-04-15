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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 28)
public class LoginPageTest {

    private static class SynchronousExecutor extends AbstractExecutorService {
        @Override public void shutdown() {}
        @Override public List<Runnable> shutdownNow() { return null; }
        @Override public boolean isShutdown() { return false; }
        @Override public boolean isTerminated() { return false; }
        @Override public boolean awaitTermination(long timeout, TimeUnit unit) { return true; }
        @Override public void execute(Runnable command) { command.run(); }
    }

    @Before
    public void setUp() {
        QueryConnectorPlusHelper.setExecutor(new SynchronousExecutor());
    }

    @Test
    public void testFragmentUIInitialization() {
        try (FragmentScenario<LoginPage> scenario = FragmentScenario.launchInContainer(LoginPage.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                assertNotNull(fragment.requireView().findViewById(R.id.inputLoginUsername));
                assertNotNull(fragment.requireView().findViewById(R.id.inputLoginPassword));
                assertNotNull(fragment.requireView().findViewById(R.id.btnLogin));
            });
        }
    }

    @Test
    public void testLoginAttemptWithMockedDb() {
        NavController mockNavController = mock(NavController.class);
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getUsernamesQuery).thenReturn(Arrays.asList("testuser"));
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUsernameIDQuery("testuser")).thenReturn("1");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getPasswordFromID("1")).thenReturn("password");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUserTypeFromID("1")).thenReturn("User");

            try (FragmentScenario<LoginPage> scenario = FragmentScenario.launchInContainer(LoginPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);
                    
                    TextInputEditText userField = fragment.requireView().findViewById(R.id.inputLoginUsername);
                    TextInputEditText passField = fragment.requireView().findViewById(R.id.inputLoginPassword);
                    Button loginBtn = fragment.requireView().findViewById(R.id.btnLogin);

                    userField.setText("testuser");
                    passField.setText("password");
                    loginBtn.performClick();
                    
                    // Idle looper to catch navigation which might be posted
                    Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
                    
                    verify(mockNavController).navigate(R.id.action_LoginPage_to_userPage);
                });
            }
        }
    }
}

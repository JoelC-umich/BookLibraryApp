package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import android.os.Bundle;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

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
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 28)
public class AdminAccountsInfoPageTest {

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
        List<String> mockUserData = Arrays.asList("John Doe;;;user1", "Jane Smith;;;user2");
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getUsersFullNamesWithUsernamesQuery).thenReturn(mockUserData);

            try (FragmentScenario<AdminAccountsInfoPage> scenario = FragmentScenario.launchInContainer(AdminAccountsInfoPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Shadows.shadowOf(Looper.getMainLooper()).idle();
                    
                    ListView listView = fragment.requireView().findViewById(R.id.userSearchResult);
                    assertNotNull(listView);

                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                    assertNotNull(adapter);
                    assertEquals(2, adapter.getCount());
                    assertEquals("John Doe (user1)", adapter.getItem(0));
                    assertEquals("Jane Smith (user2)", adapter.getItem(1));
                });
            }
        }
    }

    @Test
    public void testSearchFiltering() {
        List<String> mockUserData = Arrays.asList("Alice Wonder;;;alice", "Bob Builder;;;bob");
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getUsersFullNamesWithUsernamesQuery).thenReturn(mockUserData);

            try (FragmentScenario<AdminAccountsInfoPage> scenario = FragmentScenario.launchInContainer(AdminAccountsInfoPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Shadows.shadowOf(Looper.getMainLooper()).idle();
                    
                    SearchView searchView = fragment.requireView().findViewById(R.id.userSearchInput);
                    searchView.setQuery("Alice", false);

                    Shadows.shadowOf(Looper.getMainLooper()).idle();
                    
                    ListView listView = fragment.requireView().findViewById(R.id.userSearchResult);
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                    assertNotNull(adapter);
                    
                    // Verify filtering logic works with the new display format
                    assertEquals(1, adapter.getCount());
                    assertEquals("Alice Wonder (alice)", adapter.getItem(0));
                });
            }
        }
    }

    @Test
    public void testUserSelectionNavigatesAndSetsResult() {
        List<String> mockUserData = Arrays.asList("John Doe;;;user1");
        NavController mockNavController = mock(NavController.class);
        AtomicReference<String> resultUsername = new AtomicReference<>();
        
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getUsersFullNamesWithUsernamesQuery).thenReturn(mockUserData);

            try (FragmentScenario<AdminAccountsInfoPage> scenario = FragmentScenario.launchInContainer(AdminAccountsInfoPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);
                    
                    // Intercept the fragment result to verify username extraction
                    fragment.getParentFragmentManager().setFragmentResultListener("userNameInfo", fragment, (requestKey, bundle) -> {
                        resultUsername.set(bundle.getString("Username"));
                    });
                    
                    Shadows.shadowOf(Looper.getMainLooper()).idle();
                    
                    ListView listView = fragment.requireView().findViewById(R.id.userSearchResult);
                    listView.performItemClick(listView, 0, 0);

                    verify(mockNavController).navigate(R.id.action_adminAccountsInfoPage_to_adminChangeAccounInfoforUserPage);
                    // Verify that "user1" was correctly extracted from "John Doe (user1)"
                    assertEquals("user1", resultUsername.get());
                });
            }
        }
    }
}

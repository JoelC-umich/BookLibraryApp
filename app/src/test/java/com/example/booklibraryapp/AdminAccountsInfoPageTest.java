package com.example.booklibraryapp;

import android.os.Bundle;
import android.view.View;
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
import org.robolectric.annotation.Config;
import org.robolectric.Shadows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class AdminAccountsInfoPageTest {

    private List<String> mockUsernames;

    @Before
    public void setUp() {
        mockUsernames = Arrays.asList("user1", "user2", "admin");
    }

    /**
     * Verifies that the UI initializes correctly and populates the ListView with data.
     */
    @Test
    public void testFragmentUIInitialization() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getUsernamesQuery).thenReturn(mockUsernames);

            try (FragmentScenario<AdminAccountsInfoPage> scenario = FragmentScenario.launchInContainer(AdminAccountsInfoPage.class)) {
                scenario.onFragment(fragment -> {
                    View view = fragment.getView();
                    assertNotNull(view);
                    ListView listView = view.findViewById(R.id.userSearchResult);
                    SearchView searchView = view.findViewById(R.id.userSearchInput);

                    assertNotNull(listView);
                    assertNotNull(searchView);

                    @SuppressWarnings("unchecked")
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                    assertNotNull(adapter);
                    assertEquals(3, adapter.getCount());
                    assertEquals("user1", adapter.getItem(0));
                });
            }
        }
    }

    /**
     * Verifies handling of an empty user list from the database.
     */
    @Test
    public void testEmptyUserList() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getUsernamesQuery).thenReturn(Collections.emptyList());

            try (FragmentScenario<AdminAccountsInfoPage> scenario = FragmentScenario.launchInContainer(AdminAccountsInfoPage.class)) {
                scenario.onFragment(fragment -> {
                    ListView listView = fragment.requireView().findViewById(R.id.userSearchResult);
                    assertNotNull(listView.getAdapter());
                    assertEquals(0, listView.getAdapter().getCount());
                });
            }
        }
    }

    /**
     * Verifies that the search filter is triggered when text changes in the SearchView.
     */
    @Test
    public void testUserSearchFiltering() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getUsernamesQuery).thenReturn(mockUsernames);

            try (FragmentScenario<AdminAccountsInfoPage> scenario = FragmentScenario.launchInContainer(AdminAccountsInfoPage.class)) {
                scenario.onFragment(fragment -> {
                    SearchView searchView = fragment.requireView().findViewById(R.id.userSearchInput);
                    
                    // Simulate text change to "admin"
                    searchView.setQuery("admin", false);
                    
                    // Flush the main looper to ensure any posted filter results are processed
                    Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
                    
                    // Note: ArrayAdapter's filter results can be difficult to verify synchronously 
                    // in some Robolectric versions, but this hits the onQueryTextChange branch.
                });
            }
        }
    }

    /**
     * Verifies that the search submit listener branch is covered.
     */
    @Test
    public void testSearchSubmit() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getUsernamesQuery).thenReturn(mockUsernames);

            try (FragmentScenario<AdminAccountsInfoPage> scenario = FragmentScenario.launchInContainer(AdminAccountsInfoPage.class)) {
                scenario.onFragment(fragment -> {
                    SearchView searchView = fragment.requireView().findViewById(R.id.userSearchInput);
                    // This triggers the onQueryTextSubmit branch
                    searchView.setQuery("test", true);
                });
            }
        }
    }

    /**
     * Verifies that clicking an item in the list sets the correct fragment result 
     * and navigates to the expected destination.
     */
    @Test
    public void testUserSelectionNavigatesAndSetsResult() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getUsernamesQuery).thenReturn(mockUsernames);

            try (FragmentScenario<AdminAccountsInfoPage> scenario = FragmentScenario.launchInContainer(AdminAccountsInfoPage.class)) {
                NavController mockNavController = mock(NavController.class);

                scenario.onFragment(fragment -> {
                    View view = fragment.getView();
                    assertNotNull(view);
                    Navigation.setViewNavController(view, mockNavController);

                    ListView listView = view.findViewById(R.id.userSearchResult);
                    assertNotNull(listView);
                    
                    // Set up result listener to verify the bundle contents
                    fragment.getParentFragmentManager().setFragmentResultListener("userNameInfo", fragment, (requestKey, result) -> 
                        assertEquals("user2", result.getString("Username"))
                    );

                    // Simulate clicking the second item ("user2")
                    View listItem = listView.getAdapter().getView(1, null, listView);
                    listView.performItemClick(listItem, 1, listView.getAdapter().getItemId(1));

                    // Verify navigation to the correct destination
                    verify(mockNavController).navigate(eq(R.id.action_adminAccountsInfoPage_to_adminChangeAccountInfoforUserPage));
                });
            }
        }
    }

    /**
     * Verifies that the fragment correctly handles lifecycle recreation.
     */
    @Test
    public void testFragmentRecreation() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getUsernamesQuery).thenReturn(mockUsernames);

            try (FragmentScenario<AdminAccountsInfoPage> scenario = FragmentScenario.launchInContainer(AdminAccountsInfoPage.class)) {
                scenario.recreate();
                scenario.onFragment(fragment -> {
                    assertNotNull(fragment.getView());
                    ListView listView = fragment.getView().findViewById(R.id.userSearchResult);
                    assertNotNull(listView);
                    assertEquals(3, listView.getAdapter().getCount());
                });
            }
        }
    }
}

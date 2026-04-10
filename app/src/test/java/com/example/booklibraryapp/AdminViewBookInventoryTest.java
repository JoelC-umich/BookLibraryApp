package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.annotation.Config;
import org.robolectric.Shadows;

import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class AdminViewBookInventoryTest {

    @Test
    public void testFragmentUIInitialization() {
        List<String> mockBooks = Arrays.asList("Book 1", "Book 2");
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getBookNamesQuery).thenReturn(mockBooks);

            try (FragmentScenario<AdminViewBookInventory> scenario = FragmentScenario.launchInContainer(AdminViewBookInventory.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ListView listView = fragment.requireView().findViewById(R.id.listViewAdminViewBookInventory);
                    assertNotNull(listView);
                    
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                    assertNotNull(adapter);
                    assertEquals(2, adapter.getCount());
                    assertEquals("Book 1", adapter.getItem(0));
                });
            }
        }
    }

    @Test
    public void testSearchFiltering() {
        List<String> mockBooks = Arrays.asList("Apple", "Banana", "Cherry");
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getBookNamesQuery).thenReturn(mockBooks);

            try (FragmentScenario<AdminViewBookInventory> scenario = FragmentScenario.launchInContainer(AdminViewBookInventory.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    SearchView searchView = fragment.requireView().findViewById(R.id.searchAdminViewBookInventory);
                    searchView.setQuery("Apple", false);
                    
                    Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
                    // Basic check to ensure no crash and branch coverage
                });
            }
        }
    }

    @Test
    public void testItemClickNavigatesAndSetsResult() {
        List<String> mockBooks = Arrays.asList("Target Book");
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getBookNamesQuery).thenReturn(mockBooks);

            try (FragmentScenario<AdminViewBookInventory> scenario = FragmentScenario.launchInContainer(AdminViewBookInventory.class, null, R.style.Theme_BookLibraryApp)) {
                NavController mockNavController = mock(NavController.class);
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);
                    
                    ListView listView = fragment.requireView().findViewById(R.id.listViewAdminViewBookInventory);
                    
                    fragment.getParentFragmentManager().setFragmentResultListener("bookNameInfo", fragment, (requestKey, bundle) -> 
                        assertEquals("Target Book", bundle.getString("bookName"))
                    );

                    listView.performItemClick(null, 0, 0);

                    verify(mockNavController).navigate(R.id.action_adminViewBookInventory_to_adminViewBookInventoryEditBook);
                });
            }
        }
    }
}

package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mockStatic;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class UserReserveBookPageTest {

    // FR4 test user book reserve list view
    @Test
    public void testFragmentUIInitialization() {
        List<String> mockBooks = Arrays.asList("Book 1 by Author 1", "Book 2 by Author 2");
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getAvailableBooksQuery).thenReturn(mockBooks);

            try (FragmentScenario<UserReserveBookPage> scenario = FragmentScenario.launchInContainer(UserReserveBookPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ListView listView = fragment.requireView().findViewById(R.id.listUserReserveBookPage);
                    assertNotNull(listView);

                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                    assertNotNull(adapter);
                    assertEquals(2, adapter.getCount());
                });
            }
        }
    }

    // FR3 test user book reserve list view and search book
    @Test
    public void testSearchFiltering() {
        List<String> mockBooks = Arrays.asList("Apple by Author A", "Banana by Author B");
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getAvailableBooksQuery).thenReturn(mockBooks);

            try (FragmentScenario<UserReserveBookPage> scenario = FragmentScenario.launchInContainer(UserReserveBookPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    SearchView searchView = fragment.requireView().findViewById(R.id.searchUserReserveBookPage);
                    searchView.setQuery("Apple", false);

                    Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
                });
            }
        }
    }
}

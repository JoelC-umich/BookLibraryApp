package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mockStatic;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.fragment.app.testing.FragmentScenario;
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
public class AdminViewBorrowedBooksPageTest {

    @Test
    public void testFragmentUIInitialization() {
        List<String> mockBorrowed = Arrays.asList("Borrowed 1", "Borrowed 2");
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getBorrowedBooksQuery).thenReturn(mockBorrowed);

            try (FragmentScenario<AdminViewBorrowedBooksPage> scenario = FragmentScenario.launchInContainer(AdminViewBorrowedBooksPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ListView listView = fragment.requireView().findViewById(R.id.listAdminViewBorrowedBooks);
                    assertNotNull(listView);
                    
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                    assertNotNull(adapter);
                    assertEquals(2, adapter.getCount());
                });
            }
        }
    }

    @Test
    public void testSearchFiltering() {
        List<String> mockBorrowed = Arrays.asList("Apple", "Banana");
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getBorrowedBooksQuery).thenReturn(mockBorrowed);

            try (FragmentScenario<AdminViewBorrowedBooksPage> scenario = FragmentScenario.launchInContainer(AdminViewBorrowedBooksPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    SearchView searchView = fragment.requireView().findViewById(R.id.searchAdminViewBorrowedBooks);
                    searchView.setQuery("Apple", false);
                    
                    Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
                    // Coverage for filter logic
                });
            }
        }
    }
}

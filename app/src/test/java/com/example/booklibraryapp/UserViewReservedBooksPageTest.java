package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.fragment.app.testing.FragmentScenario;
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

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class UserViewReservedBooksPageTest {

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
        QueryConnectorPlusHelper.IDWhenLoggingIn = "1";
    }

    // FR4 test user book reserved list view
    @Test
    public void testFragmentUIInitialization() {
        List<String> mockBooksData = Arrays.asList("1;;;101;;;Book 1;;;Reserved;;;2023-10-10", "2;;;102;;;Book 2;;;Pending;;;null");

        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBorrowedBooksDetailedQuery(any())).thenReturn(mockBooksData);

            try (FragmentScenario<UserViewReservedBooksPage> scenario = FragmentScenario.launchInContainer(UserViewReservedBooksPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Shadows.shadowOf(Looper.getMainLooper()).idle();
                    
                    ListView listView = fragment.requireView().findViewById(R.id.listViewUserViewReservedBooks);
                    assertNotNull(listView);

                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                    assertNotNull(adapter);
                    assertEquals(2, adapter.getCount());
                });
            }
        }
    }

    // FR4 test user book reserved list view search book from the list
    @Test
    public void testSearchFiltering() {
        List<String> mockBooksData = Arrays.asList("1;;;101;;;Apple;;;Reserved", "2;;;102;;;Banana;;;Reserved");
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBorrowedBooksDetailedQuery(any())).thenReturn(mockBooksData);

            try (FragmentScenario<UserViewReservedBooksPage> scenario = FragmentScenario.launchInContainer(UserViewReservedBooksPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Shadows.shadowOf(Looper.getMainLooper()).idle();
                    
                    SearchView searchView = fragment.requireView().findViewById(R.id.searchUserViewReservedBooks);
                    searchView.setQuery("Apple", false);

                    Shadows.shadowOf(Looper.getMainLooper()).idle();
                });
            }
        }
    }
}

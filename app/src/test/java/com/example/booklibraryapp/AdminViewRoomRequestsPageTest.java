package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mockStatic;

import android.widget.ArrayAdapter;
import android.widget.ListView;

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
public class AdminViewRoomRequestsPageTest {

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
        List<String> mockRequests = Arrays.asList("Request #1: User A - Room 101", "Request #2: User B - Room 102");
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getPendingRoomsReservedWithDetailsQuery).thenReturn(mockRequests);

            try (FragmentScenario<AdminViewRoomRequestsPage> scenario = FragmentScenario.launchInContainer(AdminViewRoomRequestsPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ListView listView = fragment.requireView().findViewById(R.id.listViewAdminRoomRequests);
                    assertNotNull(listView);

                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                    assertNotNull(adapter);
                    assertEquals(2, adapter.getCount());
                });
            }
        }
    }
}

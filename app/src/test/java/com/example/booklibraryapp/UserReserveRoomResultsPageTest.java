package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mockStatic;

import android.os.Bundle;
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
public class UserReserveRoomResultsPageTest {

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
        Bundle args = new Bundle();
        args.putString("selectedDate", "2023-12-25");

        List<String> mockRoomIDs = Arrays.asList("1", "2");
        List<String> mockReserved = Arrays.asList("1:1"); // Room 1, Slot 1 is reserved

        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getRoomIDsQuery).thenReturn(mockRoomIDs);
            mockedHelper.when(() -> QueryConnectorPlusHelper.getRoomIDsReservedOnDateQuery("2023-12-25")).thenReturn(mockReserved);
            mockedHelper.when(() -> QueryConnectorPlusHelper.slotToTime(anyInt())).thenReturn("Mock Time");

            try (FragmentScenario<UserReserveRoomResultsPage> scenario = FragmentScenario.launchInContainer(UserReserveRoomResultsPage.class, args, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ListView listView = fragment.requireView().findViewById(R.id.listUserReserveRoomResults);
                    assertNotNull(listView);

                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                    assertNotNull(adapter);
                    // 2 rooms * 5 slots = 10 total slots. 1 is reserved, so 9 should be available.
                    assertEquals(9, adapter.getCount());
                });
            }
        }
    }
}

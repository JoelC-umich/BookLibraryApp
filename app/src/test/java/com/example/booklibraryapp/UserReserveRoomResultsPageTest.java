package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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

import java.util.ArrayList;
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

    @Test
    public void testNoDateProvided() {
        // Missing "selectedDate" argument
        try (FragmentScenario<UserReserveRoomResultsPage> scenario = FragmentScenario.launchInContainer(UserReserveRoomResultsPage.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                ListView listView = fragment.requireView().findViewById(R.id.listUserReserveRoomResults);
                // Adapter should be null because it returns early
                assertEquals(null, listView.getAdapter());
            });
        }
    }

    @Test
    public void testNoRoomsAvailable() {
        Bundle args = new Bundle();
        args.putString("selectedDate", "2023-12-25");

        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getRoomIDsQuery).thenReturn(new ArrayList<>()); // No rooms in DB

            try (FragmentScenario<UserReserveRoomResultsPage> scenario = FragmentScenario.launchInContainer(UserReserveRoomResultsPage.class, args, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Button buttonReserve = fragment.requireView().findViewById(R.id.buttonReserveRoomReserve);
                    assertFalse(buttonReserve.isEnabled());
                });
            }
        }
    }

    @Test
    public void testReserveWithoutSelection() {
        Bundle args = new Bundle();
        args.putString("selectedDate", "2023-12-25");
        NavController mockNavController = mock(NavController.class);

        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getRoomIDsQuery).thenReturn(Arrays.asList("1"));
            mockedHelper.when(() -> QueryConnectorPlusHelper.getRoomIDsReservedOnDateQuery(anyString())).thenReturn(new ArrayList<>());

            try (FragmentScenario<UserReserveRoomResultsPage> scenario = FragmentScenario.launchInContainer(UserReserveRoomResultsPage.class, args, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);
                    Button buttonReserve = fragment.requireView().findViewById(R.id.buttonReserveRoomReserve);
                    
                    buttonReserve.performClick();

                    // Verify no reservation query is run and no navigation occurs
                    mockedHelper.verify(() -> QueryConnectorPlusHelper.insertRoomReservationQuery(anyString(), anyString(), anyString(), anyString()), never());
                    verify(mockNavController, never()).navigate(anyInt());
                });
            }
        }
    }
}

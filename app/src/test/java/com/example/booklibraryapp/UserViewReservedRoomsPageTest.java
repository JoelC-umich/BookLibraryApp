package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class UserViewReservedRoomsPageTest {

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
    public void testFragmentUIInitialization() throws Exception {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            Connection mockConn = mock(Connection.class);
            Statement mockStatement = mock(Statement.class);
            ResultSet mockResultSet = mock(ResultSet.class);

            mockedHelper.when(QueryConnectorPlusHelper::Connector).thenReturn(mockConn);
            when(mockConn.createStatement()).thenReturn(mockStatement);
            when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

            // Mock one row in result set
            when(mockResultSet.next()).thenReturn(true).thenReturn(false);
            when(mockResultSet.getString("ROOM_ID")).thenReturn("101");
            when(mockResultSet.getInt("SLOT")).thenReturn(1);
            when(mockResultSet.getString("RESERVE_STATUS")).thenReturn("Reserved");

            mockedHelper.when(() -> QueryConnectorPlusHelper.slotToTime(1)).thenReturn("8:00 AM");

            try (FragmentScenario<UserViewReservedRoomsPage> scenario = FragmentScenario.launchInContainer(UserViewReservedRoomsPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ListView listView = fragment.requireView().findViewById(R.id.listUserViewReservedRooms);
                    CalendarView calendarView = fragment.requireView().findViewById(R.id.calendarUserViewReservedRooms);

                    assertNotNull(listView);
                    assertNotNull(calendarView);

                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                    assertNotNull(adapter);
                    assertEquals(1, adapter.getCount());
                });
            }
        }
    }
}

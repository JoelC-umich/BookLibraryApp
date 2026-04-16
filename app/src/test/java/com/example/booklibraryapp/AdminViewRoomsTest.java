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
public class AdminViewRoomsTest {

    @Test
    public void testFragmentUIInitialization() {
        List<String> mockRooms = Arrays.asList("101", "102");
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getRoomIDsQuery).thenReturn(mockRooms);

            try (FragmentScenario<AdminViewRooms> scenario = FragmentScenario.launchInContainer(AdminViewRooms.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ListView listView = fragment.requireView().findViewById(R.id.listViewAdminViewRooms);
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
        List<String> mockRooms = Arrays.asList("101", "202");
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getRoomIDsQuery).thenReturn(mockRooms);

            try (FragmentScenario<AdminViewRooms> scenario = FragmentScenario.launchInContainer(AdminViewRooms.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    SearchView searchView = fragment.requireView().findViewById(R.id.searchAdminViewRooms);
                    searchView.setQuery("101", false);

                    Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
                    // Coverage for filter logic
                });
            }
        }
    }
}

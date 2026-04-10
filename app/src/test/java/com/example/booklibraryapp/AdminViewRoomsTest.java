package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mockStatic;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class AdminViewRoomsTest {

    private MockedStatic<QueryConnectorPlusHelper> mockedHelper;
    private List<String> testRooms = Arrays.asList("101", "102", "201");

    @Before
    public void setUp() {
        mockedHelper = mockStatic(QueryConnectorPlusHelper.class);
        mockedHelper.when(QueryConnectorPlusHelper::getRoomIDsQuery).thenReturn(testRooms);
    }

    @After
    public void tearDown() {
        mockedHelper.close();
    }

    @Test
    public void testListViewPopulated() {
        try (FragmentScenario<AdminViewRooms> scenario = FragmentScenario.launchInContainer(AdminViewRooms.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                ListView listView = fragment.requireView().findViewById(R.id.listViewAdminViewRooms);
                assertNotNull(listView);
                assertEquals(testRooms.size(), listView.getAdapter().getCount());
                assertEquals("101", listView.getAdapter().getItem(0));
            });
        }
    }

    @Test
    public void testSearchFilter() {
        try (FragmentScenario<AdminViewRooms> scenario = FragmentScenario.launchInContainer(AdminViewRooms.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                SearchView searchView = fragment.requireView().findViewById(R.id.searchAdminViewRooms);
                ListView listView = fragment.requireView().findViewById(R.id.listViewAdminViewRooms);
                
                searchView.setQuery("201", true);
                
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                assertNotNull(adapter.getFilter());
            });
        }
    }
}

package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mockStatic;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class AdminViewBookRequestsPageTest {

    @Test
    public void testFragmentUIInitialization() {
        List<String> mockRequests = Arrays.asList("Request #1: User A - Book 1", "Request #2: User B - Book 2");
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getPendingBooksReservedWithDetailsQuery).thenReturn(mockRequests);

            try (FragmentScenario<AdminViewBookRequestsPage> scenario = FragmentScenario.launchInContainer(AdminViewBookRequestsPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ListView listView = fragment.requireView().findViewById(R.id.listAdminViewBookRequests);
                    assertNotNull(listView);

                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                    assertNotNull(adapter);
                    assertEquals(2, adapter.getCount());
                });
            }
        }
    }
}

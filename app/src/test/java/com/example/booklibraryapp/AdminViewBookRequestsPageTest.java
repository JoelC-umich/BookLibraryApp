package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mockStatic;
import static org.robolectric.Shadows.shadowOf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowToast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class AdminViewBookRequestsPageTest {

    @Test
    public void testFragmentUIInitialization() {
        List<String> mockRequests = Arrays.asList("REQ001", "REQ002");
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getPendingBooksReservedQuery).thenReturn(mockRequests);

            try (FragmentScenario<AdminViewBookRequestsPage> scenario = FragmentScenario.launchInContainer(AdminViewBookRequestsPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ListView listView = fragment.requireView().findViewById(R.id.listAdminViewBookRequests);
                    assertNotNull(listView);
                    
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                    assertNotNull(adapter);
                    assertEquals(2, adapter.getCount());
                    assertEquals("REQ001", adapter.getItem(0));
                });
            }
        }
    }

    @Test
    public void testEmptyList() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getPendingBooksReservedQuery).thenReturn(Collections.emptyList());

            try (FragmentScenario<AdminViewBookRequestsPage> scenario = FragmentScenario.launchInContainer(AdminViewBookRequestsPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ListView listView = fragment.requireView().findViewById(R.id.listAdminViewBookRequests);
                    assertEquals(0, listView.getAdapter().getCount());
                });
            }
        }
    }

    @Test
    public void testItemClickShowsDialogWithDetails() {
        String reqId = "REQ001";
        String bookId = "BOOK123";
        String userId = "USER456";
        
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getPendingBooksReservedQuery).thenReturn(Collections.singletonList(reqId));
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBookIDFromBorrowedBooksID(reqId)).thenReturn(bookId);
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUserIDFromBorrowedBooksID(reqId)).thenReturn(userId);
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBookTitleFromBookID(bookId)).thenReturn("Test Book Title");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getFirstNameFromIDQuery(userId)).thenReturn("John");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getLastNameFromIDQuery(userId)).thenReturn("Doe");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getEmailFromIDQuery(userId)).thenReturn("john.doe@example.com");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getDateFromBorrowedBooksID(bookId)).thenReturn("2023-10-27");

            try (FragmentScenario<AdminViewBookRequestsPage> scenario = FragmentScenario.launchInContainer(AdminViewBookRequestsPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ListView listView = fragment.requireView().findViewById(R.id.listAdminViewBookRequests);
                    listView.performItemClick(null, 0, 0);

                    AlertDialog dialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
                    assertNotNull(dialog);
                    
                    ShadowAlertDialog shadowDialog = shadowOf(dialog);
                    assertEquals("Book Request " + reqId, shadowDialog.getTitle());
                    String message = shadowDialog.getMessage().toString();
                    assertTrue(message.contains("Book ID: " + bookId));
                    assertTrue(message.contains("Book Title: Test Book Title"));
                    assertTrue(message.contains("Full Name: John Doe"));
                    assertTrue(message.contains("Email: john.doe@example.com"));
                    assertTrue(message.contains("Date Borrowing: 2023-10-27"));
                });
            }
        }
    }

    @Test
    public void testApproveRequest() {
        String reqId = "REQ001";
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getPendingBooksReservedQuery).thenReturn(Collections.singletonList(reqId));
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBookIDFromBorrowedBooksID(reqId)).thenReturn("B1");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUserIDFromBorrowedBooksID(reqId)).thenReturn("U1");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBookTitleFromBookID("B1")).thenReturn("Title1");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getFirstNameFromIDQuery("U1")).thenReturn("First");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getLastNameFromIDQuery("U1")).thenReturn("Last");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getEmailFromIDQuery("U1")).thenReturn("email@example.com");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getDateFromBorrowedBooksID("B1")).thenReturn("2023-01-01");

            try (FragmentScenario<AdminViewBookRequestsPage> scenario = FragmentScenario.launchInContainer(AdminViewBookRequestsPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ListView listView = fragment.requireView().findViewById(R.id.listAdminViewBookRequests);
                    listView.performItemClick(null, 0, 0);

                    AlertDialog dialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
                    assertNotNull(dialog);
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                    ShadowLooper.idleMainLooper();

                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(contains("UPDATE BOOKS_RESERVED SET RESERVE_STATUS = 'Reserved' WHERE ID = '" + reqId + "'")));
                    assertEquals("Request " + reqId + " is successfully approved", ShadowToast.getTextOfLatestToast());
                });
            }
        }
    }

    @Test
    public void testDenyRequest() {
        String reqId = "REQ002";
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getPendingBooksReservedQuery).thenReturn(Collections.singletonList(reqId));
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBookIDFromBorrowedBooksID(reqId)).thenReturn("B2");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUserIDFromBorrowedBooksID(reqId)).thenReturn("U2");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBookTitleFromBookID("B2")).thenReturn("Title2");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getFirstNameFromIDQuery("U2")).thenReturn("First2");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getLastNameFromIDQuery("U2")).thenReturn("Last2");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getEmailFromIDQuery("U2")).thenReturn("email2@example.com");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getDateFromBorrowedBooksID("B2")).thenReturn("2023-01-02");

            try (FragmentScenario<AdminViewBookRequestsPage> scenario = FragmentScenario.launchInContainer(AdminViewBookRequestsPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ListView listView = fragment.requireView().findViewById(R.id.listAdminViewBookRequests);
                    listView.performItemClick(null, 0, 0);

                    AlertDialog dialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
                    assertNotNull(dialog);
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).performClick();
                    ShadowLooper.idleMainLooper();

                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(contains("UPDATE BOOKS_RESERVED SET RESERVE_STATUS = 'Available' WHERE ID = '" + reqId + "'")));
                    assertEquals("Request " + reqId + " has been denied", ShadowToast.getTextOfLatestToast());
                });
            }
        }
    }
}

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
public class AdminViewRoomRequestsPageTest {

    @Test
    public void testFragmentUIInitialization() {
        List<String> mockRequests = Arrays.asList("ROOM_REQ_1", "ROOM_REQ_2");
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getPendingRoomsReservedQuery).thenReturn(mockRequests);

            try (FragmentScenario<AdminViewRoomRequestsPage> scenario = FragmentScenario.launchInContainer(AdminViewRoomRequestsPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ListView listView = fragment.requireView().findViewById(R.id.listViewAdminRoomRequests);
                    assertNotNull(listView);
                    
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                    assertNotNull(adapter);
                    assertEquals(2, adapter.getCount());
                    assertEquals("ROOM_REQ_1", adapter.getItem(0));
                });
            }
        }
    }

    @Test
    public void testItemClickShowsDialogWithDetails() {
        String reqId = "REQ101";
        String roomId = "ROOM_A";
        String userId = "USER789";
        
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getPendingRoomsReservedQuery).thenReturn(Collections.singletonList(reqId));
            mockedHelper.when(() -> QueryConnectorPlusHelper.getRoomIDFromReserveID(reqId)).thenReturn(roomId);
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUserIDFromReserveID(reqId)).thenReturn(userId);
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUsernameFromID(userId)).thenReturn("jdoe");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getFirstNameFromIDQuery(userId)).thenReturn("Jane");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getLastNameFromIDQuery(userId)).thenReturn("Doe");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getEmailFromIDQuery(userId)).thenReturn("jane.doe@example.com");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getSlotFromReserveID(reqId)).thenReturn("10:00 - 11:00");

            try (FragmentScenario<AdminViewRoomRequestsPage> scenario = FragmentScenario.launchInContainer(AdminViewRoomRequestsPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ListView listView = fragment.requireView().findViewById(R.id.listViewAdminRoomRequests);
                    listView.performItemClick(null, 0, 0);

                    AlertDialog dialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
                    assertNotNull(dialog);
                    
                    ShadowAlertDialog shadowDialog = shadowOf(dialog);
                    assertEquals("Room Request " + reqId, shadowDialog.getTitle());
                    String message = shadowDialog.getMessage().toString();
                    assertTrue(message.contains("Room ID: " + roomId));
                    assertTrue(message.contains("Username: jdoe"));
                    assertTrue(message.contains("Full Name: Jane Doe"));
                    assertTrue(message.contains("Email: jane.doe@example.com"));
                    assertTrue(message.contains("Slot: 10:00 - 11:00"));
                });
            }
        }
    }

    @Test
    public void testApproveRoomRequest() {
        String reqId = "REQ101";
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getPendingRoomsReservedQuery).thenReturn(Collections.singletonList(reqId));
            mockedHelper.when(() -> QueryConnectorPlusHelper.getRoomIDFromReserveID(reqId)).thenReturn("R1");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUserIDFromReserveID(reqId)).thenReturn("U1");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUsernameFromID("U1")).thenReturn("user1");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getFirstNameFromIDQuery("U1")).thenReturn("User");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getLastNameFromIDQuery("U1")).thenReturn("One");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getEmailFromIDQuery("U1")).thenReturn("user1@example.com");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getSlotFromReserveID(reqId)).thenReturn("10:00-11:00");

            try (FragmentScenario<AdminViewRoomRequestsPage> scenario = FragmentScenario.launchInContainer(AdminViewRoomRequestsPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ListView listView = fragment.requireView().findViewById(R.id.listViewAdminRoomRequests);
                    listView.performItemClick(null, 0, 0);

                    AlertDialog dialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
                    assertNotNull("Dialog should not be null", dialog);
                    
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                    ShadowLooper.idleMainLooper();

                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(contains("UPDATE ROOMS_RESERVED SET RESERVE_STATUS = 'Reserved' WHERE ID = '" + reqId + "'")));
                    assertEquals("Request " + reqId + " is successfully approved", ShadowToast.getTextOfLatestToast());
                });
            }
        }
    }

    @Test
    public void testDenyRoomRequest() {
        String reqId = "REQ102";
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getPendingRoomsReservedQuery).thenReturn(Collections.singletonList(reqId));
            mockedHelper.when(() -> QueryConnectorPlusHelper.getRoomIDFromReserveID(reqId)).thenReturn("R2");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUserIDFromReserveID(reqId)).thenReturn("U2");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getUsernameFromID("U2")).thenReturn("user2");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getFirstNameFromIDQuery("U2")).thenReturn("User");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getLastNameFromIDQuery("U2")).thenReturn("Two");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getEmailFromIDQuery("U2")).thenReturn("user2@example.com");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getSlotFromReserveID(reqId)).thenReturn("11:00-12:00");

            try (FragmentScenario<AdminViewRoomRequestsPage> scenario = FragmentScenario.launchInContainer(AdminViewRoomRequestsPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ListView listView = fragment.requireView().findViewById(R.id.listViewAdminRoomRequests);
                    listView.performItemClick(null, 0, 0);

                    AlertDialog dialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
                    assertNotNull("Dialog should not be null", dialog);
                    
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).performClick();
                    ShadowLooper.idleMainLooper();

                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(contains("UPDATE ROOMS_RESERVED SET RESERVE_STATUS = 'Available' WHERE ID = '" + reqId + "'")));
                    assertEquals("Request " + reqId + " has been denied", ShadowToast.getTextOfLatestToast());
                });
            }
        }
    }
}

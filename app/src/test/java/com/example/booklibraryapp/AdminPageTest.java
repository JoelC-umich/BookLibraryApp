package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowToast;

import java.util.Collections;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class AdminPageTest {

    private MockedStatic<QueryConnectorPlusHelper> mockedHelper;

    @Before
    public void setUp() {
        mockedHelper = mockStatic(QueryConnectorPlusHelper.class);
        QueryConnectorPlusHelper.IDWhenLoggingIn = "1";
        mockedHelper.when(() -> QueryConnectorPlusHelper.getFirstNameFromIDQuery("1")).thenReturn("TestAdmin");
    }

    @After
    public void tearDown() {
        mockedHelper.close();
    }

    @Test
    public void testWelcomeMessage() {
        try (FragmentScenario<AdminPage> scenario = FragmentScenario.launchInContainer(AdminPage.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                TextView welcomeText = fragment.requireView().findViewById(R.id.textAdminWelcomeText);
                assertEquals("Welcome Librarian TestAdmin!", welcomeText.getText().toString());
            });
        }
    }

    @Test
    public void testNavigationToViewAccounts() {
        testNavigation(R.id.btnAdminViewAccounts, R.id.action_AdminPage_to_adminAccountsInfoPage);
    }

    @Test
    public void testNavigationToBorrowedBooks() {
        testNavigation(R.id.btnAdminViewBorrowedBooks, R.id.action_AdminPage_to_adminViewBorrowedBooksPage);
    }

    @Test
    public void testNavigationToRoomReservations() {
        testNavigation(R.id.btnAdminViewRoomReservations, R.id.action_AdminPage_to_adminViewRoomReservationsPage);
    }

    @Test
    public void testNavigationToRoomRequests() {
        testNavigation(R.id.btnAdminViewRoomRequests, R.id.action_AdminPage_to_adminViewRoomRequestsPage);
    }

    @Test
    public void testNavigationToBookRequests() {
        testNavigation(R.id.btnAdminViewBookRequests, R.id.action_AdminPage_to_adminViewBookRequestsPage);
    }

    @Test
    public void testNavigationToLogout() {
        testNavigation(R.id.btnAdminLogout, R.id.action_AdminPage_to_LoginPage);
    }

    @Test
    public void testNavigationToCreateAccount() {
        testNavigation(R.id.btnAdminViewCreateAccount, R.id.action_AdminPage_to_createUserPageAdmin);
    }

    @Test
    public void testNavigationToBookInventory() {
        testNavigation(R.id.btnAdminViewBooksInventory, R.id.action_AdminPage_to_adminViewBookInventory);
    }

    @Test
    public void testNavigationToCreateBookEntry() {
        testNavigation(R.id.btnAdminCreateBookEntryInventory, R.id.action_AdminPage_to_adminCreateBookEntryInventory);
    }

    @Test
    public void testNavigationToViewRooms() {
        testNavigation(R.id.btnAdminViewRooms, R.id.action_AdminPage_to_adminViewRooms);
    }

    private void testNavigation(int buttonId, int actionId) {
        try (FragmentScenario<AdminPage> scenario = FragmentScenario.launchInContainer(AdminPage.class, null, R.style.Theme_BookLibraryApp)) {
            NavController mockNavController = mock(NavController.class);
            scenario.onFragment(fragment -> {
                Navigation.setViewNavController(fragment.requireView(), mockNavController);
                fragment.requireView().findViewById(buttonId).performClick();
                verify(mockNavController).navigate(actionId);
            });
        }
    }

    @Test
    public void testCreateRoom_Success() {
        mockedHelper.when(QueryConnectorPlusHelper::getRoomIDsQuery).thenReturn(Collections.emptyList());

        try (FragmentScenario<AdminPage> scenario = FragmentScenario.launchInContainer(AdminPage.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                fragment.requireView().findViewById(R.id.btnAdminCreateRoom).performClick();
                ShadowLooper.idleMainLooper();

                AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
                assertNotNull("Dialog should not be null", dialog);

                EditText input = dialog.findViewById(android.R.id.edit);
                assertNotNull("EditText should not be null", input);
                input.setText("101");

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                ShadowLooper.idleMainLooper();

                mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(contains("INSERT INTO ROOMS VALUES ('101')")));
                assertEquals("Room has been successfully added", ShadowToast.getTextOfLatestToast());
            });
        }
    }

    @Test
    public void testCreateRoom_BlankInput() {
        try (FragmentScenario<AdminPage> scenario = FragmentScenario.launchInContainer(AdminPage.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                fragment.requireView().findViewById(R.id.btnAdminCreateRoom).performClick();
                ShadowLooper.idleMainLooper();

                AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
                assertNotNull(dialog);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                ShadowLooper.idleMainLooper();

                assertEquals("Please enter a number", ShadowToast.getTextOfLatestToast());
            });
        }
    }

    @Test
    public void testCreateRoom_AlreadyExists() {
        mockedHelper.when(QueryConnectorPlusHelper::getRoomIDsQuery).thenReturn(Collections.singletonList("101"));

        try (FragmentScenario<AdminPage> scenario = FragmentScenario.launchInContainer(AdminPage.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                fragment.requireView().findViewById(R.id.btnAdminCreateRoom).performClick();
                ShadowLooper.idleMainLooper();

                AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
                assertNotNull(dialog);
                EditText input = dialog.findViewById(android.R.id.edit);
                assertNotNull(input);
                input.setText("101");

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                ShadowLooper.idleMainLooper();

                assertEquals("Room number already exists\nPlease try again", ShadowToast.getTextOfLatestToast());
            });
        }
    }

    @Test
    public void testCreateRoom_ExceedsLimit() {
        mockedHelper.when(QueryConnectorPlusHelper::getRoomIDsQuery).thenReturn(Collections.emptyList());

        try (FragmentScenario<AdminPage> scenario = FragmentScenario.launchInContainer(AdminPage.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                fragment.requireView().findViewById(R.id.btnAdminCreateRoom).performClick();
                ShadowLooper.idleMainLooper();

                AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
                assertNotNull(dialog);
                EditText input = dialog.findViewById(android.R.id.edit);
                assertNotNull(input);
                input.setText("2000000");

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                ShadowLooper.idleMainLooper();

                assertEquals("Room number exceeds allowed value\nPlease try again", ShadowToast.getTextOfLatestToast());
            });
        }
    }

    @Test
    public void testCreateRoom_Cancel() {
        try (FragmentScenario<AdminPage> scenario = FragmentScenario.launchInContainer(AdminPage.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                fragment.requireView().findViewById(R.id.btnAdminCreateRoom).performClick();
                ShadowLooper.idleMainLooper();

                AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
                assertNotNull(dialog);
                assertTrue(dialog.isShowing());

                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).performClick();
                ShadowLooper.idleMainLooper();
                assertTrue(!dialog.isShowing());
            });
        }
    }
}

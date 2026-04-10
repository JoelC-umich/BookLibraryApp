package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

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

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class UserPageTest {

    private MockedStatic<QueryConnectorPlusHelper> mockedHelper;

    @Before
    public void setUp() {
        mockedHelper = mockStatic(QueryConnectorPlusHelper.class);
        QueryConnectorPlusHelper.IDWhenLoggingIn = "1";
        mockedHelper.when(() -> QueryConnectorPlusHelper.getFirstNameFromIDQuery("1")).thenReturn("John");
    }

    @After
    public void tearDown() {
        mockedHelper.close();
    }

    @Test
    public void testWelcomeMessage() {
        try (FragmentScenario<UserPage> scenario = FragmentScenario.launchInContainer(UserPage.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                TextView welcomeText = fragment.requireView().findViewById(R.id.textUserWelcome);
                assertEquals("Welcome John!", welcomeText.getText().toString());
            });
        }
    }

    @Test
    public void testNavigationToReserveBook() {
        testNavigation(R.id.btnUserPageReserveBook, R.id.action_userPage_to_userReserveBookPage);
    }

    @Test
    public void testNavigationToReserveRoom() {
        testNavigation(R.id.btnUserPageReserveRoom, R.id.action_userPage_to_userReserveRoomPage);
    }

    @Test
    public void testNavigationToViewReservedBooks() {
        testNavigation(R.id.btnUserPageViewReservedBooks, R.id.action_userPage_to_userViewReservedBooksPage);
    }

    @Test
    public void testNavigationToViewReservedRooms() {
        testNavigation(R.id.btnUserPageViewRoomReservation, R.id.action_userPage_to_userViewReservedRoomsPage);
    }

    @Test
    public void testNavigationToLogout() {
        testNavigation(R.id.btnUserLogout, R.id.action_userPage_to_LoginPage);
    }

    private void testNavigation(int buttonId, int actionId) {
        try (FragmentScenario<UserPage> scenario = FragmentScenario.launchInContainer(UserPage.class, null, R.style.Theme_BookLibraryApp)) {
            NavController mockNavController = mock(NavController.class);
            scenario.onFragment(fragment -> {
                Navigation.setViewNavController(fragment.requireView(), mockNavController);
                fragment.requireView().findViewById(buttonId).performClick();
                verify(mockNavController).navigate(actionId);
            });
        }
    }
}

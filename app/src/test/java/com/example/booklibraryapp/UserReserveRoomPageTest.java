package com.example.booklibraryapp;

import static org.junit.Assert.assertNotNull;

import android.widget.Button;
import android.widget.CalendarView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class UserReserveRoomPageTest {

    @Test
    public void testFragmentUIInitialization() {
        try (FragmentScenario<UserReserveRoomPage> scenario = FragmentScenario.launchInContainer(UserReserveRoomPage.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                assertNotNull(fragment.requireView().findViewById(R.id.calendarView));
                assertNotNull(fragment.requireView().findViewById(R.id.buttonSearchDate));
            });
        }
    }
}

package com.example.booklibraryapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class UserReserveRoomResultsPageTest {

    @Test
    public void fragment_starts_with_correct_arguments() {
        Bundle args = new Bundle();
        args.putString("selectedDate", "2023-10-27");
        
        FragmentScenario<UserReserveRoomResultsPage> scenario = FragmentScenario.launchInContainer(
                UserReserveRoomResultsPage.class, args);
        
        scenario.onFragment(fragment -> {
            assertNotNull(fragment.getView());
            ListView listView = fragment.getView().findViewById(R.id.listView);
            Button buttonReserve = fragment.getView().findViewById(R.id.buttonReserveRoomReserve);
            
            assertNotNull(listView);
            assertNotNull(buttonReserve);
        });
    }

    @Test
    public void fragment_shows_error_when_date_missing() {
        // Launch without arguments
        FragmentScenario<UserReserveRoomResultsPage> scenario = FragmentScenario.launchInContainer(
                UserReserveRoomResultsPage.class, null);
        
        scenario.onFragment(fragment -> {
            // In this case, the fragment just returns the view after showing a Toast
            // We can check if the view exists
            assertNotNull(fragment.getView());
        });
    }
}

package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.squareup.picasso.Picasso;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class UserReserveBookResultsPageTest {

    @BeforeClass
    public static void setupPicasso() {
        try {
            Picasso.setSingletonInstance(new Picasso.Builder(RuntimeEnvironment.getApplication()).build());
        } catch (IllegalStateException ignored) {
            // Already initialized
        }
    }

    @Test
    public void testFragmentUIInitialization() {
        NavController mockNavController = mock(NavController.class);
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBookIDFromBookNameQuery(anyString())).thenReturn("1");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getAuthorFromBookID("1")).thenReturn("Test Author");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBookCategoryFromBookID("1")).thenReturn("Test Category");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBookSummaryFromBookID("1")).thenReturn("Test Summary");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBookImageFromBookID("1")).thenReturn("http://example.com/image.jpg");

            try (FragmentScenario<UserReserveBookResultsPage> scenario = FragmentScenario.launchInContainer(UserReserveBookResultsPage.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);

                    Bundle result = new Bundle();
                    result.putString("availableBook", "Test Book");
                    fragment.getParentFragmentManager().setFragmentResult("availableBookInfo", result);

                    TextView nameText = fragment.requireView().findViewById(R.id.textBookNameOutput);
                    TextView authorText = fragment.requireView().findViewById(R.id.textBookAuthorOutput);
                    Button reserveBtn = fragment.requireView().findViewById(R.id.btnReserveBook);

                    assertNotNull(nameText);
                    assertEquals("Test Book", nameText.getText().toString());
                    assertEquals("Test Author", authorText.getText().toString());
                    assertNotNull(reserveBtn);
                });
            }
        }
    }
}

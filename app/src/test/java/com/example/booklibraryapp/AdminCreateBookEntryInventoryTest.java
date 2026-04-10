package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import android.widget.Button;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.textfield.TextInputEditText;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

import java.util.Collections;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class AdminCreateBookEntryInventoryTest {

    @Test
    public void testCreateBookSuccess() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getLastIDPlus1BooksQuery).thenReturn("5");
            mockedHelper.when(QueryConnectorPlusHelper::getBookNamesQuery).thenReturn(Collections.emptyList());

            try (FragmentScenario<AdminCreateBookEntryInventory> scenario = FragmentScenario.launchInContainer(AdminCreateBookEntryInventory.class, null, R.style.Theme_BookLibraryApp)) {
                NavController mockNavController = mock(NavController.class);
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);
                    
                    ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminCreateBookName)).setText("New Book");
                    ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminCreateBookAuthor)).setText("Author A");
                    ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminCreateBookCategory)).setText("Cat B");
                    ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminCreateBookSummary)).setText("Summary C");
                    ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminCreateBookQuantity)).setText("10");
                    
                    fragment.requireView().findViewById(R.id.btnAdminCreateBook).performClick();

                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(contains("INSERT INTO BOOKS VALUES ('5', 'New Book'")));
                    assertEquals("Book entry successfully added to inventory", ShadowToast.getTextOfLatestToast());
                    verify(mockNavController).navigate(R.id.action_adminCreateBookEntryInventory_to_AdminPage);
                });
            }
        }
    }

    @Test
    public void testCreateBookFailure_BlankFields() {
        try (FragmentScenario<AdminCreateBookEntryInventory> scenario = FragmentScenario.launchInContainer(AdminCreateBookEntryInventory.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                fragment.requireView().findViewById(R.id.btnAdminCreateBook).performClick();
                assertEquals("One of the fields are blank\nPlease fill and try again", ShadowToast.getTextOfLatestToast());
            });
        }
    }

    @Test
    public void testCreateBookFailure_DuplicateName() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getBookNamesQuery).thenReturn(Collections.singletonList("existing book"));

            try (FragmentScenario<AdminCreateBookEntryInventory> scenario = FragmentScenario.launchInContainer(AdminCreateBookEntryInventory.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminCreateBookName)).setText("Existing Book");
                    ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminCreateBookAuthor)).setText("Author");
                    ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminCreateBookCategory)).setText("Cat");
                    ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminCreateBookSummary)).setText("Summary");
                    ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminCreateBookQuantity)).setText("1");
                    
                    fragment.requireView().findViewById(R.id.btnAdminCreateBook).performClick();
                    assertEquals("Book name already exists\nPlease try again", ShadowToast.getTextOfLatestToast());
                });
            }
        }
    }
}

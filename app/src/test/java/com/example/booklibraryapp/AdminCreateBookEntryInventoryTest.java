package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import android.view.View;

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
import java.util.List;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class AdminCreateBookEntryInventoryTest {

    @Test
    public void testFragmentInitialization() {
        try (FragmentScenario<AdminCreateBookEntryInventory> scenario = FragmentScenario.launchInContainer(AdminCreateBookEntryInventory.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                View view = fragment.requireView();
                assertNotNull(view.findViewById(R.id.inputAdminCreateBookName));
                assertNotNull(view.findViewById(R.id.inputAdminCreateBookAuthor));
                assertNotNull(view.findViewById(R.id.inputAdminCreateBookCategory));
                assertNotNull(view.findViewById(R.id.inputAdminCreateBookSummary));
                assertNotNull(view.findViewById(R.id.inputAdminCreateBookImage));
                assertNotNull(view.findViewById(R.id.inputAdminCreateBookQuantity));
                assertNotNull(view.findViewById(R.id.btnAdminCreateBook));
            });
        }
    }

    @Test
    public void testValidationBlankFields() {
        try (FragmentScenario<AdminCreateBookEntryInventory> scenario = FragmentScenario.launchInContainer(AdminCreateBookEntryInventory.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                View view = fragment.requireView();
                // Set mock NavController to prevent crashes
                Navigation.setViewNavController(view, mock(NavController.class));
                
                view.findViewById(R.id.btnAdminCreateBook).performClick();

                assertEquals("One of the fields are blank\nPlease fill and try again", ShadowToast.getTextOfLatestToast());
            });
        }
    }

    @Test
    public void testValidationDuplicateBookName() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            // The production code uses .toString().contains(bookName.toLowerCase())
            // To trigger this, we mock a list that, when stringified, contains the lowercase search term.
            List<String> existingBooks = Collections.singletonList("existing book");
            mockedHelper.when(QueryConnectorPlusHelper::getBookNamesQuery).thenReturn(existingBooks);

            try (FragmentScenario<AdminCreateBookEntryInventory> scenario = FragmentScenario.launchInContainer(AdminCreateBookEntryInventory.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    View view = fragment.requireView();
                    Navigation.setViewNavController(view, mock(NavController.class));

                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookName)).setText("Existing Book");
                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookAuthor)).setText("Author");
                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookCategory)).setText("Category");
                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookSummary)).setText("Summary");
                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookQuantity)).setText("10");

                    view.findViewById(R.id.btnAdminCreateBook).performClick();

                    assertEquals("Book name already exists\nPlease try again", ShadowToast.getTextOfLatestToast());
                });
            }
        }
    }

    @Test
    public void testValidationSummaryTooLong() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getBookNamesQuery).thenReturn(Collections.emptyList());

            try (FragmentScenario<AdminCreateBookEntryInventory> scenario = FragmentScenario.launchInContainer(AdminCreateBookEntryInventory.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    View view = fragment.requireView();
                    Navigation.setViewNavController(view, mock(NavController.class));

                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookName)).setText("New Book");
                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookAuthor)).setText("Author");
                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookCategory)).setText("Category");
                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookQuantity)).setText("10");

                    StringBuilder longSummary = new StringBuilder();
                    for (int i = 0; i < 501; i++) {
                        longSummary.append("a");
                    }
                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookSummary)).setText(longSummary.toString());

                    view.findViewById(R.id.btnAdminCreateBook).performClick();

                    assertEquals("Summary is too long\nPlease shorten and try again", ShadowToast.getTextOfLatestToast());
                });
            }
        }
    }

    @Test
    public void testSuccessfulBookCreation_NoId() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getBookNamesQuery).thenReturn(Collections.emptyList());
            mockedHelper.when(QueryConnectorPlusHelper::getLastIDPlus1BooksQuery).thenReturn(null);

            try (FragmentScenario<AdminCreateBookEntryInventory> scenario = FragmentScenario.launchInContainer(AdminCreateBookEntryInventory.class, null, R.style.Theme_BookLibraryApp)) {
                NavController mockNavController = mock(NavController.class);
                scenario.onFragment(fragment -> {
                    View view = fragment.requireView();
                    Navigation.setViewNavController(view, mockNavController);

                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookName)).setText("Success Book");
                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookAuthor)).setText("Success Author");
                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookCategory)).setText("Success Category");
                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookSummary)).setText("Success Summary");
                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookQuantity)).setText("5");
                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookImage)).setText("image_url");

                    view.findViewById(R.id.btnAdminCreateBook).performClick();

                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(contains("INSERT INTO BOOKS VALUES (0, 'Success Book'")));
                    assertEquals("Book entry successfully added to inventory", ShadowToast.getTextOfLatestToast());
                    verify(mockNavController).navigate(R.id.action_adminCreateBookEntryInventory_to_AdminPage);
                });
            }
        }
    }

    @Test
    public void testSuccessfulBookCreation_WithId() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getBookNamesQuery).thenReturn(Collections.emptyList());
            mockedHelper.when(QueryConnectorPlusHelper::getLastIDPlus1BooksQuery).thenReturn("42");

            try (FragmentScenario<AdminCreateBookEntryInventory> scenario = FragmentScenario.launchInContainer(AdminCreateBookEntryInventory.class, null, R.style.Theme_BookLibraryApp)) {
                NavController mockNavController = mock(NavController.class);
                scenario.onFragment(fragment -> {
                    View view = fragment.requireView();
                    Navigation.setViewNavController(view, mockNavController);

                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookName)).setText("New Book 42");
                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookAuthor)).setText("Author 42");
                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookCategory)).setText("Category 42");
                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookSummary)).setText("Summary 42");
                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookQuantity)).setText("1");
                    ((TextInputEditText) view.findViewById(R.id.inputAdminCreateBookImage)).setText("img42");

                    view.findViewById(R.id.btnAdminCreateBook).performClick();

                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(contains("INSERT INTO BOOKS VALUES ('42', 'New Book 42'")));
                    assertEquals("Book entry successfully added to inventory", ShadowToast.getTextOfLatestToast());
                    verify(mockNavController).navigate(R.id.action_adminCreateBookEntryInventory_to_AdminPage);
                });
            }
        }
    }
}

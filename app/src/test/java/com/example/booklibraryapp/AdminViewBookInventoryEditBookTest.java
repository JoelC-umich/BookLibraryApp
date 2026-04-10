package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.textfield.TextInputEditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class AdminViewBookInventoryEditBookTest {

    private MockedStatic<QueryConnectorPlusHelper> mockedHelper;

    @Before
    public void setUp() {
        mockedHelper = mockStatic(QueryConnectorPlusHelper.class);
    }

    @After
    public void tearDown() {
        mockedHelper.close();
    }

    @Test
    public void testBookDetailsPopulated() {
        mockedHelper.when(() -> QueryConnectorPlusHelper.getBookIDFromBookNameQuery("Test Book")).thenReturn("1");
        mockedHelper.when(() -> QueryConnectorPlusHelper.getBookTitleFromBookID("1")).thenReturn("Test Book");
        mockedHelper.when(() -> QueryConnectorPlusHelper.getAuthorFromBookID("1")).thenReturn("Author");
        mockedHelper.when(() -> QueryConnectorPlusHelper.getBookCategoryFromBookID("1")).thenReturn("Category");
        mockedHelper.when(() -> QueryConnectorPlusHelper.getBookSummaryFromBookID("1")).thenReturn("Summary");
        mockedHelper.when(() -> QueryConnectorPlusHelper.getQuantityTotalFromBookID("1")).thenReturn("10");
        mockedHelper.when(() -> QueryConnectorPlusHelper.getQuantityBorrowedFromBookID("1")).thenReturn("2");
        mockedHelper.when(() -> QueryConnectorPlusHelper.getQuantityAvailableFromBookID("1")).thenReturn("8");
        mockedHelper.when(() -> QueryConnectorPlusHelper.getBookImageFromBookID("1")).thenReturn("image.png");

        try (FragmentScenario<AdminViewBookInventoryEditBook> scenario = FragmentScenario.launchInContainer(AdminViewBookInventoryEditBook.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                Bundle bundle = new Bundle();
                bundle.putString("bookName", "Test Book");
                fragment.getParentFragmentManager().setFragmentResult("bookNameInfo", bundle);

                assertEquals("1", ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminViewBookEntryInventoryBookID)).getText().toString());
                assertEquals("Test Book", ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminViewBookEntryInventoryBookName)).getText().toString());
                assertEquals("8", ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminViewBookEntryInventoryBookQuantityAvailable)).getText().toString());
            });
        }
    }

    @Test
    public void testSaveBook_Success() {
        try (FragmentScenario<AdminViewBookInventoryEditBook> scenario = FragmentScenario.launchInContainer(AdminViewBookInventoryEditBook.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminViewBookEntryInventoryBookID)).setText("1");
                ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminViewBookEntryInventoryBookName)).setText("Updated Name");
                ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminViewBookEntryInventoryBookAuthor)).setText("Updated Author");
                ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminViewBookEntryInventoryBookCategory)).setText("Updated Category");
                ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminViewBookEntryInventoryBookSummary)).setText("Updated Summary");
                ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminViewBookEntryInventoryBookQuantityTotal)).setText("20");
                ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminViewBookEntryInventoryBookQuantityBorrowed)).setText("5");

                fragment.requireView().findViewById(R.id.btnAdminViewBookInventoryEditBook).performClick();

                mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(contains("UPDATE BOOKS SET BOOK_NAME = 'Updated Name'")));
                assertEquals("Book information is saved", ShadowToast.getTextOfLatestToast());
                assertEquals("15", ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminViewBookEntryInventoryBookQuantityAvailable)).getText().toString());
            });
        }
    }

    @Test
    public void testDeleteBook_Success() {
        try (FragmentScenario<AdminViewBookInventoryEditBook> scenario = FragmentScenario.launchInContainer(AdminViewBookInventoryEditBook.class, null, R.style.Theme_BookLibraryApp)) {
            NavController mockNavController = mock(NavController.class);
            scenario.onFragment(fragment -> {
                Navigation.setViewNavController(fragment.requireView(), mockNavController);
                ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminViewBookEntryInventoryBookID)).setText("1");
                ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminViewBookEntryInventoryBookQuantityBorrowed)).setText("0");

                fragment.requireView().findViewById(R.id.btnAdminViewBookInventoryDeleteBook).performClick();

                mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(contains("DELETE FROM BOOKS WHERE ID = '1'")));
                assertEquals("Book has been deleted", ShadowToast.getTextOfLatestToast());
                verify(mockNavController).navigate(R.id.action_adminViewBookInventoryEditBook_to_adminViewBookInventory);
            });
        }
    }

    @Test
    public void testDeleteBook_Failure_Borrowed() {
        try (FragmentScenario<AdminViewBookInventoryEditBook> scenario = FragmentScenario.launchInContainer(AdminViewBookInventoryEditBook.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                ((TextInputEditText) fragment.requireView().findViewById(R.id.inputAdminViewBookEntryInventoryBookQuantityBorrowed)).setText("2");
                fragment.requireView().findViewById(R.id.btnAdminViewBookInventoryDeleteBook).performClick();

                assertEquals("Cannot delete book until quantities are returned", ShadowToast.getTextOfLatestToast());
            });
        }
    }
}

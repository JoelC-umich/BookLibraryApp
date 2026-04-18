package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import android.os.Bundle;
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

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class AdminViewBookInventoryEditBookTest {

    // FR7 test admin book edit fields
    @Test
    public void testFragmentUIInitialization() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBookIDFromBookNameQuery(anyString())).thenReturn("1");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBookTitleFromBookID("1")).thenReturn("Test Book");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getAuthorFromBookID("1")).thenReturn("Author");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBookCategoryFromBookID("1")).thenReturn("Category");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBookSummaryFromBookID("1")).thenReturn("Summary");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getQuantityTotalFromBookID("1")).thenReturn("10");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getQuantityBorrowedFromBookID("1")).thenReturn("2");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getQuantityAvailableFromBookID("1")).thenReturn("8");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBookImageFromBookID("1")).thenReturn("image_url");

            try (FragmentScenario<AdminViewBookInventoryEditBook> scenario = FragmentScenario.launchInContainer(AdminViewBookInventoryEditBook.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Bundle result = new Bundle();
                    result.putString("bookName", "Test Book");
                    fragment.getParentFragmentManager().setFragmentResult("bookNameInfo", result);

                    TextInputEditText nameField = fragment.requireView().findViewById(R.id.inputAdminViewBookEntryInventoryBookName);
                    assertNotNull(nameField);
                    assertEquals("Test Book", nameField.getText().toString());
                    
                    Button saveBtn = fragment.requireView().findViewById(R.id.btnAdminViewBookInventoryEditBook);
                    assertNotNull(saveBtn);
                });
            }
        }
    }

    // FR7 negative test admin book edit with blank fields
    @Test
    public void testSaveEditWithBlankFields() {
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBookIDFromBookNameQuery(anyString())).thenReturn("1");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBookTitleFromBookID("1")).thenReturn("Test Book");
            // ... other mocks omitted for brevity if not needed for the start

            try (FragmentScenario<AdminViewBookInventoryEditBook> scenario = FragmentScenario.launchInContainer(AdminViewBookInventoryEditBook.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    TextInputEditText nameField = fragment.requireView().findViewById(R.id.inputAdminViewBookEntryInventoryBookName);
                    Button saveBtn = fragment.requireView().findViewById(R.id.btnAdminViewBookInventoryEditBook);

                    nameField.setText(""); // Blank field

                    saveBtn.performClick();

                    // Verify update query is NEVER run
                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(anyString()), never());
                });
            }
        }
    }

    // FR7 negative test admin delete book with borrowed quantities greater than 1
    @Test
    public void testDeleteBookWithBorrowedQuantities() {
        NavController mockNavController = mock(NavController.class);
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBookIDFromBookNameQuery(anyString())).thenReturn("1");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getQuantityBorrowedFromBookID("1")).thenReturn("5");

            try (FragmentScenario<AdminViewBookInventoryEditBook> scenario = FragmentScenario.launchInContainer(AdminViewBookInventoryEditBook.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);
                    
                    Bundle result = new Bundle();
                    result.putString("bookName", "Test Book");
                    fragment.getParentFragmentManager().setFragmentResult("bookNameInfo", result);

                    Button deleteBtn = fragment.requireView().findViewById(R.id.btnAdminViewBookInventoryDeleteBook);
                    deleteBtn.performClick();

                    // Verify delete query is NEVER run and no navigation occurs
                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(org.mockito.ArgumentMatchers.contains("DELETE FROM BOOKS")), never());
                    verify(mockNavController, never()).navigate(anyInt());
                });
            }
        }
    }

    // FR7 test admin delete book happy path
    @Test
    public void testDeleteBookSuccess() {
        NavController mockNavController = mock(NavController.class);
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(() -> QueryConnectorPlusHelper.getBookIDFromBookNameQuery(anyString())).thenReturn("1");
            mockedHelper.when(() -> QueryConnectorPlusHelper.getQuantityBorrowedFromBookID("1")).thenReturn("0");

            try (FragmentScenario<AdminViewBookInventoryEditBook> scenario = FragmentScenario.launchInContainer(AdminViewBookInventoryEditBook.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);
                    
                    Bundle result = new Bundle();
                    result.putString("bookName", "Test Book");
                    fragment.getParentFragmentManager().setFragmentResult("bookNameInfo", result);

                    Button deleteBtn = fragment.requireView().findViewById(R.id.btnAdminViewBookInventoryDeleteBook);
                    deleteBtn.performClick();

                    // Verify delete query is run and navigation occurs
                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(org.mockito.ArgumentMatchers.contains("DELETE FROM BOOKS")));
                    verify(mockNavController).navigate(R.id.action_adminViewBookInventoryEditBook_to_adminViewBookInventory);
                });
            }
        }
    }
}

package com.example.booklibraryapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

import android.os.Bundle;
import android.widget.Button;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.textfield.TextInputEditText;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.annotation.Config;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class AdminViewBookInventoryEditBookTest {

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
}

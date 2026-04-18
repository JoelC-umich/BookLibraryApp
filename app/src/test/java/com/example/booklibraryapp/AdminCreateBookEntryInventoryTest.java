package com.example.booklibraryapp;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
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

import java.util.ArrayList;
import java.util.Arrays;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 32)
public class AdminCreateBookEntryInventoryTest {

    @Test
    public void testFragmentUIInitialization() {
        try (FragmentScenario<AdminCreateBookEntryInventory> scenario = FragmentScenario.launchInContainer(AdminCreateBookEntryInventory.class, null, R.style.Theme_BookLibraryApp)) {
            scenario.onFragment(fragment -> {
                assertNotNull(fragment.requireView().findViewById(R.id.inputAdminCreateBookName));
                assertNotNull(fragment.requireView().findViewById(R.id.inputAdminCreateBookAuthor));
                assertNotNull(fragment.requireView().findViewById(R.id.btnAdminCreateBook));
            });
        }
    }

    // FR9 Admin test Book creation
    @Test
    public void testCreateBookAttemptSuccess() {
        NavController mockNavController = mock(NavController.class);
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getLastIDPlus1BooksQuery).thenReturn("50");
            mockedHelper.when(QueryConnectorPlusHelper::getBookNamesQuery).thenReturn(new ArrayList<>());

            try (FragmentScenario<AdminCreateBookEntryInventory> scenario = FragmentScenario.launchInContainer(AdminCreateBookEntryInventory.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);
                    
                    TextInputEditText name = fragment.requireView().findViewById(R.id.inputAdminCreateBookName);
                    TextInputEditText author = fragment.requireView().findViewById(R.id.inputAdminCreateBookAuthor);
                    TextInputEditText category = fragment.requireView().findViewById(R.id.inputAdminCreateBookCategory);
                    TextInputEditText summary = fragment.requireView().findViewById(R.id.inputAdminCreateBookSummary);
                    TextInputEditText image = fragment.requireView().findViewById(R.id.inputAdminCreateBookImage);
                    TextInputEditText quantity = fragment.requireView().findViewById(R.id.inputAdminCreateBookQuantity);
                    Button createBtn = fragment.requireView().findViewById(R.id.btnAdminCreateBook);

                    name.setText("New Book");
                    author.setText("New Author");
                    category.setText("Fantasy");
                    summary.setText("Short summary");
                    image.setText("image_url");
                    quantity.setText("5");

                    createBtn.performClick();

                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(org.mockito.ArgumentMatchers.contains("INSERT INTO BOOKS")));
                    verify(mockNavController).navigate(R.id.action_adminCreateBookEntryInventory_to_AdminPage);
                });
            }
        }
    }

    // FR9 Admin test Book creation with blank fields
    @Test
    public void testCreateBookAttemptWithBlankFields() {
        NavController mockNavController = mock(NavController.class);
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getLastIDPlus1BooksQuery).thenReturn("50");
            mockedHelper.when(QueryConnectorPlusHelper::getBookNamesQuery).thenReturn(new ArrayList<>());

            try (FragmentScenario<AdminCreateBookEntryInventory> scenario = FragmentScenario.launchInContainer(AdminCreateBookEntryInventory.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);
                    
                    TextInputEditText name = fragment.requireView().findViewById(R.id.inputAdminCreateBookName);
                    Button createBtn = fragment.requireView().findViewById(R.id.btnAdminCreateBook);

                    name.setText(""); // Leave fields blank

                    createBtn.performClick();

                    // Verify query is NOT run and navigation does NOT occur
                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(anyString()), never());
                    verify(mockNavController, never()).navigate(anyInt());
                });
            }
        }
    }

    // FR9 Admin test Book creation with same name of another book
    @Test
    public void testCreateBookAttemptWithDuplicateName() {
        NavController mockNavController = mock(NavController.class);
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getLastIDPlus1BooksQuery).thenReturn("50");
            mockedHelper.when(QueryConnectorPlusHelper::getBookNamesQuery).thenReturn(Arrays.asList("Existing Book"));

            try (FragmentScenario<AdminCreateBookEntryInventory> scenario = FragmentScenario.launchInContainer(AdminCreateBookEntryInventory.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);
                    
                    TextInputEditText name = fragment.requireView().findViewById(R.id.inputAdminCreateBookName);
                    TextInputEditText author = fragment.requireView().findViewById(R.id.inputAdminCreateBookAuthor);
                    TextInputEditText category = fragment.requireView().findViewById(R.id.inputAdminCreateBookCategory);
                    TextInputEditText summary = fragment.requireView().findViewById(R.id.inputAdminCreateBookSummary);
                    TextInputEditText quantity = fragment.requireView().findViewById(R.id.inputAdminCreateBookQuantity);
                    Button createBtn = fragment.requireView().findViewById(R.id.btnAdminCreateBook);

                    name.setText("Existing Book");
                    author.setText("Author");
                    category.setText("Category");
                    summary.setText("Summary");
                    quantity.setText("1");

                    createBtn.performClick();

                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(anyString()), never());
                    verify(mockNavController, never()).navigate(anyInt());
                });
            }
        }
    }

    // FR9 Admin test Book creation with long summary
    @Test
    public void testCreateBookAttemptWithLongSummary() {
        NavController mockNavController = mock(NavController.class);
        try (MockedStatic<QueryConnectorPlusHelper> mockedHelper = mockStatic(QueryConnectorPlusHelper.class)) {
            mockedHelper.when(QueryConnectorPlusHelper::getLastIDPlus1BooksQuery).thenReturn("50");
            mockedHelper.when(QueryConnectorPlusHelper::getBookNamesQuery).thenReturn(new ArrayList<>());

            try (FragmentScenario<AdminCreateBookEntryInventory> scenario = FragmentScenario.launchInContainer(AdminCreateBookEntryInventory.class, null, R.style.Theme_BookLibraryApp)) {
                scenario.onFragment(fragment -> {
                    Navigation.setViewNavController(fragment.requireView(), mockNavController);
                    
                    TextInputEditText name = fragment.requireView().findViewById(R.id.inputAdminCreateBookName);
                    TextInputEditText author = fragment.requireView().findViewById(R.id.inputAdminCreateBookAuthor);
                    TextInputEditText category = fragment.requireView().findViewById(R.id.inputAdminCreateBookCategory);
                    TextInputEditText summary = fragment.requireView().findViewById(R.id.inputAdminCreateBookSummary);
                    TextInputEditText quantity = fragment.requireView().findViewById(R.id.inputAdminCreateBookQuantity);
                    Button createBtn = fragment.requireView().findViewById(R.id.btnAdminCreateBook);

                    name.setText("Book");
                    author.setText("Author");
                    category.setText("Category");
                    quantity.setText("1");
                    
                    // Summary > 500 chars
                    StringBuilder longSummary = new StringBuilder();
                    for(int i=0; i<501; i++) longSummary.append("a");
                    summary.setText(longSummary.toString());

                    createBtn.performClick();

                    mockedHelper.verify(() -> QueryConnectorPlusHelper.runQuery(anyString()), never());
                    verify(mockNavController, never()).navigate(anyInt());
                });
            }
        }
    }
}

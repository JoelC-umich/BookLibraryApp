package com.example.booklibraryapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.booklibraryapp.databinding.FragmentAdminViewBookInventoryEditBookBinding;
import com.google.android.material.textfield.TextInputEditText;

public class AdminViewBookInventoryEditBook extends Fragment {
    private FragmentAdminViewBookInventoryEditBookBinding binding;
    String bookID, bookName, bookAuthor, bookCategory, bookSummary, bookQuantityTotal, bookQuantityBorrowed, bookQuantityAvailable, bookImage;

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentAdminViewBookInventoryEditBookBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextInputEditText bookInfoID = view.findViewById(R.id.inputAdminViewBookEntryInventoryBookID);
        TextInputEditText bookInfoName = view.findViewById(R.id.inputAdminViewBookEntryInventoryBookName);
        TextInputEditText bookInfoAuthor = view.findViewById(R.id.inputAdminViewBookEntryInventoryBookAuthor);
        TextInputEditText bookInfoCategory = view.findViewById(R.id.inputAdminViewBookEntryInventoryBookCategory);
        TextInputEditText bookInfoSummary = view.findViewById(R.id.inputAdminViewBookEntryInventoryBookSummary);
        TextInputEditText bookInfoQuantityTotal = view.findViewById(R.id.inputAdminViewBookEntryInventoryBookQuantityTotal);
        TextInputEditText bookInfoQuantityBorrowed = view.findViewById(R.id.inputAdminViewBookEntryInventoryBookQuantityBorrowed);
        TextInputEditText bookInfoQuantityAvailable = view.findViewById(R.id.inputAdminViewBookEntryInventoryBookQuantityAvailable);
        TextInputEditText bookInfoImage = view.findViewById(R.id.inputAdminViewBookEntryInventoryBookImage);

        getParentFragmentManager().setFragmentResultListener("bookNameInfo", this, (requestKey, bundle) ->
        {
            String selectedBookName = bundle.getString("bookName");
            String bookID = QueryConnectorPlusHelper.getBookIDFromBookNameQuery(selectedBookName);
            String bookName = QueryConnectorPlusHelper.getBookTitleFromBookID(bookID);
            String bookAuthor = QueryConnectorPlusHelper.getAuthorFromBookID(bookID);
            String bookCategory = QueryConnectorPlusHelper.getBookCategoryFromBookID(bookID);
            String bookSummary = QueryConnectorPlusHelper.getBookSummaryFromBookID(bookID);
            String bookQtyTotal = QueryConnectorPlusHelper.getQuantityTotalFromBookID(bookID);
            String bookQtyBorrowed = QueryConnectorPlusHelper.getQuantityBorrowedFromBookID(bookID);
            String bookQtyAvailable = QueryConnectorPlusHelper.getQuantityAvailableFromBookID(bookID);
            String bookImage = QueryConnectorPlusHelper.getBookImageFromBookID(bookID);
            bookInfoID.setText(bookID);
            bookInfoName.setText(bookName);
            bookInfoAuthor.setText(bookAuthor);
            bookInfoCategory.setText(bookCategory);
            bookInfoSummary.setText(bookSummary);
            bookInfoQuantityTotal.setText(bookQtyTotal);
            bookInfoQuantityBorrowed.setText(bookQtyBorrowed);
            bookInfoQuantityAvailable.setText(bookQtyAvailable);
            bookInfoImage.setText(bookImage);
        });

        binding.btnAdminViewBookInventoryEditBook.setOnClickListener(v ->
        {
            if (bookInfoID.getText().toString().isBlank() || bookInfoName.getText().toString().isBlank() || bookInfoAuthor.getText().toString().isBlank()
                    || bookInfoCategory.getText().toString().isBlank() || bookInfoSummary.getText().toString().isBlank() || bookInfoQuantityTotal.getText().toString().isBlank())
            {
                Toast.makeText(getContext(), "Error saving information\nOne of the fields is blank or missing", Toast.LENGTH_SHORT).show();
            }
            else
            {
                bookID = bookInfoID.getText().toString();
                bookName = bookInfoName.getText().toString();
                bookAuthor = bookInfoAuthor.getText().toString();
                bookCategory = bookInfoCategory.getText().toString();
                bookSummary = bookInfoSummary.getText().toString();
                bookQuantityTotal = bookInfoQuantityTotal.getText().toString();
                bookQuantityBorrowed = bookInfoQuantityBorrowed.getText().toString(); //CREATE A CLASS THAT COUNTS BOOKS CHECKED OUT WITH THE BOOK ID
                bookQuantityAvailable = String.valueOf(((Integer.parseInt(bookInfoQuantityTotal.getText().toString())) - (Integer.parseInt(bookInfoQuantityBorrowed.getText().toString()))));
                bookImage = bookInfoImage.getText().toString();
                QueryConnectorPlusHelper.runQuery("UPDATE BOOKS SET BOOK_NAME = '"+bookName+"', BOOK_AUTHOR = '"+bookAuthor+"', CATEGORY = '"+bookCategory+"', SUMMARY = '"+bookSummary+"', QUANTITY_TOTAL = '"+bookQuantityTotal+"', QUANTITY_BORROWED = '"+bookQuantityBorrowed+"', QUANTITY_AVAILABLE = '"+bookQuantityAvailable+"', IMAGE = '"+bookImage+"' WHERE ID = '"+bookID+"'");
                Toast.makeText(getContext(),"Book information is saved", Toast.LENGTH_SHORT).show();
                bookInfoQuantityAvailable.setText(bookQuantityAvailable);
            }
        });

        binding.btnAdminViewBookInventoryDeleteBook.setOnClickListener(v -> {
            bookID = bookInfoID.getText().toString();
            bookName = bookInfoName.getText().toString();
            if (Integer.parseInt(bookInfoQuantityBorrowed.getText().toString()) > 0)
            {
                Toast.makeText(getContext(), "Cannot delete book until quantities are returned", Toast.LENGTH_SHORT).show();
            }
            else
            {
                QueryConnectorPlusHelper.runQuery("DELETE FROM BOOKS WHERE ID = '"+bookID+"'");
                Toast.makeText(getContext(),"Book has been deleted", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(AdminViewBookInventoryEditBook.this).navigate(R.id.action_adminViewBookInventoryEditBook_to_adminViewBookInventory);
                bookInfoID.getText().clear();
                bookInfoName.getText().clear();
                bookInfoAuthor.getText().clear();
                bookInfoCategory.getText().clear();
                bookInfoSummary.getText().clear();
                bookInfoQuantityTotal.getText().clear();
                bookInfoQuantityBorrowed.getText().clear();
                bookInfoQuantityAvailable.getText().clear();
                bookInfoImage.getText().clear();
            }
        });
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        binding = null;
    }
}
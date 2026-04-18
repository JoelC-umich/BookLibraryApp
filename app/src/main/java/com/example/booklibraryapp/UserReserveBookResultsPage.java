package com.example.booklibraryapp;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.booklibraryapp.databinding.FragmentUserReserveBookResultsPageBinding;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserReserveBookResultsPage extends Fragment {
    private FragmentUserReserveBookResultsPageBinding binding;
    Button btnReserveBook;
    String availableBookName, availableBookID, author, category, summary, image;
    String loggedInUserID = QueryConnectorPlusHelper.IDWhenLoggingIn;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentUserReserveBookResultsPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // FR 4 User book selection for reservation/request
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        TextView textBookNameOutput = view.findViewById(R.id.textBookNameOutput);
        TextView textBookAuthorOutput = view.findViewById(R.id.textBookAuthorOutput);
        TextView textBookCategoryOutput = view.findViewById(R.id.textBookCategoryOutput);
        TextView textBookSummaryOutput = view.findViewById(R.id.textBookSummaryOutput);
        ImageView imageBookImageOutput = view.findViewById(R.id.imageBookImageOutput);
        btnReserveBook = view.findViewById(R.id.btnReserveBook);

        getParentFragmentManager().setFragmentResultListener("availableBookInfo", this, (requestKey, bundle) -> {
            availableBookName = bundle.getString("availableBook");
            availableBookID = QueryConnectorPlusHelper.getBookIDFromBookNameQuery(availableBookName);
            author = QueryConnectorPlusHelper.getAuthorFromBookID(availableBookID);
            category = QueryConnectorPlusHelper.getBookCategoryFromBookID(availableBookID);
            summary = QueryConnectorPlusHelper.getBookSummaryFromBookID(availableBookID);
            image = QueryConnectorPlusHelper.getBookImageFromBookID(availableBookID);
            
            textBookNameOutput.setText(availableBookName);
            textBookAuthorOutput.setText(author);
            textBookCategoryOutput.setText(category);
            textBookSummaryOutput.setText(summary);
            Picasso.get().load(image).into(imageBookImageOutput);

            binding.btnReserveBook.setOnClickListener(v -> {
                List<String> reservedBooksFromUser = QueryConnectorPlusHelper.getReservedBooksFromUserID(loggedInUserID);
                if (reservedBooksFromUser.contains(availableBookID)) {
                    Toast.makeText(getContext(), "Error reserving book\nYou already have this book reserved or pending", Toast.LENGTH_SHORT).show();
                } else {
                    String maxIDBookBorrowed = QueryConnectorPlusHelper.getLastIDPlus1BooksBorrowedQuery();
                    String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    String quantityAvailableMinus1 = QueryConnectorPlusHelper.getQuantityAvailableMinus1FromBookID(availableBookID);
                    String quantityBorrowedPlus1 = QueryConnectorPlusHelper.getQuantityBorrowedPlus1FromBookID(availableBookID);
                    
                    // Fixed: Added quotes around todayDate for SQL
                    QueryConnectorPlusHelper.runQuery("INSERT INTO BOOKS_BORROWED VALUES ("+maxIDBookBorrowed+", "+availableBookID+", "+loggedInUserID+", '"+todayDate+"', 'Pending')");
                    QueryConnectorPlusHelper.runQuery("UPDATE BOOKS SET QUANTITY_AVAILABLE = '"+quantityAvailableMinus1+"' WHERE ID = '"+availableBookID+"'");
                    QueryConnectorPlusHelper.runQuery("UPDATE BOOKS SET QUANTITY_BORROWED = '"+quantityBorrowedPlus1+"' WHERE ID = '"+availableBookID+"'");
                    
                    Toast.makeText(getContext(), availableBookName + " request to reserve has been made\nPlease wait for the librarian to approve your request", Toast.LENGTH_SHORT).show();
                    
                    // Navigate back to UserPage and clear the search/results fragments from the stack
                    NavHostFragment.findNavController(this).popBackStack(R.id.userPage, false);
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

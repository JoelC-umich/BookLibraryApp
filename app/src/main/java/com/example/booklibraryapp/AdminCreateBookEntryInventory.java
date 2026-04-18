package com.example.booklibraryapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.booklibraryapp.databinding.FragmentAdminCreateBookEntryInventoryBinding;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class AdminCreateBookEntryInventory extends Fragment {

    private FragmentAdminCreateBookEntryInventoryBinding binding;
    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentAdminCreateBookEntryInventoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        TextInputEditText inputAdminCreateBookName = view.findViewById(R.id.inputAdminCreateBookName);
        TextInputEditText inputAdminCreateBookAuthor = view.findViewById(R.id.inputAdminCreateBookAuthor);
        TextInputEditText inputAdminCreateBookCategory = view.findViewById(R.id.inputAdminCreateBookCategory);
        TextInputEditText inputAdminCreateBookSummary = view.findViewById(R.id.inputAdminCreateBookSummary);
        TextInputEditText inputAdminCreateBookImage = view.findViewById(R.id.inputAdminCreateBookImage);
        TextInputEditText inputAdminCreateBookQuantity = view.findViewById(R.id.inputAdminCreateBookQuantity);

        binding.btnAdminCreateBook.setOnClickListener(v -> {
            String ID = QueryConnectorPlusHelper.getLastIDPlus1BooksQuery();
            String bookName = inputAdminCreateBookName.getText().toString();
            String author = inputAdminCreateBookAuthor.getText().toString();
            String category = inputAdminCreateBookCategory.getText().toString();
            String summary = inputAdminCreateBookSummary.getText().toString();
            String image = inputAdminCreateBookImage.getText().toString();
            String quantity = inputAdminCreateBookQuantity.getText().toString();

            boolean exists = false;
            List<String> existingNames = QueryConnectorPlusHelper.getBookNamesQuery();
            if (existingNames != null) {
                for (String name : existingNames) {
                    if (name != null && name.equalsIgnoreCase(bookName)) {
                        exists = true;
                        break;
                    }
                }
            }

            if(bookName.isBlank() || author.isBlank() || category.isBlank() || summary.isBlank() || quantity.isBlank())
            {
                Toast.makeText(getContext(), "One of the fields are blank\nPlease fill and try again", Toast.LENGTH_SHORT).show();
            } else if (exists)
            {
                Toast.makeText(getContext(), "Book name already exists\nPlease try again", Toast.LENGTH_SHORT).show();
            } else if (summary.length() > 500)
            {
                Toast.makeText(getContext(), "Summary is too long\nPlease shorten and try again", Toast.LENGTH_SHORT).show();
            }
            else
            {
                if (ID == null)
                {
                    QueryConnectorPlusHelper.runQuery("INSERT INTO BOOKS VALUES (0, '"+bookName+"', '"+author+"', '"+category+"', '"+summary+"', '"+quantity+"', '0', '"+quantity+"', '"+image+"')");
                }
                else
                {
                    QueryConnectorPlusHelper.runQuery("INSERT INTO BOOKS VALUES ('"+ID+"', '"+bookName+"', '"+author+"', '"+category+"', '"+summary+"', '"+quantity+"', '0', '"+quantity+"', '"+image+"')");
                }
                Toast.makeText(getContext(), "Book entry successfully added to inventory", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(AdminCreateBookEntryInventory.this).navigate(R.id.action_adminCreateBookEntryInventory_to_AdminPage);
            }
        });
    }
}
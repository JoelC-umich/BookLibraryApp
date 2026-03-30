package com.example.booklibraryapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.booklibraryapp.databinding.AdminPageBinding;

public class AdminPage extends Fragment {

    private AdminPageBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = AdminPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textAdminWelcomeText = view.findViewById(R.id.textAdminWelcomeText);
        getParentFragmentManager().setFragmentResultListener("userInfo", this, (requestKey, bundle) -> {
            String loggedInUserID = bundle.getString("User_ID");
            String firstUserName = QueryConnectorPlusHelper.getFirstNameFromIDQuery(loggedInUserID);
            textAdminWelcomeText.setText("Welcome Librarian "+firstUserName+"!");
        });

        binding.btnAdminViewAccounts.setOnClickListener(v ->
                NavHostFragment.findNavController(AdminPage.this).navigate(R.id.action_AdminPage_to_adminAccountsInfoPage)
        );

        binding.btnAdminViewBorrowedBooks.setOnClickListener(v ->
                NavHostFragment.findNavController(AdminPage.this).navigate(R.id.action_AdminPage_to_adminViewBorrowedBooksPage)
        );

        binding.btnAdminViewRoomReservations.setOnClickListener(v ->
                NavHostFragment.findNavController(AdminPage.this).navigate(R.id.action_AdminPage_to_adminViewRoomReservationsPage)
        );

        binding.btnAdminViewRoomRequests.setOnClickListener(v ->
                NavHostFragment.findNavController(AdminPage.this).navigate(R.id.action_AdminPage_to_adminViewRoomRequestsPage)
        );

        binding.btnAdminViewBookRequests.setOnClickListener(v ->
                NavHostFragment.findNavController(AdminPage.this).navigate(R.id.action_AdminPage_to_adminViewBookRequestsPage)
        );

        binding.btnAdminLogout.setOnClickListener(v ->
                NavHostFragment.findNavController(AdminPage.this).navigate(R.id.action_AdminPage_to_LoginPage)
        );

        binding.btnAdminViewCreateAccount.setOnClickListener(v ->
                NavHostFragment.findNavController(AdminPage.this).navigate(R.id.action_AdminPage_to_createUserPageAdmin)
        );

        binding.btnAdminViewBooksInventory.setOnClickListener(v ->
                NavHostFragment.findNavController(AdminPage.this).navigate(R.id.action_AdminPage_to_adminViewBookInventory)
        );

        binding.btnAdminCreateBookEntryInventory.setOnClickListener(v ->
                NavHostFragment.findNavController(AdminPage.this).navigate(R.id.action_AdminPage_to_adminCreateBookEntryInventory)
        );

        binding.btnAdminViewRooms.setOnClickListener(v ->
                NavHostFragment.findNavController(AdminPage.this).navigate(R.id.action_AdminPage_to_adminViewRooms)
        );

        binding.btnAdminCreateRoom.setOnClickListener(v ->
        {
            final EditText userInput = new EditText(getContext());
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            dialogBuilder.setTitle("Create Room");
            dialogBuilder.setMessage("Enter a Room Number");
            dialogBuilder.setView(userInput);

            dialogBuilder.setPositiveButton("OK", (dialog, which) ->
            {
                String roomEntered = userInput.getText().toString();
                if(roomEntered.isBlank())
                {
                    Toast.makeText(getContext(), "Please enter a number", Toast.LENGTH_LONG).show();
                }
                else if (QueryConnectorPlusHelper.getRoomIDsQuery().contains(roomEntered))
                {
                    Toast.makeText(getContext(), "Room number already exists\nPlease try again", Toast.LENGTH_LONG).show();
                }
                else if(Integer.parseInt(roomEntered) > 1000000) //random number set
                {
                    Toast.makeText(getContext(), "Room number exceeds allowed value\nPlease try again", Toast.LENGTH_LONG).show();
                }
                else
                {
                    QueryConnectorPlusHelper.runQuery("INSERT INTO ROOMS VALUES ('"+roomEntered+"')");
                    Toast.makeText(getContext(), "Room has been successfully added", Toast.LENGTH_LONG).show();
                }
            });

            dialogBuilder.setNeutralButton("Cancel", (dialog, which) ->
            {
                dialog.dismiss();
            });

            AlertDialog createRoomDialog = dialogBuilder.create();
            createRoomDialog.show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
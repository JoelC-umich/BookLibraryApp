package com.example.booklibraryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.booklibraryapp.databinding.LoginPageBinding;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginPage extends Fragment {
    DatabaseConnectorClass DBConnector;
    java.sql.Connection connection;
    private LoginPageBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {

        binding = LoginPageBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnLogin.setOnClickListener(v -> {
            // Put your code here:
            // For example:
            // Toast.makeText(requireContext(), "Button clicked!", Toast.LENGTH_SHORT).show();

            // If you still want to navigate:
            NavHostFragment.findNavController(LoginPage.this)
                    .navigate(R.id.action_LoginPage_to_userPage);
        });

        binding.btnLoginCreateAccount.setOnClickListener(v -> {
            // Put your code here:
            // For example:
            // Toast.makeText(requireContext(), "Button clicked!", Toast.LENGTH_SHORT).show();

            // If you still want to navigate:
            NavHostFragment.findNavController(LoginPage.this)
                    .navigate(R.id.action_LoginPage_to_createUserPage);
        });

//        binding.buttonShowTable.setOnClickListener(v ->{
//            ExecutorService executorService = Executors.newSingleThreadExecutor();
//            executorService.execute(() -> {
//                try {
//                    DBConnector = new DatabaseConnectorClass();
//                    connection = DBConnector.Connector();
//
//                    String query = "SELECT * FROM USERS";
//                    PreparedStatement statement = connection.prepareStatement(query);
//                    ResultSet rset = statement.executeQuery();
//                    StringBuilder stringbldr = new StringBuilder();
//                    while (rset.next()) {
//                        stringbldr.append(rset.getString("FIRST_NAME")).append("\n");
//                    }
//
//                    String result = stringbldr.toString();
//                    // Update UI on main thread
//                    requireActivity().runOnUiThread(() -> {
//                        //binding.textviewTable.setText(result);
//                    });
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
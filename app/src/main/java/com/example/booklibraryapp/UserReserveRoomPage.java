package com.example.booklibraryapp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserReserveRoomPage extends Fragment {

    // Default selected date is today so the user doesn't have to tap before searching
    private String selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(new Date());

    public UserReserveRoomPage() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_reserve_room_page, container, false);

        CalendarView calendarView = view.findViewById(R.id.calendarView);
        Button buttonSearch = view.findViewById(R.id.buttonSearchDate);

        calendarView.setOnDateChangeListener((calView, year, month, dayOfMonth) -> {
            // month is 0-indexed so add 1
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    year, month + 1, dayOfMonth);
        });

        buttonSearch.setOnClickListener(v -> {
            // Pass selectedDate to the results page via Bundle
            Bundle bundle = new Bundle();
            bundle.putString("selectedDate", selectedDate);
            Navigation.findNavController(view)
                    .navigate(R.id.action_userReserveRoomPage_to_userReserveRoomResultsPage, bundle);
        });

        return view;
    }

}
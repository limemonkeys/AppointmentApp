package com.soursimianstudios.dalplexapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;

public class FilterTimeMenu extends AppCompatActivity {
    int tableWidth, newHeight;
    SharedPreferences timePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        // Initialization of images at the top of application.
        ImageView filterMenu = (ImageView) findViewById(R.id.filterMenu);
        filterMenu.setColorFilter(Color.GRAY);

        ImageView refreshButton = (ImageView) findViewById(R.id.refreshButton);
        refreshButton.setColorFilter(Color.GRAY);

        ImageView menuButton = findViewById(R.id.menuButton);
        TableLayout table = (TableLayout) findViewById(R.id.AppointmentsTable);
        tableWidth = table.getLayoutParams().width;

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterTimeMenu.this, LandingPage.class);
                startActivity(intent);
            }
        });

        ImageView helpButton = (ImageView) findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterTimeMenu.this, HelpMenu.class);
                startActivity(intent);
            }
        });

        // Set the times for row creation
        ArrayList<String> timeslots = new ArrayList<>();

        /*
        All time intervals (but who knows, Dal loves to switch this stuff up...)
        As of commit, airing on the side of over-listing, than under. Potential for slider, but
        slider does not work well with complicated schedules. Therefore, stuck with this list.

        As of Sept 6th, 2021
        Earliest seen opening: 6:00 AM - 7:00 AM
        Latest seen opening: 9:00 PM - 10:00 PM
        */
        timeslots.add("6:00 AM - 7:00 AM");
        timeslots.add("6:30 AM - 7:30 AM");
        timeslots.add("7:00 AM - 8:00 AM");
        timeslots.add("7:30 AM - 8:30 AM");
        timeslots.add("8:00 AM - 9:00 AM");
        timeslots.add("8:30 AM - 9:30 AM");
        timeslots.add("9:00 AM - 10:00 AM");
        timeslots.add("9:30 AM - 10:30 AM");
        timeslots.add("10:00 AM - 11:00 AM");
        timeslots.add("10:30 AM - 11:30 AM");
        timeslots.add("11:00 AM - 12:00 PM");
        timeslots.add("11:30 AM - 12:30 PM");
        timeslots.add("12:00 PM - 1:00 PM");
        timeslots.add("12:30 PM - 1:30 PM");
        timeslots.add("1:00 PM - 2:00 PM");
        timeslots.add("1:30 PM - 2:30 PM");
        timeslots.add("2:00 PM - 3:00 PM");
        timeslots.add("2:30 PM - 3:30 PM");
        timeslots.add("3:00 PM - 4:00 PM");
        timeslots.add("3:30 PM - 4:30 PM");
        timeslots.add("4:00 PM - 5:00 PM");
        timeslots.add("4:30 PM - 5:30 PM");
        timeslots.add("5:00 PM - 6:00 PM");
        timeslots.add("5:30 PM - 6:30 PM");
        timeslots.add("6:00 PM - 7:00 PM");
        timeslots.add("6:30 PM - 7:30 PM");
        timeslots.add("7:00 PM - 8:00 PM");
        timeslots.add("7:30 PM - 8:30 PM");
        timeslots.add("8:00 PM - 9:00 PM");
        timeslots.add("8:30 PM - 9:30 PM");
        timeslots.add("9:00 PM - 10:00 PM");


        for(String timeslot_name : timeslots){
            addRow(timeslot_name);
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int newHeight = Math.max(((150 + 45) * timeslots.size()) + 90, displayMetrics.heightPixels * 3/4);
        table.setLayoutParams(new TableLayout.LayoutParams(tableWidth, newHeight));
    }

    // Add rows with switches for each of the days
    public void addRow(String time){
        TableLayout table = (TableLayout) findViewById(R.id.AppointmentsTable);

        int width = table.getLayoutParams().width;

        TableRow row = new TableRow(this);

        row.setPadding(15,45,15,0);
        table.setLayoutParams(new TableLayout.LayoutParams(table.getLayoutParams().width, newHeight));

        TextView filterText = new TextView(this);
        Switch filterSwitch = new Switch(this);

        filterText.setTextColor(Color.parseColor("#000000"));
        filterSwitch.setTextColor(Color.parseColor("#000000"));

        Typeface typeface = ResourcesCompat.getFont(this, R.font.fugaz_one);
        filterText.setTypeface(typeface);

        filterText.setBackgroundColor(Color.parseColor("#F2F197"));
        filterSwitch.setBackgroundColor(Color.parseColor("#F2F197"));

        filterText.setGravity(Gravity.CENTER);
        filterSwitch.setGravity(Gravity.CENTER);

        filterText.setTextSize(16);

        filterText.setMaxWidth(width/2);
        filterSwitch.setMaxWidth(width/2);

        filterText.setMinWidth(width/2);
        filterSwitch.setMinWidth(width/2);

        filterSwitch.setPadding(0,0,tableWidth/6,0);

        filterText.setMaxHeight(150);
        filterSwitch.setMaxHeight(150);

        filterText.setMinHeight(150);
        filterSwitch.setMinHeight(150);

        filterText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        filterSwitch.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

        filterText.setText(time);
        filterSwitch.setChecked(false);

        timePreferences = getSharedPreferences("timePreferences", MODE_PRIVATE);
        //Note: Boolean.getValue(...) doesn't work
        filterSwitch.setChecked(timePreferences.getString(time, String.valueOf(true)).equals("true"));

        // When switch is flipped, commit to SharedPreferences for reference.
        filterSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePreferences = getSharedPreferences("timePreferences", 0);
                SharedPreferences.Editor editPreference = timePreferences.edit();
                // apply() does not produce desired results
                editPreference.putString(time, String.valueOf(filterSwitch.isChecked())).commit();
            }
        });

        row.addView(filterText);
        row.addView(filterSwitch);

        table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

    }
}

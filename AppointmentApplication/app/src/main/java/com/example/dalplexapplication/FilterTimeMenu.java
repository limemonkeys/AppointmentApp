package com.example.dalplexapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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

        ImageView menuButton = findViewById(R.id.menuButton);
        tableWidth = findViewById(R.id.AppointmentsTable).getLayoutParams().width;
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterTimeMenu.this, LandingPage.class);
                startActivity(intent);
            }
        });

        ImageView filterMenu = (ImageView) findViewById(R.id.filterMenu);
        filterMenu.setColorFilter(Color.GRAY);

        ImageView settingsButton = (ImageView) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterTimeMenu.this, SettingsMenu.class);
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

        TableLayout table = (TableLayout) findViewById(R.id.AppointmentsTable);
        int height = table.getLayoutParams().height;
        int width = table.getLayoutParams().width;

        //Currently hardcoded as these time intervals shouldn't change
        int numRows = 19;
        newHeight = Math.max(((150 + 45) * numRows) + 45, height);
        System.out.println(newHeight);

        timePreferences = getSharedPreferences("timePreferences", MODE_PRIVATE);

        ArrayList<String> timeslots = new ArrayList<>();

        // All other day's intervals
        timeslots.add("6:00 AM - 7:00 AM");
        timeslots.add("7:30 AM - 8:30 AM");
        timeslots.add("9:00 AM - 10:00 AM");
        timeslots.add("10:30 AM - 11:30 AM");
        timeslots.add("12:00 PM - 1:00 PM");
        timeslots.add("1:30 PM - 2:30 PM");
        timeslots.add("3:00 PM - 4:00 PM");
        timeslots.add("4:30 PM - 5:30 PM");
        timeslots.add("6:00 PM - 7:00 PM");
        timeslots.add("7:30 PM - 8:30 PM");

        // Specifically Saturday's times intervals
        timeslots.add("7:00 AM - 8:00 AM");
        timeslots.add("8:30 AM - 9:30 AM");
        timeslots.add("10:00 AM - 11:00 AM");
        timeslots.add("11:30 AM - 12:30 PM");
        timeslots.add("1:00 PM - 2:00 PM");
        timeslots.add("2:30 PM - 3:30 PM");
        timeslots.add("4:00 PM - 5:00 PM");;

        for(String timeslot_name : timeslots){
            addRow(timeslot_name);
        }
    }

    public void addRow(String time){
        TableLayout table = (TableLayout) findViewById(R.id.AppointmentsTable);

        int height = table.getLayoutParams().height;
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

        filterText.setTextSize(18);

        filterText.setMaxWidth(width/2);
        filterSwitch.setMaxWidth(width/2);

        filterText.setMinWidth(width/2);
        filterSwitch.setMinWidth(width/2);

        filterSwitch.setPadding(0,0,tableWidth/6,0);
        System.out.println("WIDTH: " + String.valueOf(width));

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
        filterSwitch.setChecked(timePreferences.getString(time, String.valueOf(false)).equals("true"));

        filterSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePreferences = getSharedPreferences("timePreferences", 0);
                SharedPreferences.Editor editPreference = timePreferences.edit();
                editPreference.putString(time, String.valueOf(filterSwitch.isChecked())).commit();
            }
        });

        row.addView(filterText);
        row.addView(filterSwitch);

        table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

    }
}

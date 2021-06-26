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

public class FilterDayMenu extends AppCompatActivity {

    int tableWidth, newHeight;

    SharedPreferences dayPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        ImageView refreshButton = (ImageView) findViewById(R.id.refreshButton);
        refreshButton.setColorFilter(Color.GRAY);

        ImageView filterMenu = (ImageView) findViewById(R.id.filterMenu);
        filterMenu.setColorFilter(Color.GRAY);

        tableWidth = findViewById(R.id.AppointmentsTable).getLayoutParams().width;

        ImageView menuButton = (ImageView) findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterDayMenu.this, LandingPage.class);
                startActivity(intent);
            }
        });

        ImageView helpButton = (ImageView) findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterDayMenu.this, HelpMenu.class);
                startActivity(intent);
            }
        });

        TableLayout table = (TableLayout) findViewById(R.id.AppointmentsTable);
        int height = table.getLayoutParams().height;
        int width = table.getLayoutParams().width;

        //Hardcoded as there should always be 7 days in a week
        int numRows = 7;
        newHeight = Math.max(((150 + 45) * numRows) + 45, height);

        dayPreferences = getSharedPreferences("dayPreferences", MODE_PRIVATE);

        ArrayList<String> days = new ArrayList<>();

        days.add("Monday");
        days.add("Tuesday");
        days.add("Wednesday");
        days.add("Thursday");
        days.add("Friday");
        days.add("Saturday");
        days.add("Sunday");


        for(String day_name : days){
            addRow(day_name);
        }

    }


    // TODO: dayPreference ends up being fixed when row is initialized. Make an initial call to set the switch, then make every other reference a getString call within onClick
    public void addRow(String day){




        TableLayout table = (TableLayout) findViewById(R.id.AppointmentsTable);
        int height = table.getLayoutParams().height;
        int width = table.getLayoutParams().width;



        TableRow row = new TableRow(this);
        row.setPadding(15,45,15,0);

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

        filterText.setTextSize(24);

        filterText.setMaxWidth(width/2);
        filterSwitch.setMaxWidth(width/2);

        filterText.setMinWidth(width/2);
        filterSwitch.setMinWidth(width/2);

        filterSwitch.setPadding(0,0,width/6,0);

        filterText.setMaxHeight(150);
        filterSwitch.setMaxHeight(150);

        filterText.setMinHeight(150);
        filterSwitch.setMinHeight(150);


        filterText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        filterSwitch.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

        filterText.setText(day);

        dayPreferences = getSharedPreferences("dayPreferences", MODE_PRIVATE);
        //Boolean.getValue(...) doesn't work
        filterSwitch.setChecked(dayPreferences.getString(day, String.valueOf(false)).equals("true"));


        filterSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println("-------------------");
                dayPreferences = getSharedPreferences("dayPreferences", 0);
                //System.out.println("filterSwitch.isChecked(): " + filterSwitch.isChecked());
                //System.out.println("new get: " + dayPreferences.getString(day, String.valueOf(false)));
                SharedPreferences.Editor editPreference = dayPreferences.edit();
                System.out.println(day);
                System.out.println(String.valueOf(filterSwitch.isChecked()));
                //Apply does not produce desired results
                editPreference.putString(day, String.valueOf(filterSwitch.isChecked())).commit();
                //dayPreferences = getSharedPreferences("dayPreferences", 0);
                //System.out.println("filterSwitch.isChecked(): " + filterSwitch.isChecked());
                //System.out.println("new get: " + dayPreferences.getString(day, String.valueOf(false)));

            }
        });

        row.addView(filterText);
        row.addView(filterSwitch);

        table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }
}

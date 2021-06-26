package com.example.dalplexapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class FilterMenu extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        ImageView menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterMenu.this, LandingPage.class);
                startActivity(intent);
            }
        });

        ImageView filterMenu = findViewById(R.id.filterMenu);
        filterMenu.setColorFilter(Color.GRAY);

        ImageView settingsButton = (ImageView) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterMenu.this, SettingsMenu.class);
                startActivity(intent);
            }
        });

        ImageView helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterMenu.this, HelpMenu.class);
                startActivity(intent);
            }
        });


        TableLayout table = (TableLayout) findViewById(R.id.AppointmentsTable);
        addRow("Filter by Days", "FilterDayMenu");
        addRow("Filter by Time", "FilterTimeMenu");
    }

    public void addRow(String text, String FilterType){
        TableLayout table = (TableLayout) findViewById(R.id.AppointmentsTable);
        int width = table.getLayoutParams().width;

        TableRow row = new TableRow(this);

        row.setPadding(15,45,15,0);

        TextView filterText = new TextView(this);

        filterText.setTextColor(Color.parseColor("#000000"));

        Typeface typeface = ResourcesCompat.getFont(this, R.font.fugaz_one);
        filterText.setTypeface(typeface);

        filterText.setBackgroundColor(Color.parseColor("#F2F197"));

        filterText.setGravity(Gravity.CENTER);

        filterText.setTextSize(24);

        filterText.setMaxWidth(width);

        filterText.setMinWidth(width);

        filterText.setMaxHeight(150);

        filterText.setMinHeight(150);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterMenu.this, LandingPage.class);
                if (FilterType.equals("FilterDayMenu")){
                    intent = new Intent(FilterMenu.this, FilterDayMenu.class);

                }
                else if (FilterType.equals("FilterTimeMenu")){
                    intent = new Intent(FilterMenu.this, FilterTimeMenu.class);
                }
                startActivity(intent);
            }
        });

        filterText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

        filterText.setText(text);


        row.addView(filterText);

        table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }
}

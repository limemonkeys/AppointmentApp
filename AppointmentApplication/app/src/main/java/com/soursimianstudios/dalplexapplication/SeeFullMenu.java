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

public class SeeFullMenu extends AppCompatActivity {

    int tableWidth, newHeight;
    SharedPreferences seeFullPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        // Initialization of images at the top of application.
        ImageView refreshButton = (ImageView) findViewById(R.id.refreshButton);
        refreshButton.setColorFilter(Color.GRAY);

        ImageView filterMenu = (ImageView) findViewById(R.id.filterMenu);
        filterMenu.setColorFilter(Color.GRAY);

        TableLayout table = (TableLayout) findViewById(R.id.AppointmentsTable);
        tableWidth = table.getLayoutParams().width;

        ImageView menuButton = (ImageView) findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeeFullMenu.this, LandingPage.class);
                startActivity(intent);
            }
        });

        ImageView helpButton = (ImageView) findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeeFullMenu.this, HelpMenu.class);
                startActivity(intent);
            }
        });

        // Set the days for row creation
        seeFullPreferences = getSharedPreferences("seeFullPreferences", MODE_PRIVATE);

        addRow("See Full Appointments");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int newHeight = Math.max((150 + 45) + 45, displayMetrics.heightPixels * 3/4);
        table.setLayoutParams(new TableLayout.LayoutParams(tableWidth, newHeight));
    }

    // Add rows with switches for each of the days
    public void addRow(String enabled){
        TableLayout table = (TableLayout) findViewById(R.id.AppointmentsTable);
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

        filterText.setTextSize(18);

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

        filterText.setText(enabled);

        seeFullPreferences = getSharedPreferences("seeFullPreferences", MODE_PRIVATE);
        // Boolean.getValue(...) doesn't work
        filterSwitch.setChecked(seeFullPreferences.getString(enabled, String.valueOf(true)).equals("true"));

        // When switch is flipped, commit to SharedPreferences for reference.
        filterSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seeFullPreferences = getSharedPreferences("seeFullPreferences", 0);
                SharedPreferences.Editor editPreference = seeFullPreferences.edit();
                // apply() does not produce desired results
                editPreference.putString("See Full Appointments", String.valueOf(filterSwitch.isChecked())).commit();
            }
        });

        row.addView(filterText);
        row.addView(filterSwitch);

        table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }
}

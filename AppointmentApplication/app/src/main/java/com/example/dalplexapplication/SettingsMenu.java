package com.example.dalplexapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageView menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsMenu.this, LandingPage.class);
                startActivity(intent);
            }
        });

        ImageView filterMenu =findViewById(R.id.filterMenu);
        filterMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsMenu.this, FilterMenu.class);
                startActivity(intent);
            }
        });

        ImageView settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setColorFilter(Color.GRAY);

        ImageView helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsMenu.this, HelpMenu.class);
                startActivity(intent);
            }
        });
    }
}

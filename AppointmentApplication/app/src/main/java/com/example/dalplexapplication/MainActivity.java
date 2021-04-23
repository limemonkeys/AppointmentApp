package com.example.dalplexapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

public class MainActivity extends AppCompatActivity {

    int rowCounter = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TableLayout table = (TableLayout) findViewById(R.id.AppointmentsTable);
        int height = table.getLayoutParams().height;
        int width = table.getLayoutParams().width;
        int numRows = 12;
        int newHeight = ((height/16 + 45) * numRows) + 90;

        for (int i = 0; i < numRows; i++){
            addRow(newHeight);
        }

    }

    protected void addRow(int newHeight){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        TableLayout table = (TableLayout) findViewById(R.id.AppointmentsTable);

        int width = table.getLayoutParams().width;

        TableRow row = new TableRow(this);

        row.setPadding(15,45,15,0);


        int numRows = 12;
        table.setLayoutParams(new TableLayout.LayoutParams(table.getLayoutParams().width, newHeight));


        TextView appointmentDay = new TextView(this);
        TextView appointmentTime = new TextView(this);
        TextView appointmentAvailablility = new TextView(this);

        appointmentDay.setTextColor(Color.parseColor("#000000"));
        appointmentTime.setTextColor(Color.parseColor("#000000"));
        appointmentAvailablility.setTextColor(Color.parseColor("#000000"));

        appointmentDay.setBackgroundColor(Color.parseColor("#F2F197"));
        appointmentTime.setBackgroundColor(Color.parseColor("#F2F197"));
        appointmentAvailablility.setBackgroundColor(Color.parseColor("#F2F197"));

        appointmentDay.setGravity(Gravity.CENTER);
        appointmentTime.setGravity(Gravity.CENTER);
        appointmentAvailablility.setGravity(Gravity.CENTER);

        appointmentDay.setTextSize(15);
        appointmentTime.setTextSize(15);
        appointmentAvailablility.setTextSize(15);

        appointmentDay.setMaxWidth(width/3);
        appointmentTime.setMaxWidth(width/3);
        appointmentAvailablility.setMaxWidth(width/3);

        appointmentDay.setMinWidth(width/3);
        appointmentTime.setMinWidth(width/3);
        appointmentAvailablility.setMinWidth(width/3);

        appointmentDay.setMaxHeight(newHeight/16);
        appointmentTime.setMaxHeight(newHeight/16);
        appointmentAvailablility.setMaxHeight(newHeight/16);

        appointmentDay.setMinHeight(newHeight/16);
        appointmentTime.setMinHeight(newHeight/16);
        appointmentAvailablility.setMinHeight(newHeight/16);

        appointmentDay.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        appointmentTime.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        appointmentAvailablility.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

        String textDay = "March 20th";
        String textTime = "8:00am - 9:00am";
        String textAvailablility = "3 Appts";

        appointmentDay.setText(textDay);
        appointmentTime.setText(textTime);
        appointmentAvailablility.setText(textAvailablility);

        row.addView(appointmentDay);
        row.addView(appointmentTime);
        row.addView(appointmentAvailablility);



        table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }
}
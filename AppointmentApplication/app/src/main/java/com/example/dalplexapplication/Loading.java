package com.example.dalplexapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class Loading  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        SharedPreferences userInfo = getSharedPreferences("userinfo", 0);
        String username = userInfo.getString("username", "None");


        Intent intent;
        if (username.equals("None")){
            intent = new Intent(Loading.this, Login.class);
        }
        else{
            intent = new Intent(Loading.this, LandingPage.class);
        }
        startActivity(intent);
    }
}

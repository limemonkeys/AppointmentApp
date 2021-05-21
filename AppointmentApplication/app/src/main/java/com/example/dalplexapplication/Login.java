package com.example.dalplexapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button filterMenu = (Button) findViewById(R.id.button);
        filterMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView username = (TextView) findViewById(R.id.username);
                TextView password = (TextView) findViewById(R.id.password);

                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();

                if (usernameText.equals("") || passwordText.equals("")){
                    Snackbar snackbar = Snackbar.make(v, "Missing Username or Password", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                else{
                    Intent intent = new Intent(Login.this, LandingPage.class);
                    startActivity(intent);
                }
            }
        });
    }
}

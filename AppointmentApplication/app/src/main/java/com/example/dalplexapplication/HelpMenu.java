package com.example.dalplexapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HelpMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);


        TableLayout table = (TableLayout) findViewById(R.id.FAQTable);
        table.setLayoutParams(new TableLayout.LayoutParams(table.getLayoutParams().width, 3300));


        CharSequence questionOneString = "What Does This App Do?";
        CharSequence answerOneString = "The Dalplex Fitness Hall app assists in acquiring Dalplex " +
                "fitness hall appointments.";

        CharSequence questionTwoString = "What Makes This App Different From the Website?";
        CharSequence answerTwoString =
                "This app allows for filtration of appointments so you only have to see " +
                    "days and times you want.\n\n"+
                "The app also provides notifications every five minutes whether there are freed " +
                    "appointments that were previously at zero availability or if there are " +
                    "freshly released appointments. If there are no freed or fresh " +
                    "appointments, you will not get a notification.\n\n" +
                "IMPORTANT NOTE: I cannot afford to run server based push notifications at a " +
                    "frequency as high as five minute intervals, therefore the app must be " +
                    "running either in the foreground, background, or background while you " +
                    "phone is turned off. DO NOT close the app if you wish to receive " +
                    "notifications.";

        CharSequence questionThreeString = "Who am I?";
        CharSequence answerThreeString =
                "My name is Joseph Burton. I am a full time Computer Science student at Dalhousie" +
                        " University.\n\n" +
                "You can get in contact with me through email: macburton1000@gmail.com.\n\n" +
                "You can also find me on LinkedIn.";

        CharSequence questionFourString = "How Can I Support You?";
        CharSequence answerFourString =
                "Rating this app ★★★★★. Being a student entering the workforce, it’s important " +
                        "I built good credit. This is one way I can do so.";

        CharSequence questionFiveString = "Issues with the App?";
        CharSequence answerFiveString =
                "Contact me directly through email: macburton1000@gmail.com";

        TextView questionOneTextView = (TextView) findViewById(R.id.QuestionOne);
        TextView questionTwoTextView = (TextView) findViewById(R.id.QuestionTwo);
        TextView questionThreeTextView = (TextView) findViewById(R.id.QuestionThree);
        TextView questionFourTextView = (TextView) findViewById(R.id.QuestionFour);
        TextView questionFiveTextView = (TextView) findViewById(R.id.QuestionFive);

        TextView answerOneTextView = (TextView) findViewById(R.id.AnswerOne);
        TextView answerTwoTextView = (TextView) findViewById(R.id.AnswerTwo);
        TextView answerThreeTextView = (TextView) findViewById(R.id.AnswerThree);
        TextView answerFourTextView = (TextView) findViewById(R.id.AnswerFour);
        TextView answerFiveTextView = (TextView) findViewById(R.id.AnswerFive);

        questionOneTextView.setText(questionOneString);
        questionTwoTextView.setText(questionTwoString);
        questionThreeTextView.setText(questionThreeString);
        questionFourTextView.setText(questionFourString);
        questionFiveTextView.setText(questionFiveString);

        answerOneTextView.setText(answerOneString);
        answerTwoTextView.setText(answerTwoString);
        answerThreeTextView.setText(answerThreeString);
        answerFourTextView.setText(answerFourString);
        answerFiveTextView.setText(answerFiveString);


        ImageView helpButton = (ImageView) findViewById(R.id.helpButton);
        helpButton.setColorFilter(Color.GRAY);

        ImageView refreshButton = (ImageView) findViewById(R.id.refreshButton);
        refreshButton.setColorFilter(Color.GRAY);

        ImageView menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpMenu.this, LandingPage.class);
                startActivity(intent);
            }
        });

        ImageView filterMenu = findViewById(R.id.filterMenu);
        filterMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpMenu.this, FilterMenu.class);
                startActivity(intent);
            }
        });
    }
}

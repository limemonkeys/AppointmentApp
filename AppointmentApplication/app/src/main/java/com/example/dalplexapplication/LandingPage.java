package com.example.dalplexapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class LandingPage extends AppCompatActivity {

    int rowCounter = 1;
    ArrayList<String> appointmentTimes = new ArrayList<>();
    ArrayList<Integer> appointmentAvailablility = new ArrayList<>();
    ArrayList<String> appointmentDates = new ArrayList<>();
    ArrayList<Appointment> appointments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        //Get saved variables (example for two different SharedPreferences)
        SharedPreferences mPrefs = getSharedPreferences("userinfo", 0);
        String mString = mPrefs.getString("tag", "default_value_if_variable_not_found");

        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString("tag", "gunga ginga!").commit();

        SharedPreferences nPrefs = getSharedPreferences("preferences", 0);
        String nString = nPrefs.getString("Mondays", "default_value_if_variable_not_found");

        SharedPreferences.Editor nEditor = nPrefs.edit();
        nEditor.putString("Mondays", "yes please!").commit();

        System.out.println("mString ONE: " + mString);

        mPrefs = getSharedPreferences("userinfo", 0);
        mString = mPrefs.getString("tag", "default_value_if_variable_not_found");

        nPrefs = getSharedPreferences("preferences", 0);
        nString = nPrefs.getString("Mondays", "default_value_if_variable_not_found");

        System.out.println("nString ONE: " + nString);
         */




        ImageView menuButton = (ImageView) findViewById(R.id.menuButton);
        menuButton.setColorFilter(Color.GRAY);

        ImageView filterMenu = (ImageView) findViewById(R.id.filterMenu);
        filterMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Uri uri = Uri.parse("https://www.dalsports.dal.ca/Program/GetProgramDetails?courseId=8993d840-c85b-4afb-b8a9-3c30b3c16817&semesterId=cefa4d21-6d59-4e72-81b8-7d66b8843351#"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

                 */
                Intent intent = new Intent(LandingPage.this, FilterMenu.class);
                startActivity(intent);
            }
        });

        ImageView settingsButton = (ImageView) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Uri uri = Uri.parse("https://www.dalsports.dal.ca/Program/GetProgramDetails?courseId=8993d840-c85b-4afb-b8a9-3c30b3c16817&semesterId=cefa4d21-6d59-4e72-81b8-7d66b8843351#"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

                 */
                Intent intent = new Intent(LandingPage.this, SettingsMenu.class);
                startActivity(intent);
            }
        });
        ImageView helpButton = (ImageView) findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Uri uri = Uri.parse("https://www.dalsports.dal.ca/Program/GetProgramDetails?courseId=8993d840-c85b-4afb-b8a9-3c30b3c16817&semesterId=cefa4d21-6d59-4e72-81b8-7d66b8843351#"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

                 */
                Intent intent = new Intent(LandingPage.this, HelpMenu.class);
                startActivity(intent);
            }
        });
        //TODO: Uncomment to get appointments
        //Dalplex back up :)
        AppointmentRetriever appt = new AppointmentRetriever();
        appt.execute();

    }

    public void createTable(ArrayList<Appointment> returnedAppointments){
        TableLayout table = (TableLayout) findViewById(R.id.AppointmentsTable);
        int height = table.getLayoutParams().height;
        int width = table.getLayoutParams().width;

        SharedPreferences dayPreferences = getSharedPreferences("dayPreferences", 0);

        int numRows = 0;
        for (Appointment appointment : returnedAppointments){
            //System.out.println(appointment.getAvailable());
            if (appointment.getAvailable() > 0){
                String currDate = appointment.getDate().split(",")[0];
                boolean preferredDate = dayPreferences.getString(currDate, String.valueOf(false)).equals("true");
                System.out.println("DATE: " + appointment.getDate().split(",")[0]);
                System.out.println("DAYPREF: " + dayPreferences.getString(appointment.getDate(), String.valueOf(false)));
                System.out.println("PREFERREDDATE: " + preferredDate);
                if (preferredDate){
                    numRows++;
                }
            }
        }

        int newHeight = Math.max(((200 + 45) * numRows) + 45, height);


        for (Appointment appointment : returnedAppointments){
            if (appointment.getAvailable() > 0){
                String currDate = appointment.getDate().split(",")[0];
                boolean preferredDate = dayPreferences.getString(currDate, String.valueOf(false)).equals("true");
                System.out.println("DATE: " + appointment.getDate().split(",")[0]);
                System.out.println("DAYPREF: " + dayPreferences.getString(appointment.getDate(), String.valueOf(false)));
                System.out.println("PREFERREDDATE: " + preferredDate);
                if (preferredDate){
                    addRow(newHeight, appointment);
                }

            }
        }

        System.out.println(appointmentTimes);
        System.out.println(appointmentAvailablility);
        System.out.println(appointmentDates);
        System.out.println(appointments);


    }

    protected void addRow(int newHeight, Appointment appointment){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        TableLayout table = (TableLayout) findViewById(R.id.AppointmentsTable);

        int width = table.getLayoutParams().width;

        TableRow row = new TableRow(this);

        row.setPadding(15,45,15,0);

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

        appointmentDay.setMaxHeight(150);
        appointmentTime.setMaxHeight(150);
        appointmentAvailablility.setMaxHeight(150);

        appointmentDay.setMinHeight(150);
        appointmentTime.setMinHeight(150);
        appointmentAvailablility.setMinHeight(150);

        appointmentDay.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        appointmentTime.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        appointmentAvailablility.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

        /*
        String textDay = "March 20th";
        String textTime = "8:00am - 9:00am";
        String textAvailablility = "3 Appts";
         */

        //String textDay = appointment.getDate();

        String textDay = appointment.getDate().replace(", ", "\n");

        String textTime = appointment.getTime().replace(" - ", "-\n");
        String textAvailablility = String.valueOf(appointment.getAvailable()) + " appt(s)";

        appointmentDay.setText(textDay);
        appointmentTime.setText(textTime);
        appointmentAvailablility.setText(textAvailablility);

        row.addView(appointmentDay);
        row.addView(appointmentTime);
        row.addView(appointmentAvailablility);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://www.dalsports.dal.ca/Program/GetProgramDetails?courseId=8993d840-c85b-4afb-b8a9-3c30b3c16817&semesterId=cefa4d21-6d59-4e72-81b8-7d66b8843351#"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);



            }
        });

        table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

    }

    public class AppointmentRetriever extends AsyncTask<Void, Void, ArrayList<Appointment>> {

        @Override
        protected ArrayList<Appointment> doInBackground(Void... voids) {
            try {
                String url = "https://www.dalsports.dal.ca/Program/GetProgramDetails?courseId=8993d840-c85b-4afb-b8a9-3c30b3c16817&semesterId=cefa4d21-6d59-4e72-81b8-7d66b8843351";
                Document doc = Jsoup.connect(url).get();
                System.out.println("Got website");
                //System.out.println(doc.body());
                //Elements byClass = doc.body().getElementsByClass("TitleDiv");
                //Elements byClass = doc.getElementsByClass("TitleDiv");
                Elements byClass = doc.getElementsByClass("caption program-schedule-card-caption");
                System.out.println("getByClass");
                //Elements byClass = doc.getAllElements();
                System.out.println(byClass.size());
                for (Element appointment : byClass){
                    System.out.println("Iteration");
                    //System.out.println("---------------------------");
                    //System.out.print(appointment.getElementsByClass("program-schedule-card-header").text() + " ");
                    //System.out.print(appointment.getElementsByClass("pull-right").text() + " ");
                    //System.out.println(appointment.getElementsByTag("small").text() + " " + i);
                    //String[] temp = appointment.getElementsByTag("small").text().split("available");

                    String temp = appointment.getElementsByTag("small").text();
                    System.out.println("Get text");
                    String[] tempList = temp.split(" ");
                    System.out.println("Split text");

                    String timeString = tempList[0] + " " +  tempList[1] + " " + tempList[2] + " " + tempList[3] + " " + tempList[4];
                    System.out.println("Create timeString");

                    appointmentTimes.add(timeString);
                    System.out.println("added to appointmentTimes");

                    String dateString = appointment.getElementsByClass("program-schedule-card-header").text();
                    System.out.println("Created dateString");

                    appointmentDates.add(dateString);
                    System.out.println("added to appointmentDates");

                    temp = appointment.getElementsByClass("pull-right").text();
                    System.out.println("Created temp?");

                    tempList = temp.split(" ");
                    System.out.println("Split temp?");

                    int availableString = 0;
                    if (!tempList[0].equals("No")){
                        availableString = Integer.parseInt(tempList[0]);
                    }

                    System.out.println(availableString);

                    System.out.println("Created availableString");

                    appointmentAvailablility.add(availableString);
                    System.out.println("added to appointmentAvailablility");

                    System.out.println("Finished creation");

                    Appointment newAppointment = new Appointment(dateString, timeString, availableString);
                    System.out.println("Appointment created");
                    appointments.add(newAppointment);
                    System.out.println("added to appointments");
                    //System.out.println(newAppointment);
                }
                //System.out.println(appointmentTimes);
                //System.out.println(appointmentDates);
                //System.out.println(appointmentAvailablility);
                //onPostExecute(appointments);
                return appointments;
            } catch (IOException e) {
                System.out.println("exception");
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(ArrayList<Appointment> returnedAppointments) {
            returnSize(returnedAppointments);
        }
    }
    public void returnSize(ArrayList<Appointment> returnedAppointments){
        createTable(returnedAppointments);
    }
}


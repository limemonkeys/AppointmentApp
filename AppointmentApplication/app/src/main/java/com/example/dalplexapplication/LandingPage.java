package com.example.dalplexapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import org.jsoup.select.Evaluator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class LandingPage extends AppCompatActivity {

    ArrayList<String> appointmentTimes = new ArrayList<>();
    ArrayList<Integer> appointmentAvailability = new ArrayList<>();
    ArrayList<String> appointmentDates = new ArrayList<>();
    ArrayList<Appointment> appointments = new ArrayList<>();
    ArrayList<Appointment> previousAppointments = new ArrayList<>();
    Runnable RunnableRefresh;

    ArrayList<Appointment> openedAppointments = new ArrayList<>();
    ArrayList<Appointment> freshAppointments = new ArrayList<>();

    // 5 minute refresh interval.
    //private final static int REFRESH_INTERVAL = 1000 * 60 * 5;
    private final static int REFRESH_INTERVAL = 500 * 60;
    final String channelName1 = "dalplexChannelFreshAppointments";
    final String channelName2 = "dalplexChannelNewAppointments";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialization of images at the top of application.
        ImageView menuButton = findViewById(R.id.menuButton);
        menuButton.setColorFilter(Color.GRAY);

        ImageView filterMenu = findViewById(R.id.filterMenu);
        filterMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingPage.this, FilterMenu.class);
                startActivity(intent);
            }
        });

        ImageView settingsButton = findViewById(R.id.refreshButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingPage.this, LandingPage.class);
                startActivity(intent);
            }
        });

        ImageView helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingPage.this, HelpMenu.class);
                startActivity(intent);
            }
        });

        // Adding header value in table.
        // Removing position 0 makes listings invisible, this is one reason header is a row.
        TableLayout table = (TableLayout) findViewById(R.id.AppointmentsTable);
        addRow(table.getHeight(), new Appointment("DAY", "TIME", -1));

        Handler HandlerRefresh = new Handler();
        RunnableRefresh = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                openedAppointments.clear();
                freshAppointments.clear();

                SharedPreferences dayPreferences = getSharedPreferences("dayPreferences", 0);
                SharedPreferences timePreferences = getSharedPreferences("timePreferences", 0);

                // Refresh appointments
                AppointmentRetriever appts = new AppointmentRetriever();
                try {
                    // Refill table
                    appts.execute().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Below compares fetched appointments to previously fetched appointments
                // Before checking for new appointments, ensure there is a history
                if (!previousAppointments.isEmpty()){
                    for (Appointment appointment : appointments){
                        for (Appointment previousAppointment : previousAppointments){
                            if (appointment.getAvailable() > 0 && previousAppointment.getAvailable() == 0){
                                boolean preferredDate = dayPreferences.getString(appointment.getDate().split(",")[0], String.valueOf(false)).equals("true");
                                boolean compareDates = previousAppointment.getDate().equals(appointment.getDate());
                                if (preferredDate && compareDates){
                                    boolean preferredTime = timePreferences.getString(appointment.getTime(), String.valueOf(false)).equals("true");
                                    boolean compareTime = previousAppointment.getTime().equals(appointment.getTime());
                                    if (preferredTime && compareTime){
                                        if (!openedAppointments.contains(appointment)){
                                            openedAppointments.add(appointment);
                                        }
                                    }
                                }
                            }
                        }
                    }


                    // Begin checking for newly opened appointments
                    for (Appointment appointment : appointments){
                        // First, check if date and time is preferred
                        boolean preferredDate = dayPreferences.getString(appointment.getDate().split(",")[0], String.valueOf(false)).equals("true");
                        boolean preferredTime = timePreferences.getString(appointment.getTime(), String.valueOf(false)).equals("true");
                        if (preferredDate && preferredTime){
                            // If there is a new day in the list, new appointments
                            if (previousAppointments.stream().noneMatch(tempAppointment1 -> tempAppointment1.getDate().equals(appointment.getDate()))){
                                if(!freshAppointments.contains(appointment)){
                                    freshAppointments.add(appointment);
                                }
                            }
                            // Otherwise date already exists.
                            // Cycle through appointments looking for that day, then check if time exists.
                            else{
                                boolean existingTime = false;
                                for (Appointment previousAppointment : previousAppointments){
                                    if (appointment.getDate().equals(previousAppointment.getDate())){
                                        if(appointment.getTime().equals(previousAppointment.getTime())){
                                            existingTime = true;
                                        }
                                    }
                                }
                                if (!existingTime){
                                    if(!freshAppointments.contains(appointment)){
                                        freshAppointments.add(appointment);
                                    }
                                }
                            }
                        }
                    }

                }

                // Create notification based on if there are fresh or newly freed appointments
                if (!freshAppointments.isEmpty()){
                    createNotification(freshAppointments, "fresh");
                }

                if (!openedAppointments.isEmpty()){
                    createNotification(openedAppointments, "opened");
                }
                HandlerRefresh.postDelayed(RunnableRefresh, REFRESH_INTERVAL);
            }
        };
        RunnableRefresh.run();

    }

    public void createNotification(ArrayList<Appointment> newAppointments, String intention){
        // Format the notification
        StringBuilder appointmentsString = new StringBuilder();
        for (Appointment appointment : newAppointments){
            appointmentsString.append(appointment.getDate()).append(" ").append(appointment.getTime());
            if (!appointment.equals(newAppointments.get(newAppointments.size() - 1))){
                appointmentsString.append("\n");
            }
        }

        createNotificationChannel();

        // Base builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelName2)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Please contact developer!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(appointmentsString))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (intention.equals("fresh")){
            builder = new NotificationCompat.Builder(getApplicationContext(), channelName1)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle("Freshly Created Dalplex Appointments Available:")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(appointmentsString))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }
        if (intention.equals("opened")){
            builder = new NotificationCompat.Builder(getApplicationContext(), channelName2)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle("Previously Occupied Dalplex Appointments Available:")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(appointmentsString))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(0, builder.build());
    }

    private void createNotificationChannel() {
        // Method to create notifcation channels
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name1 = "dalplexChannelFreshAppointments";
            CharSequence name2 = "dalplexChannelNewAppointments";
            String description1 = "Channel for the dalplex's fresh appointments";
            String description2 = "Channel for the dalplex's opened appointments";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel1 = new NotificationChannel("dalplexChannelFreshAppointments", name1, importance);
            NotificationChannel channel2 = new NotificationChannel("dalplexChannelNewAppointments", name2, importance);
            channel1.setDescription(description1);
            channel2.setDescription(description2);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }
    }

    public void createTable(ArrayList<Appointment> returnedAppointments){
        // Dynamically size the table based on number of appointments
        TableLayout table = (TableLayout) findViewById(R.id.AppointmentsTable);
        int height = table.getLayoutParams().height;

        // Go through the rows and remove everything except the
        for (int i = table.getChildCount() - 1; i > -1; i--){
            TableRow currRow = (TableRow) table.getChildAt(i);
            TextView textviewDay = (TextView) currRow.getChildAt(0);
            String stringDay = textviewDay.getText().toString();
            if (!stringDay.equals("Day")){
                table.removeView(currRow);
            }
        }

        SharedPreferences dayPreferences = getSharedPreferences("dayPreferences", 0);
        SharedPreferences timePreferences = getSharedPreferences("timePreferences", 0);

        int numRows = 0;
        for (Appointment appointment : returnedAppointments){
            if (appointment.getAvailable() > 0){
                String currDate = appointment.getDate().split(",")[0];
                boolean preferredDate = dayPreferences.getString(currDate, String.valueOf(false)).equals("true");
                if (preferredDate){
                    String currTime = appointment.getTime();
                    boolean preferredTime = timePreferences.getString(currTime, String.valueOf(false)).equals("true");
                    if (preferredTime){
                        numRows++;
                    }
                }
            }
        }

        int header = 100 + 45;
        int newHeight = Math.max(((195 + 45) * numRows) + 45 + header, height);

        for (Appointment appointment : returnedAppointments){
            if (appointment.getAvailable() > 0){
                String currDate = appointment.getDate().split(",")[0];
                boolean preferredDate = dayPreferences.getString(currDate, String.valueOf(false)).equals("true");
                if (preferredDate){
                    String currTime = appointment.getTime();
                    boolean preferredTime = timePreferences.getString(currTime, String.valueOf(false)).equals("true");
                    if (preferredTime){
                        addRow(newHeight, appointment);
                    }
                }
            }
        }
        //table.setLayoutParams(new TableLayout.LayoutParams(table.getLayoutParams().width, newHeight));
    }

    public class AppointmentRetriever extends AsyncTask<Void, Void, ArrayList<Appointment>> {
        // Background async task to query Dalplex website.
        @Override
        protected ArrayList<Appointment> doInBackground(Void... voids) {
            try {
                // Scrape website for appointments and format them
                String url = "https://www.dalsports.dal.ca/Program/GetProgramDetails?courseId=8993d840-c85b-4afb-b8a9-3c30b3c16817&semesterId=cefa4d21-6d59-4e72-81b8-7d66b8843351";
                Document doc = Jsoup.connect(url).get();
                Elements byClass = doc.getElementsByClass("caption program-schedule-card-caption");

                appointmentAvailability.clear();
                appointmentDates.clear();
                appointmentTimes.clear();

                previousAppointments.clear();
                previousAppointments.addAll(appointments);
                appointments.clear();

                for (Element appointment : byClass){
                    String temp = appointment.getElementsByTag("small").text();
                    String[] tempList = temp.split(" ");
                    String timeString = tempList[0] + " " +  tempList[1] + " " + tempList[2] + " " + tempList[3] + " " + tempList[4];

                    appointmentTimes.add(timeString);
                    String dateString = appointment.getElementsByClass("program-schedule-card-header").text();

                    appointmentDates.add(dateString);
                    temp = appointment.getElementsByClass("pull-right").text();
                    tempList = temp.split(" ");

                    int availableString = 0;
                    if (!tempList[0].equals("No")){
                        availableString = Integer.parseInt(tempList[0]);
                    }
                    appointmentAvailability.add(availableString);
                    Appointment newAppointment = new Appointment(dateString, timeString, availableString);
                    appointments.add(newAppointment);
                }
                return appointments;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(ArrayList<Appointment> returnedAppointments) {
            returnSize(returnedAppointments);
        }
    }
    public void returnSize(ArrayList<Appointment> returnedAppointments){
        // Post execute, send to make dynamic table
        createTable(returnedAppointments);
    }

    protected void addRow(int newHeight, Appointment appointment){
        // Add row to table
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        TableLayout table = (TableLayout) findViewById(R.id.AppointmentsTable);

        TableRow row = new TableRow(this);

        TextView appointmentDay = new TextView(this);
        TextView appointmentTime = new TextView(this);
        TextView appointmentAvailability = new TextView(this);

        // Only should happen with placeholder value
        if (appointment.getAvailable() == -1){

            int width = table.getLayoutParams().width;

            row.setPadding(15,45,15,0);

            appointmentDay.setTextColor(Color.parseColor("#000000"));
            appointmentTime.setTextColor(Color.parseColor("#000000"));
            appointmentAvailability.setTextColor(Color.parseColor("#000000"));

            appointmentDay.setBackgroundColor(Color.parseColor("#F2F197"));
            appointmentTime.setBackgroundColor(Color.parseColor("#F2F197"));
            appointmentAvailability.setBackgroundColor(Color.parseColor("#F2F197"));

            appointmentDay.setGravity(Gravity.CENTER);
            appointmentTime.setGravity(Gravity.CENTER);
            appointmentAvailability.setGravity(Gravity.CENTER);

            appointmentDay.setTextSize(15);
            appointmentTime.setTextSize(15);
            appointmentAvailability.setTextSize(15);

            appointmentDay.setMaxWidth(width/3);
            appointmentTime.setMaxWidth(width/3);
            appointmentAvailability.setMaxWidth(width/3);

            appointmentDay.setMinWidth(width/3);
            appointmentTime.setMinWidth(width/3);
            appointmentAvailability.setMinWidth(width/3);

            appointmentDay.setMaxHeight(100);
            appointmentTime.setMaxHeight(100);
            appointmentAvailability.setMaxHeight(100);

            appointmentDay.setMinHeight(100);
            appointmentTime.setMinHeight(100);
            appointmentAvailability.setMinHeight(100);

            appointmentDay.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            appointmentTime.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            appointmentAvailability.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

            //String textDay = appointment.getDate().replace(", ", "\n");
            String textDay = "Day";
            String textTime = "Time";
            String textAvailability = "Appts";

            appointmentDay.setTextColor(Color.BLACK);
            appointmentTime.setTextColor(Color.BLACK);
            appointmentAvailability.setTextColor(Color.BLACK);

            appointmentDay.setTypeface(Typeface.DEFAULT_BOLD);
            appointmentTime.setTypeface(Typeface.DEFAULT_BOLD);
            appointmentAvailability.setTypeface(Typeface.DEFAULT_BOLD);

            appointmentDay.setText(textDay);
            appointmentTime.setText(textTime);
            appointmentAvailability.setText(textAvailability);

            appointmentDay.setTextSize(28.0f);
            appointmentTime.setTextSize(28.0f);
            appointmentAvailability.setTextSize(28.0f);

            row.addView(appointmentDay);
            row.addView(appointmentTime);
            row.addView(appointmentAvailability);

        }
        // Otherwise with normal listings
        else {
            int width = table.getLayoutParams().width;

            row.setPadding(15,45,15,0);

            table.setLayoutParams(new TableLayout.LayoutParams(table.getLayoutParams().width, newHeight));

            appointmentDay.setTextColor(Color.parseColor("#000000"));
            appointmentTime.setTextColor(Color.parseColor("#000000"));
            appointmentAvailability.setTextColor(Color.parseColor("#000000"));

            appointmentDay.setBackgroundColor(Color.parseColor("#F2F197"));
            appointmentTime.setBackgroundColor(Color.parseColor("#F2F197"));
            appointmentAvailability.setBackgroundColor(Color.parseColor("#F2F197"));

            appointmentDay.setGravity(Gravity.CENTER);
            appointmentTime.setGravity(Gravity.CENTER);
            appointmentAvailability.setGravity(Gravity.CENTER);

            appointmentDay.setTextSize(15);
            appointmentTime.setTextSize(15);
            appointmentAvailability.setTextSize(15);

            appointmentDay.setMaxWidth(width/3);
            appointmentTime.setMaxWidth(width/3);
            appointmentAvailability.setMaxWidth(width/3);

            appointmentDay.setMinWidth(width/3);
            appointmentTime.setMinWidth(width/3);
            appointmentAvailability.setMinWidth(width/3);

            appointmentDay.setMaxHeight(150);
            appointmentTime.setMaxHeight(150);
            appointmentAvailability.setMaxHeight(150);

            appointmentDay.setMinHeight(150);
            appointmentTime.setMinHeight(150);
            appointmentAvailability.setMinHeight(150);

            appointmentDay.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            appointmentTime.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
            appointmentAvailability.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

            String textDay = appointment.getDate().replace(", ", "\n");

            String textTime = appointment.getTime().replace(" - ", "-\n");
            String textAvailability = String.valueOf(appointment.getAvailable()) + " appt(s)";

            appointmentDay.setTextColor(Color.BLACK);
            appointmentTime.setTextColor(Color.BLACK);
            appointmentAvailability.setTextColor(Color.BLACK);

            appointmentDay.setText(textDay);
            appointmentTime.setText(textTime);
            appointmentAvailability.setText(textAvailability);

            appointmentDay.setTextSize(16.0f);
            appointmentTime.setTextSize(16.0f);
            appointmentAvailability.setTextSize(16.0f);

            row.addView(appointmentDay);
            row.addView(appointmentTime);
            row.addView(appointmentAvailability);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("https://www.dalsports.dal.ca/Program/GetProgramDetails?courseId=8993d840-c85b-4afb-b8a9-3c30b3c16817&semesterId=cefa4d21-6d59-4e72-81b8-7d66b8843351#");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }

        table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }
}

package com.example.dalplexapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class LandingPage extends AppCompatActivity {

    int rowCounter = 1;
    ArrayList<String> appointmentTimes = new ArrayList<>();
    ArrayList<Integer> appointmentAvailablility = new ArrayList<>();
    ArrayList<String> appointmentDates = new ArrayList<>();
    ArrayList<Appointment> appointments = new ArrayList<>();
    ArrayList<Appointment> previousAppointments = new ArrayList<>();
    Runnable RunnableRefresh;
    private static boolean activityVisible;
    //private final static int INTERVAL = 1000 * 60 * 2; //2 minutes
    private final static int INTERVAL = 500 * 60; //30 minute secs
    ArrayList<Appointment> openedAppointments = new ArrayList<>();
    ArrayList<Appointment> freshAppointments = new ArrayList<>();


    final String channelName1 = "dalplexChannelNewAppointments";
    final String channelName2 = "dalplexChannelFreshAppointments";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                // Compare fetched appointments to previously fetched appointments
                // Checking for new openings for desired appointments (Cancelled full sessions)

                // Before checking for new appointments, ensure there is a history
                if (!previousAppointments.isEmpty()){
                    //Fresh test using this.
                    //appointments.add(new Appointment("Monday, June 28, 2021", "6:00 PM - 7:00 PM", 55));

                    //Open test using this
                    //appointments.set(0, new Appointment(appointments.get(0).getDate(), appointments.get(0).getTime(), appointments.get(0).getAvailable() + 1));

                    //Open test using this
                    //appointments.set(0, new Appointment(appointments.get(0).getDate(), appointments.get(0).getTime(), 1));
                    //previousAppointments.set(0, new Appointment(appointments.get(0).getDate(), appointments.get(0).getTime(), 0));

                    //appointments.set(1, new Appointment(appointments.get(1).getDate(), appointments.get(1).getTime(), 1));
                    //previousAppointments.set(1, new Appointment(appointments.get(1).getDate(), appointments.get(1).getTime(), 0));

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


                if (!freshAppointments.isEmpty()){
                    createNotification(freshAppointments, "fresh");
                    System.out.println("FRESH");
                }

                if (!openedAppointments.isEmpty()){
                    createNotification(openedAppointments, "opened");
                    System.out.println("OPENED");

                }
                HandlerRefresh.postDelayed(RunnableRefresh, INTERVAL);
            }
        };
        RunnableRefresh.run();

    }

    public void createNotification(ArrayList<Appointment> newAppointments, String intention){
        StringBuilder appointmentsString = new StringBuilder();
        for (Appointment appointment : newAppointments){
            appointmentsString.append(appointment.getDate()).append(" ").append(appointment.getTime());
            if (!appointment.equals(newAppointments.get(newAppointments.size() - 1))){
                appointmentsString.append("\n");
            }
        }

        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelName2)
                .setSmallIcon(R.drawable.running)
                .setContentTitle("Please contact developer!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(appointmentsString))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (intention.equals("fresh")){
            builder = new NotificationCompat.Builder(getApplicationContext(), channelName1)
                    .setSmallIcon(R.drawable.running)
                    .setContentTitle("Freshly Created Dalplex Appointments Available:")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(appointmentsString))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }
        if (intention.equals("opened")){
            builder = new NotificationCompat.Builder(getApplicationContext(), channelName2)
                    .setSmallIcon(R.drawable.running)
                    .setContentTitle("Previously Occupied Dalplex Appointments Available:")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(appointmentsString))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(0, builder.build());
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name1 = "dalplexChannelNewAppointments";
            CharSequence name2 = "dalplexChannelFreshAppointments";
            String description1 = "Channel for the dalplex's new appointments";
            String description2 = "Channel for the dalplex's fresh appointments";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel1 = new NotificationChannel("dalplexChannelNewAppointments", name1, importance);
            NotificationChannel channel2 = new NotificationChannel("dalplexChannelFreshAppointments", name2, importance);
            channel1.setDescription(description1);
            channel2.setDescription(description2);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }
    }

    public void createTable(ArrayList<Appointment> returnedAppointments){
        TableLayout table = (TableLayout) findViewById(R.id.AppointmentsTable);
        int height = table.getLayoutParams().height;
        int previousTableSize = table.getChildCount();

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

        int newHeight = Math.max(((195 + 45) * numRows) + 45, height);

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

        // After adding each row, remove each row previously there. removeAllViews() breaks list.
        // Do not remove view at position 0. Note: Despite how it seem, the list completely updates
        for (int i = 0; i < previousTableSize; i++){
            table.removeViewAt(1);
        }
    }

    public class AppointmentRetriever extends AsyncTask<Void, Void, ArrayList<Appointment>> {

        @Override
        protected ArrayList<Appointment> doInBackground(Void... voids) {
            try {
                String url = "https://www.dalsports.dal.ca/Program/GetProgramDetails?courseId=8993d840-c85b-4afb-b8a9-3c30b3c16817&semesterId=cefa4d21-6d59-4e72-81b8-7d66b8843351";
                Document doc = Jsoup.connect(url).get();
                Elements byClass = doc.getElementsByClass("caption program-schedule-card-caption");

                appointmentAvailablility.clear();
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
                    appointmentAvailablility.add(availableString);
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
        createTable(returnedAppointments);
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
        //appointmentAvailablility.setMaxHeight(150);

        appointmentDay.setMinHeight(150);
        appointmentTime.setMinHeight(150);
        appointmentAvailablility.setMinHeight(150);

        appointmentDay.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        appointmentTime.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        appointmentAvailablility.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

        String textDay = appointment.getDate().replace(", ", "\n");

        String textTime = appointment.getTime().replace(" - ", "-\n");
        String textAvailability = String.valueOf(appointment.getAvailable()) + " appt(s)";

        appointmentDay.setTextColor(Color.BLACK);
        appointmentTime.setTextColor(Color.BLACK);
        appointmentAvailablility.setTextColor(Color.BLACK);

        appointmentDay.setText(textDay);
        appointmentTime.setText(textTime);
        appointmentAvailablility.setText(textAvailability);

        appointmentDay.setTextSize(16.0f);
        appointmentTime.setTextSize(16.0f);
        appointmentAvailablility.setTextSize(16.0f);


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

        System.out.println(row.getHeight());

        table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }
}

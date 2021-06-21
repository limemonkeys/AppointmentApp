package com.example.dalplexapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
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
    Runnable objRunnable;

    //private final static int INTERVAL = 1000 * 60 * 2; //2 minutes

    private final static int INTERVAL = 1000 * 10 * 1; //10 sec


    Handler objHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle objBundle = msg.getData();
            String mymessage = objBundle.getString("AppointmentAvailabilityUpdate");

            System.out.println(mymessage);
        }
    };


    final String channelName = "dalplexChannel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView menuButton = (ImageView) findViewById(R.id.menuButton);
        menuButton.setColorFilter(Color.GRAY);

        ImageView filterMenu = (ImageView) findViewById(R.id.filterMenu);
        filterMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingPage.this, FilterMenu.class);
                startActivity(intent);
            }
        });

        ImageView settingsButton = (ImageView) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingPage.this, SettingsMenu.class);
                startActivity(intent);
            }
        });

        ImageView helpButton = (ImageView) findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingPage.this, HelpMenu.class);
                startActivity(intent);
            }
        });

        //This pulls appointments
        //AppointmentRetriever appt = new AppointmentRetriever();
        //appt.execute();





        //AppointmentRetriever appt = new AppointmentRetriever();
        //appt.execute();
        // notificationId is a unique int for each notification that you must define


        Handler mHandler = new Handler();
        objRunnable = new Runnable() {
            Message objMessage = objHandler.obtainMessage();
            Bundle objBundle = new Bundle();



            @Override
            public void run() {
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                boolean isScreenOn = pm.isScreenOn();
                if (!isScreenOn){
                    createNotification();
                }

                mHandler.postDelayed(objRunnable, INTERVAL);



                objBundle.putString("AppointmentAvailabilityUpdate", "blah blah blah");
                objMessage.setData(objBundle);

                objHandler.sendMessage(objMessage);
                //objHandler.sendEmptyMessage(0);
            }
        };

        //Thread objBgThread = new Thread(objRunnable);
        //objBgThread.start();

        objRunnable.run();


    }

    public void createNotification(){
        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelName)
                .setSmallIcon(R.drawable.running)
                .setContentTitle("title")
                .setContentText("desc")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(0, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "dalplexChannel";
            String description = "channel for the dalplex";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("dalplexChannel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createTable(ArrayList<Appointment> returnedAppointments){
        TableLayout table = (TableLayout) findViewById(R.id.AppointmentsTable);
        int height = table.getLayoutParams().height;

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

        int newHeight = Math.max(((200 + 45) * numRows) + 45, height);

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
    }

    public class AppointmentRetriever extends AsyncTask<Void, Void, ArrayList<Appointment>> {

        @Override
        protected ArrayList<Appointment> doInBackground(Void... voids) {
            try {
                String url = "https://www.dalsports.dal.ca/Program/GetProgramDetails?courseId=8993d840-c85b-4afb-b8a9-3c30b3c16817&semesterId=cefa4d21-6d59-4e72-81b8-7d66b8843351";
                Document doc = Jsoup.connect(url).get();
                Elements byClass = doc.getElementsByClass("caption program-schedule-card-caption");

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
        appointmentAvailablility.setMaxHeight(150);

        appointmentDay.setMinHeight(150);
        appointmentTime.setMinHeight(150);
        appointmentAvailablility.setMinHeight(150);

        appointmentDay.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        appointmentTime.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        appointmentAvailablility.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

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
}

/*
    System.out.println("-------DAY PREFERENCES---------");
    System.out.println(dayPreferences.getString("Monday", String.valueOf(false)).equals("true"));
    System.out.println(dayPreferences.getString("Tuesday", String.valueOf(false)).equals("true"));
    System.out.println(dayPreferences.getString("Wednesday", String.valueOf(false)).equals("true"));
    System.out.println(dayPreferences.getString("Thursday", String.valueOf(false)).equals("true"));
    System.out.println(dayPreferences.getString("Friday", String.valueOf(false)).equals("true"));
    System.out.println(dayPreferences.getString("Saturday", String.valueOf(false)).equals("true"));
    System.out.println(dayPreferences.getString("Sunday", String.valueOf(false)).equals("true"));

    System.out.println();

    System.out.println("-------Time PREFERENCES---------");
    System.out.println(dayPreferences.getString("6:00 AM - 7:00 AM", String.valueOf(false)).equals("true"));
    System.out.println(dayPreferences.getString("7:30 AM - 8:30 AM", String.valueOf(false)).equals("true"));
    System.out.println(dayPreferences.getString("9:00 AM - 10:00 AM", String.valueOf(false)).equals("true"));
    System.out.println(dayPreferences.getString("10:30 AM - 11:30 AM", String.valueOf(false)).equals("true"));
    System.out.println(dayPreferences.getString("12:00 PM - 1:00 PM", String.valueOf(false)).equals("true"));
    System.out.println(dayPreferences.getString("1:30 PM - 2:30 PM", String.valueOf(false)).equals("true"));
    System.out.println(dayPreferences.getString("3:00 PM - 4:00 PM", String.valueOf(false)).equals("true"));
    System.out.println(dayPreferences.getString("4:30 PM - 5:30 PM", String.valueOf(false)).equals("true"));
    System.out.println(dayPreferences.getString("6:00 PM - 7:00 PM", String.valueOf(false)).equals("true"));
    System.out.println(dayPreferences.getString("7:30 PM - 8:30 PM", String.valueOf(false)).equals("true"));
 */

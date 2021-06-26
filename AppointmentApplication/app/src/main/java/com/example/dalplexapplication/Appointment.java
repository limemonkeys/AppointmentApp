package com.example.dalplexapplication;

public class Appointment {

    String date, time;
    int available;

    public Appointment(String date, String time, int available){
        this.date = date;
        this.time = time;
        this.available = available;
    }

    public int getAvailable() {
        return available;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}


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

    @Override
    public String toString() {
        return "Appointment{" +
                "date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", available='" + String.valueOf(available) + '\'' +
                '}';
    }
}


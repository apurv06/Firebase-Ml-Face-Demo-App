package com.example.apurvchaudhary.cameratest.models;

import java.util.ArrayList;

public class dateData {


ArrayList<Long> Entries;
String inTime;
String outTime;

    public ArrayList<Long> getEntries() {
        return Entries;
    }

    public void setEntries(ArrayList<Long> entries) {
        Entries = entries;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String status;

    public dateData(ArrayList<Long> entries, String inTime, String outTime, String status) {
        Entries = entries;
        this.inTime = inTime;
        this.outTime = outTime;
        this.status = status;
    }
}

package com.example.apurvchaudhary.cameratest.models;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class AttendanceEmployee extends Employee {

    int attendance;

    int color;

    DataSnapshot snapshot;

    HashMap<Integer,dateData> map;

    public DataSnapshot getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(DataSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public HashMap<Integer, dateData> getMap() {
        return map;
    }

    public void setMap(HashMap<Integer, dateData> map) {
        this.map = map;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getAttendance() {
        return attendance;
    }

    public void setAttendance(int attendance) {
        this.attendance = attendance;
    }

    AttendanceEmployee()
    {

    }

    AttendanceEmployee(String firstName, String secondName, String employeePosition, String phoneNo, int employeeId, String date) {
      super(firstName,secondName,employeePosition,phoneNo,employeeId,date);
    }

    public void processDates()
    {
        map=new HashMap<>();
        if(snapshot==null||snapshot.getValue()==null)
            return;
            for (DataSnapshot snapshot:snapshot.getChildren())
            {
                String day=snapshot.getKey();
                ArrayList<Long> entries= (ArrayList<Long>) snapshot.child("Entries").getValue();
                String status= (String) snapshot.child("status").getValue();
                String inTime=(String) snapshot.child("inTime").getValue();
                String outTime=(String) snapshot.child("outTime").getValue();
                map.put(Integer.valueOf(day),new dateData(entries,inTime,outTime,status));
            }
    }


}

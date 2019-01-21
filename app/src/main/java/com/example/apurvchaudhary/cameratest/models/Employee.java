package com.example.apurvchaudhary.cameratest.models;

import java.util.Date;

public class Employee {
    public static Employee instance=null;
    String firstName,secondName,employeePosition,dob, phoneNo;
    int employeeId;
    int score;
    Date scoreStamp;

    public Date getScoreStamp() {
        return scoreStamp;
    }

    public void setScoreStamp(Date scoreStamp) {
        this.scoreStamp = scoreStamp;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    Employee()
{

}

    public static void setInstance(Employee instance) {
        Employee.instance = instance;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public void setEmployeePosition(String employeePosition) {
        this.employeePosition = employeePosition;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public static Employee newInstance(String firstName, String secondName, String employeePosition, String phoneNo, int employeeId, String dob) {
        if(instance!=null) {
            instance.setFirstName(firstName);
            instance.setSecondName(secondName);
            instance.setEmployeeId(employeeId);
            instance.setEmployeePosition(employeePosition);
            instance.setPhoneNo(phoneNo);
            return instance;
        }
       instance=new Employee(firstName, secondName, employeePosition, phoneNo, employeeId, dob);
        return instance;
    }
    public Employee(String firstName, String secondName, String employeePosition, String phoneNo, int employeeId, String date) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.employeePosition = employeePosition;
        this.phoneNo = phoneNo;
       this.dob=date;
        this.employeeId = employeeId;
    }

    public static Employee getInstance() {
        return instance;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getEmployeePosition() {
        return employeePosition;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getDob() {
        return dob;
    }

    public int getEmployeeId() {
        return employeeId;
    }


}

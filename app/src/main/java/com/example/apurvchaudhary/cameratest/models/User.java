package com.example.apurvchaudhary.cameratest.models;

public class User {

    public String firstName;
    public String lastName;
    public String company;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String country;

    public User(){ }

    public User(String company, String country) {
        this.company = company;
        this.country = country;
    }
}

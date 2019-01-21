package com.example.apurvchaudhary.cameratest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class APIFaceData {

    @SerializedName("result")
    @Expose
    private List<Result> result;

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public class Result {

        @SerializedName("person")
        @Expose
        private String person;

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

    }

}

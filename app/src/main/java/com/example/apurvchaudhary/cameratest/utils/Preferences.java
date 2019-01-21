package com.example.apurvchaudhary.cameratest.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    public static void setCompanyName(Context context, String companyName){
        SharedPreferences.Editor preferences = context.getSharedPreferences("adminData", Context.MODE_PRIVATE).edit();
        preferences.putString("companyName", companyName);
        preferences.apply();
    }

    public static String getCompanyName(Context context){
        SharedPreferences preferences = context.getSharedPreferences("adminData", Context.MODE_PRIVATE);
        return preferences.getString("companyName", null);
    }

    public static void setCountryName(Context context, String countryName) {
        SharedPreferences.Editor preferences = context.getSharedPreferences("adminData", Context.MODE_PRIVATE).edit();
        preferences.putString("countryName", countryName);
        preferences.apply();
    }

    public static String getCountryName(Context context){
        SharedPreferences preferences = context.getSharedPreferences("adminData", Context.MODE_PRIVATE);
        return preferences.getString("countryName", null);
    }

    public static void setOrganizationType(Context context, String organizationType){
        SharedPreferences.Editor preferences = context.getSharedPreferences("adminData", Context.MODE_PRIVATE).edit();
        preferences.putString("organizationType", organizationType);
        preferences.apply();
    }

    public static String getOrganizationType(Context context){
        SharedPreferences preferences = context.getSharedPreferences("adminData", Context.MODE_PRIVATE);
        return preferences.getString("organizationType", null);
    }

    public static void setOutHour(Context context, Long outHour) {
        SharedPreferences.Editor preferences = context.getSharedPreferences("adminData", Context.MODE_PRIVATE).edit();
        preferences.putLong("outHour", outHour);
        preferences.apply();
    }

    public static long getOutHour(Context context){
        SharedPreferences preferences = context.getSharedPreferences("adminData", Context.MODE_PRIVATE);
        return preferences.getLong("outHour", 0l);
    }

    public static void setOutMin(Context context, Long outMin) {
        SharedPreferences.Editor preferences = context.getSharedPreferences("adminData", Context.MODE_PRIVATE).edit();
        preferences.putLong("outMin", outMin);
        preferences.apply();
    }

    public static long getOutMin (Context context){
        SharedPreferences preferences = context.getSharedPreferences("adminData", Context.MODE_PRIVATE);
        return preferences.getLong("outMin", 0);
    }

    public static void setInHour(Context context, Long inHour) {
        SharedPreferences.Editor preferences = context.getSharedPreferences("adminData", Context.MODE_PRIVATE).edit();
        preferences.putLong("inHour", inHour);
        preferences.apply();
    }

    public static long getInHour(Context context){
        SharedPreferences preferences = context.getSharedPreferences("adminData", Context.MODE_PRIVATE);
        return preferences.getLong("inHour", 0);
    }

    public static void setInMin(Context context, Long inMin) {
        SharedPreferences.Editor preferences = context.getSharedPreferences("adminData", Context.MODE_PRIVATE).edit();
        preferences.putLong("inMin", inMin);
        preferences.apply();
    }

    public static long getInMin (Context context){
        SharedPreferences preferences = context.getSharedPreferences("adminData", Context.MODE_PRIVATE);
        return preferences.getLong("inMin", 0);
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences("adminData", Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences("adminData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

}

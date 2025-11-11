package com.example.englishapp.utils;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "MyAppPrefs";
    private static SharedPreferences sharedPreferences;

    public static void init(Context context) {
        if(sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    public static void saveToken(String token){
        sharedPreferences.edit().putString("jwt_token", token).apply();
    }

    public static String getToken(){
        return sharedPreferences.getString("jwt_token", null);
    }
}
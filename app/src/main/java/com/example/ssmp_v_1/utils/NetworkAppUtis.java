package com.example.ssmp_v_1.utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.ssmp_v_1.AuthActivity;
import com.example.ssmp_v_1.CallInfoActivity;
import com.example.ssmp_v_1.LineActivity;
import com.example.ssmp_v_1.SettingActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class NetworkAppUtis {


    protected static final String BASE_URL = "http://85.172.11.152:9871";

    public static String getResponseFromURL(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput){
                return scanner.next();
            }else {
                return null;
            }
        }finally {
            urlConnection.disconnect();
        }
    } // Запрос с помощью URL



}


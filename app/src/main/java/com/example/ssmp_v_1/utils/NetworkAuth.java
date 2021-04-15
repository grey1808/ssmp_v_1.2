package com.example.ssmp_v_1.utils;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkAuth extends NetworkAppUtis {
    private static final String ADDRESS = "/auth";
    private static final String LOGIN = "login";
    private static final String PASSWORD = "password";

    public static URL generateURL(
            String login,
            String password
    ) throws MalformedURLException {
        Uri builtUri = Uri.parse(BASE_URL + ADDRESS)
                .buildUpon()
                .appendQueryParameter(LOGIN,login)
                .appendQueryParameter(PASSWORD,password)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    } // Генерация URL
}
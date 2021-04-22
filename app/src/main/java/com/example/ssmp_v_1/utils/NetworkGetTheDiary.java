package com.example.ssmp_v_1.utils;

import android.net.Uri;

import com.example.ssmp_v_1.utils.NetworkAppUtis;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkGetTheDiary extends NetworkAppUtis {
    private static final String CONTROLLER = "/ssmp11";
    private static final String ADDRESS = "/get-diary";
    private static final String  PERSON_ID = "person_id";
    private static final String  CLIENT_ID = "client_id";

    public static URL generateURL(
            String person_id,
            String client_id
    ) throws MalformedURLException {
        Uri builtUri = Uri.parse(BASE_URL + CONTROLLER + ADDRESS)
                .buildUpon()
                .appendQueryParameter(PERSON_ID,person_id)
                .appendQueryParameter(CLIENT_ID,client_id)
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
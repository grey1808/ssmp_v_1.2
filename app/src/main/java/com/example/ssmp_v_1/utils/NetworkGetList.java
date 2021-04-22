package com.example.ssmp_v_1.utils;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkGetList extends NetworkAppUtis {
    private static final String CONTROLLER = "/ssmp11";
    private static final String ADDRESS = "/get-list-and-ssmp";
    private static final String  PERSON_ID = "person_id";
    private static final String  SETDATE = "setDate";

    public static URL generateURL(
            String person_id,
            String setDate
    ) throws MalformedURLException {
        Uri builtUri = Uri.parse(BASE_URL + CONTROLLER + ADDRESS)
                .buildUpon()
                .appendQueryParameter(PERSON_ID,person_id)
                .appendQueryParameter(SETDATE,setDate)
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
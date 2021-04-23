package com.example.ssmp_v_1.utils;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkGetReports extends NetworkAppUtis {
    private static final String CONTROLLER = "/ssmp11";
    private static final String ADDRESS = "/get-reports";
    private static final String  PERSON_ID = "person_id";
    private static final String  DATE_ONE = "date_one";
    private static final String  DATE_TWO = "date_two";
    private static final String  TYPE = "type";
    public static URL generateURL(
            String person_id,
            String date_one,
            String date_two,
            String type
    ) throws MalformedURLException {
        Uri builtUri = Uri.parse(BASE_URL + CONTROLLER + ADDRESS)
                .buildUpon()
                .appendQueryParameter(PERSON_ID,person_id)
                .appendQueryParameter(DATE_ONE,date_one)
                .appendQueryParameter(DATE_TWO,date_two)
                .appendQueryParameter(TYPE,type)
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

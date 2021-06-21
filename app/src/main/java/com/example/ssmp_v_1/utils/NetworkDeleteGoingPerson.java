package com.example.ssmp_v_1.utils;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkDeleteGoingPerson extends NetworkAppUtis {
    private static final String CONTROLLER = "/ssmp11";
    private static final String ADDRESS = "/delete-going-person";
    private static final String PERSON_ID = "person_id";
    private static final String EVENTID = "eventId";

    public static URL generateURL(
            String baseURL,
            String person_id,
            String eventId
    ) throws MalformedURLException {
        Uri builtUri = Uri.parse(baseURL + CONTROLLER + ADDRESS)
                .buildUpon()
                .appendQueryParameter(PERSON_ID, person_id)
                .appendQueryParameter(EVENTID,eventId)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    } // Генерация URL
}
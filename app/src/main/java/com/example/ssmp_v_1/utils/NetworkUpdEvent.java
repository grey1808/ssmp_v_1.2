package com.example.ssmp_v_1.utils;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUpdEvent extends NetworkAppUtis {
    private static final String CONTROLLER = "/ssmp11";
    private static final String ADDRESS = "/upd-event";
    private static final String EVENTID = "eventId";

    public static URL generateURLUpdEvent(
            String eventId
    ) throws MalformedURLException {
        Uri builtUri = Uri.parse(BASE_URL + CONTROLLER + ADDRESS)
                .buildUpon()
                .appendQueryParameter(EVENTID,eventId)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    } // Генерация URL{
}

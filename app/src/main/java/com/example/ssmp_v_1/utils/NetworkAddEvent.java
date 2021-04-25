package com.example.ssmp_v_1.utils;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkAddEvent extends NetworkAppUtis {
    private static final String CONTROLLER = "/ssmp11";
    private static final String ADDRESS = "/add-event";
    private static final String EVENTID = "eventId";
    private static final String CALLNUMBERID = "callNumberId";
    private static final String SSMPRESULT = "ssmpresoult";
    private static final String SSMPRESULT_TEXT = "ssmpresoult_text";
    private static final String NOTE = "note";

    public static URL generateURLAddEvent(
            String baseURL,
            String eventId,
            String callNumberId,
            String ssmpresoult,
            String ssmpresoult_text,
            String note
    ) throws MalformedURLException {
        Uri builtUri = Uri.parse(baseURL + CONTROLLER + ADDRESS)
                .buildUpon()
                .appendQueryParameter(EVENTID,eventId)
                .appendQueryParameter(CALLNUMBERID,callNumberId)
                .appendQueryParameter(SSMPRESULT,ssmpresoult)
                .appendQueryParameter(SSMPRESULT_TEXT,ssmpresoult_text)
                .appendQueryParameter(NOTE,note)
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

package com.example.ssmp_v_1.utils;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkCallInfo extends NetworkAppUtis {
    private static final String CONTROLLER = "/ssmp11";
    private static final String ADDRESS = "/get-call-info";
    private static final String CALLNUMBERID = "callNumberId";

    public static URL generateURLGetCallInfo(
            String callNumberId
    ) throws MalformedURLException {
        Uri builtUri = Uri.parse(BASE_URL + CONTROLLER + ADDRESS)
                .buildUpon()
                .appendQueryParameter(CALLNUMBERID,callNumberId)
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

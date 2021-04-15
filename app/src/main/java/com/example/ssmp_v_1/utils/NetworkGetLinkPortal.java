package com.example.ssmp_v_1.utils;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkGetLinkPortal extends NetworkAppUtis {
    private static final String ADDRESS = "/get-portal";
    private static final String  CLIENT_ID = "client_id";
    private static final String  PERSON_ID = "person_id";

    public static URL generateURL(
            String client_id,
            String person_id
    ) throws MalformedURLException {
        Uri builtUri = Uri.parse(BASE_URL + ADDRESS)
                .buildUpon()
                .appendQueryParameter(CLIENT_ID,client_id)
                .appendQueryParameter(PERSON_ID,person_id)
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
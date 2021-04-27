package com.example.ssmp_v_1.utils;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkGetMkb extends NetworkAppUtis {
    private static final String CONTROLLER = "/ssmp11";
    private static final String ADDRESS = "/get-mkb";

    public static URL generateURL(
            String baseURL
    ) throws MalformedURLException {
        Uri builtUri = Uri.parse(baseURL + CONTROLLER + ADDRESS)
                .buildUpon()
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
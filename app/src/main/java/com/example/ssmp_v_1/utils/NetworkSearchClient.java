package com.example.ssmp_v_1.utils;

import android.net.Uri;

import com.example.ssmp_v_1.utils.NetworkAppUtis;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkSearchClient extends NetworkAppUtis {
    private static final String CONTROLLER = "/ssmp11";
    private static final String ADDRESS = "/search-client";
    private static final String LASTNAME = "lastName";
    private static final String FIRSTNAME = "firstName";
    private static final String PATRNAME = "patrName";
    private static final String BIRTHDATE = "birthDate";
    private static final String SNILS = "snils";

    public static URL generateURLSearchClient(
            String lastName,
            String firstName,
            String patrName,
            String birthDate,
            String snils
    ) throws MalformedURLException {
        Uri builtUri = Uri.parse(BASE_URL + CONTROLLER + ADDRESS)
                .buildUpon()
                .appendQueryParameter(LASTNAME,lastName)
                .appendQueryParameter(FIRSTNAME,firstName)
                .appendQueryParameter(PATRNAME,patrName)
                .appendQueryParameter(BIRTHDATE,birthDate)
                .appendQueryParameter(SNILS,snils)
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
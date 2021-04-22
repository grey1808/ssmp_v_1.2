package com.example.ssmp_v_1.utils;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtilsGetSsmp extends NetworkAppUtis {

    private static final String CONTROLLER = "/ssmp11";
    private static final String ADDRESS = "/get-ssmp-list";
    private static final String CALLNUMBERID = "callNumberId";
    private static final String DATE = "date";
    private static final String FIO = "fio";
    private static final String STATUS = "status";

    public static URL generateURLGetList(
            String et_search_number_call,
            String et_search_date,
            String et_search_fio,
            String et_search_close_event
    ) throws MalformedURLException {
        Uri builtUri = Uri.parse(BASE_URL + CONTROLLER + ADDRESS)
                .buildUpon()
                .appendQueryParameter(CALLNUMBERID,et_search_number_call)
                .appendQueryParameter(DATE,et_search_date)
                .appendQueryParameter(FIO,et_search_fio)
                .appendQueryParameter(STATUS,et_search_close_event)
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

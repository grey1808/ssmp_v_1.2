package com.example.ssmp_v_1.utils;

import android.net.Uri;

import org.json.JSONArray;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkSetAppeal extends NetworkAppUtis {
    private static final String ADDRESS = "/set-appeal";
    private static final String  PERSON_ID = "person_id";
    private static final String  CLIENT_ID = "client_id";
    private static final String  MKB = "mkb";
    private static final String  ORGSTRUCTURE_ID = "orgstructure_id";
    private static final String  JSON = "json";
    private static final String  ACTION_ID = "action_id";

    public static URL generateURLSetAppeal(
            String person_id,
            String client_id,
            String mkb,
            String orgstructure_id,
            String jsonArray,
            String action_id
    ) throws MalformedURLException {
        Uri builtUri = Uri.parse(BASE_URL + ADDRESS)
                .buildUpon()
                .appendQueryParameter(PERSON_ID,person_id)
                .appendQueryParameter(CLIENT_ID,client_id)
                .appendQueryParameter(MKB,mkb)
                .appendQueryParameter(ORGSTRUCTURE_ID,orgstructure_id)
                .appendQueryParameter(JSON,jsonArray)
                .appendQueryParameter(ACTION_ID,action_id)
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

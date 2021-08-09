package com.example.ssmp_v_1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ssmp_v_1.utils.NetworkAddEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.ssmp_v_1.utils.NetworkAppUtis.getResponseFromURL;
import static com.example.ssmp_v_1.utils.NetworkGetTheDiary.generateURL;
import static com.example.ssmp_v_1.utils.NetworkSetAppeal.generateURLSetAppeal;

public class NewAppealActivity extends AppCompatActivity {

    private ProgressBar loadingIndicator;
    private TextView tv_result;
    private TextView tv_error;
    private EditText et_orgstructure_id;
    private EditText et_orgstructure_name;
    private TextView text_diary;
    private TextView et_mkb;
    public String addEvent;
    private Button b_to_send;
    private AutoCompleteTextView ac_tv_mkb;


    private void showResultTextView(){
        tv_result.setVisibility(View.VISIBLE);
        tv_error.setVisibility(View.GONE);
    } // Скрывает результат запроса, показывает результат запроса
    private void showErrorTextView(){
        tv_result.setVisibility(View.GONE);
        tv_error.setVisibility(View.VISIBLE);
    } // Скрывает текст с ошибкой, показывает результат запроса


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_appeal);
        tv_result = findViewById(R.id.tv_result);
        tv_error = findViewById(R.id.tv_error);
        loadingIndicator = findViewById(R.id.pb_loader_indicator);
        et_orgstructure_id = findViewById(R.id.et_orgstructure_id);
        et_orgstructure_name = findViewById(R.id.et_orgstructure_name);
        text_diary = findViewById(R.id.text_diary);
        b_to_send = findViewById(R.id.b_to_send);
        et_mkb = findViewById(R.id.et_mkb);
        ac_tv_mkb = findViewById(R.id.ac_tv_mkb);
        addEvent = null;

        getMkb();
        get_the_diary(); // Получить дневник

        b_to_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // проверка на переменную в хранилище из модуля ССМП
                SharedPreferences new_appeal = getSharedPreferences("new_appeal", MODE_PRIVATE);
                String callNumberId = new_appeal.getString("callNumberId", "");
                String eventId = new_appeal.getString("eventId", "");
                if (callNumberId.trim().length() != 0){
                    addEvent(eventId,callNumberId); // добавить событие к вызову
                }
                String string = ac_tv_mkb.getText().toString();
                if (string == null || string.equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Нет МКБ! Добавьте МКБ и сохраните обращение", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                set_appeal(); // создать обращение
            }
        });

    }
    protected void get_the_diary(){
        URL generatedURL = null;
        // Получение переменной из хранилища
        SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
        String person_id = auth.getString("person_id", "");
        String client_id = getIntent().getExtras().getString("client_id");
        try {

            /*Получить абазовый URL */
            SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
            String baseURL = setting.getString("address", "");

            generatedURL = generateURL(baseURL,person_id,client_id);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new QueryTask().execute(generatedURL);
    } // Получить дневник

    class QueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute(){
            loadingIndicator.setVisibility(View.VISIBLE);
        }


        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = getResponseFromURL(urls[0]);
            }catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response){
//            tv_result.setText(response);
            if (response != null && !response.equals("")){
                try {
                    JSONObject list = new JSONObject(response);
                    Integer status = (Integer) list.get("status");
                    if(status != 0){
                        String jsonArray = list.getString("result");

                        /*Получаем тип услуги*/
                        JSONObject json = new JSONObject(jsonArray);
                        String eventType = json.getString("eventType");
                        JSONObject eventTypeJson = new JSONObject(eventType);
                        String org_id = eventTypeJson.getString("id");
                        String org_name = eventTypeJson.getString("name");
                        et_orgstructure_id.setText(org_id);
                        et_orgstructure_name.setText(org_name);
                        /*Конец Получаем тип услуги*/

                        /*Проверяем есть ли дневник*/
                        Integer status_diary = (Integer) json.getInt("status");
                        if(status_diary != 0){
                            printList(json.getString("diary"));
                            showResultTextView();
                        }else {
                            text_diary.setText(Html.fromHtml(list.getString("message")));
                            showResultTextView();
                        }
                        /*Конец Проверяем есть ли дневник*/

                    }else {
                        showErrorTextView();
                        tv_error.setText(Html.fromHtml(list.getString("message")));
                    }

                } catch (JSONException e) {
                    showErrorTextView();
                    e.printStackTrace();
                }
            }else {
                showErrorTextView();
            }
            loadingIndicator.setVisibility(View.GONE);
        }

        /*Распечатать response*/
        protected void printList(String response) throws JSONException {
            JSONArray jsonArray = new JSONArray(response);
            LinearLayout layout = (LinearLayout) findViewById(R.id.ll_block_diary);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject list = jsonArray.getJSONObject(i);
                TextView label = new TextView(NewAppealActivity.this);
                EditText input = new EditText(NewAppealActivity.this);
                label.setText(list.getString("label"));
                label.setTextSize(20);
                input.setId(list.getInt("id"));
                input.setTextSize(20);
                layout.addView(label);
                layout.addView(input);
            }
        }

    } // для получения дневника

    protected void set_appeal(){

        LinearLayout layout = findViewById(R.id.ll_block_diary);
        ArrayList<String> editTextContent = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        int count = layout.getChildCount();

        JSONObject jsonObject = new JSONObject();
        for(int i =0;i<count;i++)
        {
            View vi = layout.getChildAt(i);
            if(vi instanceof EditText)
            {
                // тут EditText
                EditText s = (EditText) vi;
                Integer id = s.getId();
                String content = s.getText().toString();
                try {
                    jsonObject.put("id", id);
                    jsonObject.put("content", content);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editTextContent.add(""+jsonObject);
                jsonArray.put(""+jsonObject);
            }
        }
        String mkb = et_mkb.getText().toString();
        String mkb_all = ac_tv_mkb.getText().toString().trim();
        String[] mkb_arr = mkb_all.split(" ");
        String mkb_fin = mkb_arr[0].trim();

        String orgstructure_id = et_orgstructure_id.getText().toString();
        String client_id = getIntent().getExtras().getString("client_id");
        String action_id = getIntent().getExtras().getString("action_id");
        // Получение переменной из хранилища
        SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
        String person_id = auth.getString("person_id", "");

        URL generatedURL = null;
        try {
            /*Получить абазовый URL */
            SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
            String baseURL = setting.getString("address", "");

            generatedURL = generateURLSetAppeal(baseURL,person_id,client_id,mkb_fin,orgstructure_id,editTextContent.toString(),action_id);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new QueryTaskSetAppeal().execute(generatedURL);
    };

    // для получения дневника
    class QueryTaskSetAppeal extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute(){
            loadingIndicator.setVisibility(View.VISIBLE);
        }


        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = getResponseFromURL(urls[0]);
            }catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response){
//            tv_result.setText(response);
            if (response != null && !response.equals("")){
                try {
                    JSONObject list = new JSONObject(response);
                    Integer status = (Integer) list.get("status");
                    if(status != 0){
                        String message = list.getString("message");

                        // проверка на переменную в хранилище из модуля ССМП
                        SharedPreferences new_appeal = getSharedPreferences("new_appeal", MODE_PRIVATE);
                        Intent intent = new Intent(NewAppealActivity.this, LineActivity.class);

                        intent.putExtra("request_message", message);
                        if (addEvent == null){
                            addEvent = "";
                        }
                        intent.putExtra("addMessage", addEvent);
                        Toast toast = Toast.makeText(getApplicationContext(),
                                Html.fromHtml(message), Toast.LENGTH_SHORT);
                        toast.show();
                        startActivity(intent);
                        finish();


                    }else {
                        showErrorTextView();
                        tv_error.setText(Html.fromHtml(list.getString("message")));
                    }

                } catch (JSONException e) {
                    showErrorTextView();
                    e.printStackTrace();
                }
            }else {
                showErrorTextView();
            }
            loadingIndicator.setVisibility(View.GONE);
        }

        /*Распечатать response*/
        protected void printList(String response) throws JSONException {
            JSONArray jsonArray = new JSONArray(response);
            LinearLayout layout = (LinearLayout) findViewById(R.id.ll_block_diary);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject list = jsonArray.getJSONObject(i);
                TextView label = new TextView(NewAppealActivity.this);
                EditText input = new EditText(NewAppealActivity.this);
                label.setText(list.getString("label"));
                input.setId(list.getInt("id"));
                layout.addView(label);
                layout.addView(input);
            }
        }

    }


    // Принять вызов 1
    protected void addEvent(String eventId,String callNumberId){
        URL generatedURL = null;
        try {
            String ssmpresoult_text = "Вызов выполнен"; // получить значение
            Integer ssmpresoult = 2; // получить порядковый номер
            String note = "";
            /*Получить абазовый URL */
            SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
            SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
            String baseURL = setting.getString("address", "");
            String person_id = auth.getString("person_id", "");

            generatedURL = NetworkAddEvent.generateURLAddEvent(baseURL,person_id,eventId,callNumberId,"" + ssmpresoult,ssmpresoult_text,note);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//                tv_ssmp_text_message.setText(generatedURL.toString());
        new QueryTaskAddEvent().execute(generatedURL);
    }
    // Принять вызов 2
    class QueryTaskAddEvent extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute(){
            loadingIndicator.setVisibility(View.VISIBLE);
        }


        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = getResponseFromURL(urls[0]);
            }catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response){
//            tv_ssmp_text_message.setText(response);
            String message = null;
            if (response != null && !response.equals("")){
                try {
                    JSONObject list = new JSONObject(response);
                    Integer status = (Integer) list.get("status");
                    message = (String) list.get("message");


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                message = "Ошибка закрытия вызова ССМП! Что то пошло не так..";
            }
            addEvent = message;
            loadingIndicator.setVisibility(View.GONE);
        }

    }

    /*Получить MKB*/
    protected void getMkb() {
        final String[] mrb_array = null;
        SharedPreferences setting = getSharedPreferences("mkb", MODE_PRIVATE);
        String response = setting.getString("response", "");

        if (response != null || !response.equals("")){
            String[] tempstr = response.split(",");
            ac_tv_mkb = findViewById(R.id.ac_tv_mkb);
            ac_tv_mkb.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, tempstr));
        }
    }


}

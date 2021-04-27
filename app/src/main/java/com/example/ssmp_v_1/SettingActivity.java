package com.example.ssmp_v_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.ssmp_v_1.utils.NetworkAppUtis.getResponseFromURL;
import static com.example.ssmp_v_1.utils.NetworkGetMkb.generateURL;

public class SettingActivity  extends AppCompatActivity {

    private EditText et_address;
    private TextView tv_error;
    private Button b_send;
    private ProgressBar pb_loader_indicator;
    private Boolean testConnect;

    private void showResultTextView(){
        tv_error.setVisibility(View.GONE);
    } // Скрывает текст с ошибкой, показывает результат запроса
    private void showErrorTextView(){
        tv_error.setVisibility(View.VISIBLE);
    } // Показывает результат запроса, крывает текст с ошибкой

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        et_address = findViewById(R.id.et_address);
        tv_error = findViewById(R.id.tv_error);
        b_send = findViewById(R.id.b_send);
        pb_loader_indicator = findViewById(R.id.pb_loader_indicator);

        b_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = et_address.getText().toString();
                if (address == "" || address.equals("")){
                    tv_error.setText("Введите Адрес!");
                    showErrorTextView();
                    return;
                }
                if (!URLUtil.isValidUrl(address)){
                    tv_error.setText(Html.fromHtml("Некорректный адрес! Адрес должен быть такого вида: <b>http://123.123.123.123:1111</b>"));
                    showErrorTextView();
                    return;
                }
                // запсиcь переменной
                SharedPreferences sPref = getSharedPreferences("setting", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("address", address.trim());
                ed.commit();


                showResultTextView();
                getMkb();


            }
        });

        // Получить адрес
        SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
        String savedText = setting.getString("address", "");
        if (savedText != null || !savedText.equals("")){
            et_address.setText(savedText);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Boolean test_auth = test_auth();
        if (test_auth){
            SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
            String login = auth.getString("login", "");
            getMenuInflater().inflate(R.menu.menu_main, menu);
            MenuItem dsd = menu.findItem(R.id.main_login);
            dsd.setTitle(login);
            return true;
        }else {
            return  false;
        }
    } // Для меню

    @Override
    // переход на пункт меню
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.main_line:
                new_appeal_clear();
                Intent intent3 = new Intent(SettingActivity.this, LineActivity.class);
                startActivity(intent3);
                finish();
                return true;

            case R.id.main_search_client:
                new_appeal_clear();
                Intent intent2 = new Intent(SettingActivity.this, SearchClientActivity.class);
                startActivity(intent2);
                finish();
                return true;
            case R.id.main_setting:
                new_appeal_clear();
                Intent intent5 = new Intent(SettingActivity.this, SettingActivity.class);
                startActivity(intent5);
                finish();
                return true;
            case R.id.main_reports:
                new_appeal_clear();
                Intent intent4 = new Intent(SettingActivity.this, ReportsActivity.class);
                startActivity(intent4);
                finish();
                return true;
            case R.id.main_exit:
                SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
                auth.edit().remove("person_id").commit();
                String savedText = auth.getString("person_id", "");
                Intent intent = new Intent(SettingActivity.this, AuthActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        //headerView.setText(item.getTitle());
        return super.onOptionsItemSelected(item);
    }

    // очистить переход из модуля ССМП
    protected void new_appeal_clear(){
        SharedPreferences auth = getSharedPreferences("new_appeal", MODE_PRIVATE);
        auth.edit().clear().commit();
    }


    // получить список
    protected void getMkb(){
        URL generatedURL = null;
        try {
            /*Получить базовый URL */
            SharedPreferences auth = getSharedPreferences("setting", MODE_PRIVATE);
            String baseURL = auth.getString("address", "");
            generatedURL = generateURL(baseURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new QueryTask().execute(generatedURL);
    }

    class QueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute(){
            pb_loader_indicator.setVisibility(View.VISIBLE);
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
//            et_search_result.setText(response);
            if (response != null && !response.equals("")){
                String response_str = response.toString();
                try {
                    JSONObject list = new JSONObject(response);
                    Integer status = (Integer) list.get("status");
                    if(status != 0){
                        String jsonArray = list.getString("result");
                        // запиcь переменной
                        SharedPreferences sPref = getSharedPreferences("mkb", MODE_PRIVATE);
                        sPref.edit().clear().commit(); // Очистить переменную
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putString("response", jsonArray);
                        ed.commit();

                        Intent intent = new Intent(SettingActivity.this, LineActivity.class);
                        startActivity(intent);
                        finish();
                        showResultTextView();
                    }else {
                        showErrorTextView();
                    }

                } catch (JSONException e) {
                    showErrorTextView();
                    e.printStackTrace();
                }
            }else {
                tv_error.setText("Ошибка соединения!");
                showErrorTextView();
            }
            pb_loader_indicator.setVisibility(View.GONE);
        }



    }

    protected void  addString(String response){
        JSONArray jsonArray = null; // получаем ответ от сервера
        try {
            jsonArray = new JSONArray(response);
            String response_str = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject list = jsonArray.getJSONObject(i);

                response_str += list.getString("DiagID") + " | " + list.getString("DiagName") + ",";
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // проверка авторизации
    private boolean test_auth(){
        // проверка переменной
        SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
        String savedText = auth.getString("person_id", "");
        if (savedText == null || savedText.equals("")){
            return false;
        }
        return true;
    }

}

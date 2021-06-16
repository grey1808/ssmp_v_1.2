package com.example.ssmp_v_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.example.ssmp_v_1.utils.NetworkAuth.generateURL;
import static com.example.ssmp_v_1.utils.NetworkAppUtis.getResponseFromURL;

public class AuthActivity extends AppCompatActivity {

    private EditText et_login;
    private EditText et_password;
    private TextView tv_error;
    private Button b_send;
    private ProgressBar pb_loader_indicator;

    private void showResultTextView(){
        tv_error.setVisibility(View.GONE);
    } // Скрывает текст с ошибкой, показывает результат запроса
    private void showErrorTextView(){
        tv_error.setVisibility(View.VISIBLE);
    } // Показывает результат запроса, крывает текст с ошибкой

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        test_setting();
        setContentView(R.layout.activity_auth);

        et_login = findViewById(R.id.et_login);
        et_password = findViewById(R.id.et_password);
        tv_error = findViewById(R.id.tv_error);
        b_send = findViewById(R.id.b_send);
        pb_loader_indicator = findViewById(R.id.pb_loader_indicator);




        b_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login = et_login.getText().toString();
                if (login == "" || login.equals("")){
                    tv_error.setText("Введите логин!");
                    showErrorTextView();
                    return;
                }
                getAuth();
            }
        });
    }

    // получить список
    protected void getAuth(){
        URL generatedURL = null;
        try {
            String login = et_login.getText().toString();
            String password = et_password.getText().toString();
            String shaHex = md5(password);

            /*Получить абазовый URL */
            SharedPreferences auth = getSharedPreferences("setting", MODE_PRIVATE);
            String baseURL = auth.getString("address", "");

            generatedURL = generateURL(baseURL,login,shaHex);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new QueryTask().execute(generatedURL);
    }
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            StringBuilder hexString = new StringBuilder();
            for (byte digestByte : md.digest(input.getBytes()))
                hexString.append(String.format("%02X", digestByte));

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
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
                try {
                    JSONObject list = new JSONObject(response);
                    Integer status = (Integer) list.get("status");
                    if(status != 0){
                        String jsonArray = list.getString("result");
                        printList(jsonArray);
                    }else {
                        showErrorTextView();
                    }

                } catch (JSONException e) {
                    showErrorTextView();
                    e.printStackTrace();
                }
            }else {
                tv_error.setText("Что то не так с подключением! Проверьте адрес или повторите попытку позже!");
                showErrorTextView();
            }
            pb_loader_indicator.setVisibility(View.GONE);
        }

        /*Распечатать response*/
        protected void printList(String response) throws JSONException {
            JSONObject list = new JSONObject(response);
            String person_id = list.getString("id");
            String login = list.getString("login");
            String lastName = list.getString("lastName");
            String firstName = list.getString("firstName");
            String patrName = list.getString("patrName");
            String snils = list.getString("SNILS");

            // запсиcь переменной
            SharedPreferences sPref = getSharedPreferences("auth", MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("person_id", person_id);
            ed.putString("login", login);
            ed.putString("lastName", lastName);
            ed.putString("firstName", firstName);
            ed.putString("patrName", patrName);
            ed.putString("snils", snils);
            ed.commit();
            Intent intent = new Intent(AuthActivity.this, LineActivity.class);
            startActivity(intent);
            finish();
        }

    }

    // проверка настройки подключения
    private void test_setting(){
        // проверка переменной
        SharedPreferences auth = getSharedPreferences("setting", MODE_PRIVATE);
        String savedText = auth.getString("address", "");
        if (savedText == null || savedText.equals("")){
            Intent intent = new Intent(AuthActivity.this, SettingActivity.class);
            startActivity(intent);
            finish();
        }
    }


    /*Меню*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
        String login = auth.getString("login", "");
        getMenuInflater().inflate(R.menu.menu_auth, menu);
        return true;
    } // Для меню

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.main_setting:
                new_appeal_clear();
                Intent intent5 = new Intent(AuthActivity.this, SettingActivity.class);
                startActivity(intent5);
                finish();
                return true;
        }
        //headerView.setText(item.getTitle());
        return super.onOptionsItemSelected(item);
    } // переход на пункт меню

    protected void new_appeal_clear(){
        SharedPreferences auth = getSharedPreferences("new_appeal", MODE_PRIVATE);
        auth.edit().clear().commit();
    } // очистить переход из модуля ССМП
    /*Конец меню*/



    }



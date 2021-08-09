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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private TextView tv_title_main;
    private Button b_send;
    private ProgressBar pb_loader_indicator;
    private Boolean testConnect;
    /*Для портала врача*/
    private TextView tv_title_portal_doctor; // Наименовавние для обозначения настроек для портала врача
    private TextView et_wsdl_portal; // Адрес портала врача
    private EditText et_guid; // Гуид разработчика
    private EditText et_idLPU; // Идентификатор ЛПУ в справочнике 64
    private EditText et_url_token; // адрес для получаения токена
    /*Для портала врача*/

    /*Для лицензионного соглашения*/
    private LinearLayout ll_setting;
    private LinearLayout ll_licenses;
    private TextView tv_licenses;
    private TextView b_send_licenses;
    private TextView tv_licenses_warning;
    private CheckBox checkBoxBold ;
    boolean isFlag = false;
    /*Для лицензионного соглашения*/

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
        tv_title_main = findViewById(R.id.tv_title_main);
        tv_error = findViewById(R.id.tv_error);
        b_send = findViewById(R.id.b_send);
        pb_loader_indicator = findViewById(R.id.pb_loader_indicator);
        /*Для портала врача*/
        tv_title_portal_doctor = findViewById(R.id.tv_title_portal_doctor);
        et_wsdl_portal = findViewById(R.id.et_wsdl_portal);
        et_guid = findViewById(R.id.et_guid);
        et_idLPU = findViewById(R.id.et_idLPU);
        et_url_token = findViewById(R.id.et_url_token);
        /*Для портала врача*/



        /*Для лицензионного соглашения*/
        tv_licenses = findViewById(R.id.tv_licenses);
        b_send_licenses = findViewById(R.id.b_send_licenses);
        checkBoxBold = findViewById(R.id.checkBoxBold);
        tv_licenses_warning = findViewById(R.id.tv_licenses_warning);
        /*Для лицензионного соглашения*/
        // Проверить лизензию
        ll_setting = findViewById(R.id.ll_setting);
        ll_licenses = findViewById(R.id.ll_licenses);


        isLicenses();
        String lisenses = " <p>Повседневная практика показывает, что консультация с широким активом играет важную роль в формировании форм развития. Таким образом постоянный количественный рост и сфера нашей активности требуют от нас анализа дальнейших направлений развития. С другой стороны новая модель организационной деятельности представляет собой интересный эксперимент проверки новых предложений. Разнообразный и богатый опыт постоянный количественный рост и сфера нашей активности требуют определения и уточнения модели развития. Равным образом сложившаяся структура организации позволяет оценить значение систем массового участия.</p>\n" +
                "\n" +
                "<p>Значимость этих проблем настолько очевидна, что начало повседневной работы по формированию позиции способствует подготовки и реализации направлений прогрессивного развития. Равным образом укрепление и развитие структуры позволяет оценить значение системы обучения кадров, соответствует насущным потребностям.</p>\n" +
                "\n" +
                "<p>Товарищи! сложившаяся структура организации позволяет оценить значение новых предложений. Повседневная практика показывает, что начало повседневной работы по формированию позиции позволяет оценить значение существенных финансовых и административных условий.</p>\n" +
                "\n" +
                "<p>Повседневная практика показывает, что постоянное информационно-пропагандистское обеспечение нашей деятельности обеспечивает широкому кругу (специалистов) участие в формировании соответствующий условий активизации. Таким образом укрепление и развитие структуры обеспечивает широкому кругу (специалистов) участие в формировании форм развития. Повседневная практика показывает, что рамки и место обучения кадров влечет за собой процесс внедрения и модернизации соответствующий условий активизации. Таким образом сложившаяся структура организации представляет собой интересный эксперимент проверки направлений прогрессивного развития. Товарищи! рамки и место обучения кадров представляет собой интересный эксперимент проверки форм развития. Товарищи! начало повседневной работы по формированию позиции представляет собой интересный эксперимент проверки соответствующий условий активизации.</p>\n" +
                "\n" +
                "<p>С другой стороны консультация с широким активом представляет собой интересный эксперимент проверки существенных финансовых и административных условий. С другой стороны постоянный количественный рост и сфера нашей активности требуют от нас анализа соответствующий условий активизации. Товарищи! укрепление и развитие структуры в значительной степени обуславливает создание форм развития. Значимость этих проблем настолько очевидна, что начало повседневной работы по формированию позиции позволяет выполнять важные задания по разработке соответствующий условий активизации. Таким образом консультация с широким активом требуют от нас анализа системы обучения кадров, соответствует насущным потребностям. Разнообразный и богатый опыт постоянный количественный рост и сфера нашей активности требуют определения и уточнения модели развития. </p>";
        tv_licenses.setText(Html.fromHtml(lisenses));
        // Принять лицензию
        acceptLicenses();



        b_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = et_address.getText().toString();
                String wsdl_portal = et_wsdl_portal.getText().toString().trim();
                String guid = et_guid.getText().toString();
                String idLPU = et_idLPU.getText().toString();
                String url_token = et_url_token.getText().toString();

                if (address == "" || address.equals("")){
                    tv_error.setText("Введите адрес сервиса!");
                    showErrorTextView();
                    return;
                }
                if (!URLUtil.isValidUrl(address)){
                    tv_error.setText(Html.fromHtml("Некорректный адрес основного сервиса! Адрес должен быть такого формата: <b>http://123.123.123.123:1111</b>"));
                    showErrorTextView();
                    return;
                }

                if (wsdl_portal.trim().length() != 0 && !URLUtil.isValidUrl(wsdl_portal)){
                    tv_error.setText(Html.fromHtml("Некорректный адрес портала врача! Адрес должен быть такого формата: <b>http://10.0.1.179/EMK/PixService.svc?wsdl</b>"));
                    showErrorTextView();
                    return;
                }
                if (url_token.trim().length() != 0 && !URLUtil.isValidUrl(url_token)){
                    tv_error.setText(Html.fromHtml("Некорректный адрес токен! Адрес должен быть такого формата: <b>http://10.0.1.179/acs2/acs/connect/token</b>"));
                    showErrorTextView();
                    return;
                }
                // запиcь переменной
                SharedPreferences sPref = getSharedPreferences("setting", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("address", address.trim());
                /*Переменные для портала врача*/
                ed.putString("wsdl_portal", wsdl_portal.trim());
                ed.putString("guid", guid.trim());
                ed.putString("idLPU", idLPU.trim());
                ed.putString("url_token", url_token.trim());
                /*Переменные для портала врача*/
                ed.commit();


                showResultTextView();
                getMkb();


            }
        });

        // Получить адрес
        SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
        String savedText = setting.getString("address", "");

        String wsdl_portal = setting.getString("wsdl_portal", "");
        String guid = setting.getString("guid", "");
        String idLPU = setting.getString("idLPU", "");
        String url_token = setting.getString("url_token", "");

        if (savedText != null || !savedText.equals("")){
            et_address.setText(savedText);
        }
        if (wsdl_portal != null || !wsdl_portal.equals("")){
            et_wsdl_portal.setText(wsdl_portal);
        }
        if (guid != null || !guid.equals("")){
            et_guid.setText(guid);
        }
        if (idLPU != null || !idLPU.equals("")){
            et_idLPU.setText(idLPU);
        }
        if (url_token != null || !url_token.equals("")){
            et_url_token.setText(url_token);
        }
    }

    // проверка на лицензию
    public void isLicenses(){
        // Получить адрес
        SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
        String isLicenses = setting.getString("isLicenses", "");
//        setting.edit().remove("isLicenses").commit(); // очистить отметку о лицензии

        if (isLicenses.equals("true")){
            ll_setting.setVisibility(View.VISIBLE);
            ll_licenses.setVisibility(View.GONE);
            tv_title_main.setText("Основные настройки");
        }else {
            ll_setting.setVisibility(View.GONE);
            ll_licenses.setVisibility(View.VISIBLE);
            tv_title_main.setText("Лицензионное соглашение");
        }
    }

    // Принять лицензию
    public void acceptLicenses(){

        checkBoxBold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // Флажок выбран
                    isFlag = true;
                }else {
                    // Флажок не выбран
                    isFlag = false;
                }
            }
        });

        b_send_licenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // запиcь переменной
                SharedPreferences sPref = getSharedPreferences("setting", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                if (isFlag){
                    ed.putString("isLicenses", "true");
                }else {
                    tv_licenses_warning.setText("Вы должны принять лицензионное соглашение!");
                    tv_licenses_warning.setVisibility(View.VISIBLE);
                    ed.putString("isLicenses", "false");
                }
                ed.commit(); // Записать в хранилище
                isLicenses(); // Проверить лицензию
            }
        });
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
            case R.id.main_about:
                new_appeal_clear();
                Intent intent6 = new Intent(SettingActivity.this, AboutActivity.class);
                startActivity(intent6);
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

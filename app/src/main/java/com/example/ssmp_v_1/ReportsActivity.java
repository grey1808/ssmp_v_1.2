package com.example.ssmp_v_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import static com.example.ssmp_v_1.utils.NetworkAppUtis.getResponseFromURL;
import static com.example.ssmp_v_1.utils.NetworkGetReports.generateURL;

public class ReportsActivity  extends AppCompatActivity {
    private TextView tv_result;
    private TextView tv_error;
    private EditText et_date_one;
    private EditText et_date_two;
    private Button b_send;
    private ProgressBar loader_indicator;
    private DatePicker datePicker;
    private LinearLayout ll_calendar;
    private LinearLayout ll_main_block;
    private Spinner s_type;
    String[] s_list = { "Все врачи", "Текущий врач"}; // Выпадающие список в форме


    private void showResultTextView(){
        tv_result.setVisibility(View.VISIBLE);
        tv_error.setVisibility(View.GONE);
    } // Скрывает текст с ошибкой, показывает результат запроса
    private void showErrorTextView(){
        tv_result.setVisibility(View.GONE);
        tv_error.setVisibility(View.VISIBLE);
    } // Показывает результат запроса, крывает текст с ошибкой

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        test_auth();
        setContentView(R.layout.activity_reports);

        tv_result = findViewById(R.id.tv_result);
        tv_error = findViewById(R.id.tv_error);
        et_date_one = findViewById(R.id.et_date_one);
        et_date_two = findViewById(R.id.et_date_two);
        b_send = findViewById(R.id.b_send);
        loader_indicator = findViewById(R.id.pb_loader_indicator);
        datePicker = findViewById(R.id.datePicker);
        ll_calendar = findViewById(R.id.ll_calendar);
        ll_main_block = findViewById(R.id.ll_main_block);
        s_type = findViewById(R.id.s_type);

        Spinner spinner = (Spinner) findViewById(R.id.s_type);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spiner, s_list);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(1); // установить индекс выпадающего списка по умолчанию

        b_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String date_one = et_date_two.getText().toString();
                String date_two = et_date_two.getText().toString();
                if (date_one.trim().length() == 0){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Сначала выберите дату начала!", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                if (date_two.trim().length() == 0){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Сначала выберите дату окончания!", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                getReports();
            }
        });

        // свернуть каледарь
        ll_main_block.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ll_calendar.setVisibility(View.GONE);
            }
        });

        // Календарь et_date_one
        et_date_one.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ll_calendar.setVisibility(View.VISIBLE);
                setCalendarDateOne();
            }
        });
        // Календарь et_date_two
        et_date_two.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ll_calendar.setVisibility(View.VISIBLE);
                setCalendarDateTwo();
            }
        });

    }
    // получить отчет
    protected void getReports(){
        URL generatedURL = null;
        try {
            String date_one = et_date_one.getText().toString();
            String date_two = et_date_two.getText().toString();
            SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
            String person_id = auth.getString("person_id", "");
            Spinner spinner = (Spinner) findViewById(R.id.s_type);
            String type = ("" + spinner.getSelectedItemPosition());

            /*Получить абазовый URL */
            SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
            String baseURL = setting.getString("address", "");

            generatedURL = generateURL(baseURL,person_id,date_one,date_two,type);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new QueryTask().execute(generatedURL);
    }


    class QueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute(){
            loader_indicator.setVisibility(View.VISIBLE);
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
            if (response != null && !response.equals("")){
                try {
                    JSONObject list = new JSONObject(response);
                    Integer status = (Integer) list.get("status");
                    String message = (String) list.get("message");
                    String result = (String) list.get("result");
                    if(status != 0){
                        showResultTextView();
                        tv_result.setText(Html.fromHtml(result));
                    }else {
                        showResultTextView();
                        tv_result.setText(Html.fromHtml(message));
                    }

                } catch (JSONException e) {
                    showErrorTextView();
                    e.printStackTrace();
                }
            }else {
                showErrorTextView();
            }
            loader_indicator.setVisibility(View.GONE);
        }


    } // для получения дневника


    // проверка авторизации
    private void test_auth(){
        // проверка переменной
        SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
        String savedText = auth.getString("person_id", "");
        if (savedText == null || savedText.equals("")){
            Intent intent = new Intent(ReportsActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
        }




    }
    // Календарь даты 1
    protected void setCalendarDateOne() {
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        Calendar today = Calendar.getInstance();
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                        et_date_one.setText(new StringBuilder()
                                // Месяц отсчитывается с 0, поэтому добавляем 1
                                .append(datePicker.getDayOfMonth()).append(".")
                                .append(datePicker.getMonth() + 1).append(".")
                                .append(datePicker.getYear()));
                        ll_calendar.setVisibility(View.GONE);
                    }
                });
    }
    // Календарь даты 2
    protected void setCalendarDateTwo() {
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        Calendar today = Calendar.getInstance();
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                        et_date_two.setText(new StringBuilder()
                                // Месяц отсчитывается с 0, поэтому добавляем 1
                                .append(datePicker.getDayOfMonth()).append(".")
                                .append(datePicker.getMonth() + 1).append(".")
                                .append(datePicker.getYear()));
                        ll_calendar.setVisibility(View.GONE);
                    }
                });
    }


    /*Меню*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
        String login = auth.getString("login", "");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem dsd = menu.findItem(R.id.main_login);
        dsd.setTitle(login);
        return true;
    } // Для меню

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.main_line:
                new_appeal_clear();
                Intent intent3 = new Intent(ReportsActivity.this, LineActivity.class);
                startActivity(intent3);
                finish();
                return true;
            case R.id.main_search_client:
                new_appeal_clear();
                Intent intent2 = new Intent(ReportsActivity.this, SearchClientActivity.class);
                startActivity(intent2);
                finish();
                return true;
            case R.id.main_setting:
                new_appeal_clear();
                Intent intent5 = new Intent(ReportsActivity.this, SettingActivity.class);
                startActivity(intent5);
                finish();
                return true;

            case R.id.main_reports:
                new_appeal_clear();
                Intent intent4 = new Intent(ReportsActivity.this, ReportsActivity.class);
                startActivity(intent4);
                finish();
                return true;

            case R.id.main_exit:
                SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
                auth.edit().remove("person_id").commit();
                String savedText = auth.getString("person_id", "");
                Intent intent = new Intent(ReportsActivity.this, AuthActivity.class);
                startActivity(intent);
                finish();
                return true;

            case R.id.main_about:
                new_appeal_clear();
                Intent intent6 = new Intent(ReportsActivity.this, AboutActivity.class);
                startActivity(intent6);
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
    /*Меню*/
}

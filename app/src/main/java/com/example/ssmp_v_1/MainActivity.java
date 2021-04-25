package com.example.ssmp_v_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.example.ssmp_v_1.utils.NetworkUtilsGetSsmp.generateURLGetList;
import static com.example.ssmp_v_1.utils.NetworkUtilsGetSsmp.getResponseFromURL;

public class MainActivity extends AppCompatActivity {
    private EditText et_search_number_call; // Номер вызова
    private EditText et_search_date; // Дата вызова
    private EditText et_search_fio; // Фио в вызове
    private Spinner et_search_close_event; // статус вызова
    private Button b_search_clear; // Кнопка очистить
    private Button b_search_send; // Кнопка отправить
    private TextView et_search_result; // Список вызовов
    private TextView et_search_error; // поле для ошибки
    private TableLayout tableLayout; // Таблица начало
    private TextView tv_result; // поле для результата клика на таблицу
    String[] search_ssmp_list = { "Все", "Активные", "Завершенные"}; // Выпадающие список в форме
    private ProgressBar loadingIndicator;
    private DatePicker mDatePicker;
    private LinearLayout ll_calendar;
    private LinearLayout ll_main_block;
    private Menu menu;
    private HorizontalScrollView hsv_horizontal;

    private void showResultTextView(){
        hsv_horizontal.setVisibility(View.VISIBLE);
        et_search_result.setVisibility(View.GONE);
        et_search_error.setVisibility(View.GONE);
    } // Скрывает текст с ошибкой, показывает результат запроса
    private void showErrorTextView(){
        hsv_horizontal.setVisibility(View.GONE);
        et_search_result.setVisibility(View.GONE);
        et_search_error.setVisibility(View.VISIBLE);
    } // Показывает результат запроса, крывает текст с ошибкой
    private void showMessTextView(){
        hsv_horizontal.setVisibility(View.GONE);
        et_search_result.setVisibility(View.VISIBLE);
        et_search_error.setVisibility(View.GONE);
    } // Скрыватет сообщение ошибки




    // Календарь
    protected void setCalendar() {
        mDatePicker = (DatePicker) findViewById(R.id.datePicker);

        Calendar today = Calendar.getInstance();

        mDatePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                        et_search_date.setText(new StringBuilder()
                                // Месяц отсчитывается с 0, поэтому добавляем 1
                                .append(mDatePicker.getDayOfMonth()).append(".")
                                .append(mDatePicker.getMonth() + 1).append(".")
                                .append(mDatePicker.getYear()));
                        ll_calendar.setVisibility(View.GONE);
                    }
                });
    }


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
//            et_search_result.setText(response);
            if (response != null && !response.equals("")){
            try {
                JSONObject list = new JSONObject(response);
                Integer status = (Integer) list.get("status");
                if(status != 0){
                    String jsonArray = list.getString("result");
                    printListTable(jsonArray);
                    showResultTextView();
                }else {
                    TableLayout tblLayout = null;
                    tblLayout = (TableLayout) findViewById(R.id.tableLayout);
                    tblLayout.removeAllViews();
                    showMessTextView();
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
          protected void printListTable(String response){
              try {
                  JSONArray jsonArray = new JSONArray(response); // получаем ответ от сервера
                  TableLayout tblLayout = null;
                  tblLayout = (TableLayout) findViewById(R.id.tableLayout);
                  tblLayout.removeAllViews();

//                  TableRow tr_head = new TableRow(MainActivity.this);
//                  tr_head.setBackgroundColor(Color.GRAY);        // part1
//                  tr_head.setLayoutParams(new TableLayout.LayoutParams(
//                          TableLayout.LayoutParams.MATCH_PARENT,
//                          TableLayout.LayoutParams.WRAP_CONTENT));
//                  TextView th1 = new TextView(MainActivity.this);
//                  TextView th2 = new TextView(MainActivity.this);
//                  TextView th3 = new TextView(MainActivity.this);
//                  TextView th4 = new TextView(MainActivity.this);
//                  th1.setText("1");
//                  th2.setText("1");
//                  th3.setText("1");
//                  th4.setText("1");
//                  tr_head.addView(th1);
//                  tr_head.addView(th2);
//                  tr_head.addView(th3);
//                  tr_head.addView(th4);
//                  tblLayout.addView(tr_head);

                  for (int i = 0; i < jsonArray.length(); i++) {
                      JSONObject list = jsonArray.getJSONObject(i);
                      TableRow tableRow = new TableRow(MainActivity.this);
                      TableRow.LayoutParams llp = new TableRow.LayoutParams(
                              TableLayout.LayoutParams.MATCH_PARENT,
                              TableLayout.LayoutParams.WRAP_CONTENT
                      );


                      TextView textView1 = new TextView(MainActivity.this);
                      TextView textView2 = new TextView(MainActivity.this);
                      TextView textView3 = new TextView(MainActivity.this);
                      TextView textView4 = new TextView(MainActivity.this);
                      TextView textView5 = new TextView(MainActivity.this);
                      textView1.setTextColor(Color.WHITE);
                      textView2.setTextColor(Color.WHITE);
                      textView3.setTextColor(Color.WHITE);
                      textView4.setTextColor(Color.WHITE);
                      textView5.setTextColor(Color.WHITE);
                      textView1.setPadding(10, 10, 10, 10);
                      textView2.setPadding(10, 10, 10, 10);
                      textView3.setPadding(10, 10, 10, 10);
                      textView4.setPadding(10, 10, 10, 10);
                      textView5.setPadding(10, 10, 10, 10);
                      textView1.setTextSize(20);
                      textView2.setTextSize(20);
                      textView3.setTextSize(20);
                      textView4.setTextSize(20);
                      textView5.setTextSize(20);
                      textView1.setText(list.getString("fio"));
                      textView2.setText(list.getString("callNumberId"));
                      textView3.setText(list.getString("contact"));
                      textView4.setText(list.getString("result"));
                      textView5.setText(list.getString("eventId"));
                      tableRow.addView(textView1);
                      tableRow.addView(textView3);
                      tableRow.addView(textView4);
                      tableRow.addView(textView2);
                      tableRow.addView(textView5);
                      Integer isDone = list.getInt("isDone");
//                      Integer status = list.getInt("status");
                      if (isDone == 1){
                          textView1.setTextColor(Color.parseColor("#333333"));
                          textView2.setTextColor(Color.parseColor("#333333"));
                          textView3.setTextColor(Color.parseColor("#333333"));
                          textView4.setTextColor(Color.parseColor("#333333"));
                          textView5.setTextColor(Color.parseColor("#333333"));
                          tableRow.setBackgroundResource(R.drawable.cell_shape_new_yellow);
                      }else {
                          tableRow.setBackgroundResource(R.drawable.call_shape_new_red);
                      }
                      tblLayout.addView(tableRow, i);
                      tableRow.setClickable(true);
                      tableRow.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              TableRow t = (TableRow) v;
                              TextView firstTextView = (TextView) t.getChildAt(3);
                              TextView fourText = (TextView) t.getChildAt(4);
                              String callNumberId = firstTextView.getText().toString();
                              String eventId = fourText.getText().toString();
//                              tv_result.setText(callNumberId);
                              Intent intent = new Intent(MainActivity.this, CallInfoActivity.class);
                              intent.putExtra("callNumberId", callNumberId);
                              intent.putExtra("eventId", eventId);
                              startActivity(intent);
                          }
                      });
                  }
              } catch (JSONException e) {
                  e.printStackTrace();
              }
          }

        }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        test_auth();
        setContentView(R.layout.activity_main);

        Spinner spinner = (Spinner) findViewById(R.id.et_search_close_event);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spiner, search_ssmp_list);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(1); // установить индекс выпадающего списка по умолчанию

        // получаем значение из формы
        et_search_number_call = findViewById(R.id.et_search_number_call);
        et_search_date = findViewById(R.id.et_search_date);
        et_search_fio = findViewById(R.id.et_search_fio);
        et_search_close_event = findViewById(R.id.et_search_close_event);
        b_search_clear = findViewById(R.id.b_search_clear);
        b_search_send = findViewById(R.id.b_search_send);
        et_search_result = findViewById(R.id.et_search_result);
        et_search_error = findViewById(R.id.et_search_error);
        tableLayout = findViewById(R.id.tableLayout);
        tv_result = findViewById(R.id.tv_result);
        loadingIndicator = findViewById(R.id.pb_loader_indicator);
        mDatePicker = findViewById(R.id.datePicker);
        ll_calendar = findViewById(R.id.ll_calendar);
        hsv_horizontal = findViewById(R.id.hsv_horizontal);
        ll_main_block = findViewById(R.id.ll_main_block);

        // Календарь
        et_search_date.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            ll_calendar.setVisibility(View.VISIBLE);
            setCalendar();
            }
        });



        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getList();
            }
        };
        b_search_send.setOnClickListener(onClickListener);
        getList(); // получить список автоматически
        b_search_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search_number_call.setText("");
                et_search_date.setText("");
                et_search_fio.setText("");
                et_search_close_event.setSelection(1);
            }
        });

        // свернуть каледарь
        ll_main_block.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
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
                Intent intent3 = new Intent(MainActivity.this, LineActivity.class);
                startActivity(intent3);
                finish();
                return true;
            case R.id.main_search_client:
                new_appeal_clear();
                Intent intent2 = new Intent(MainActivity.this, SearchClientActivity.class);
                startActivity(intent2);
                finish();
                return true;

            case R.id.main_reports:
                new_appeal_clear();
                Intent intent4 = new Intent(MainActivity.this, ReportsActivity.class);
                startActivity(intent4);
                finish();
                return true;
            case R.id.main_setting:
                new_appeal_clear();
                Intent intent5 = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent5);
                finish();
                return true;

            case R.id.main_exit:
                SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
                auth.edit().remove("person_id").commit();
                String savedText = auth.getString("person_id", "");
                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                startActivity(intent);
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

    // получить список
    protected void getList(){
        URL generatedURL = null;
        try {
            String number = et_search_number_call.getText().toString();
            String date = et_search_date.getText().toString();
            String fio = et_search_fio.getText().toString();
            Spinner mySpinner=(Spinner) findViewById(R.id.et_search_close_event);
//                    String text = mySpinner.getSelectedItem().toString(); // получить значение
            String text = ("" + mySpinner.getSelectedItemPosition()); // получить порядковый номер

//                    String status = (String)et_search_close_event.getItemAtPosition();
//                    String status = et_search_close_event.getText().toString();
            generatedURL = generateURLGetList(number,date,fio,text);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new QueryTask().execute(generatedURL);
    }

    // проверка авторизации
    private void test_auth(){
        // проверка переменной
        SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
        String savedText = auth.getString("person_id", "");
        if (savedText == null || savedText.equals("")){
            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
        }
    }




}
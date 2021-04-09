package com.example.ssmp_v_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

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

    private void showResultTextView(){
        et_search_result.setVisibility(View.GONE);
        et_search_error.setVisibility(View.GONE);
    } // Скрывает текст с ошибкой, показывает результат запроса
    private void showErrorTextView(){
        et_search_result.setVisibility(View.GONE);
        et_search_error.setVisibility(View.VISIBLE);
    } // Показывает результат запроса, крывает текст с ошибкой
    private void showMessTextView(){
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
                      String isDone = list.getString("isDone");
                      if (isDone == "1"){
                          tableRow.setBackgroundResource(R.drawable.cell_shape_new_yellow);
                      }else {
                          tableRow.setBackgroundResource(R.drawable.cell_shape_new_yellow);
                      }


                      TextView textView1 = new TextView(MainActivity.this);
                      TextView textView2 = new TextView(MainActivity.this);
                      TextView textView3 = new TextView(MainActivity.this);
                      TextView textView4 = new TextView(MainActivity.this);
                      textView1.setTextColor(Color.WHITE);
                      textView2.setTextColor(Color.WHITE);
                      textView3.setTextColor(Color.WHITE);
                      textView4.setTextColor(Color.WHITE);
                      textView1.setPadding(10, 10, 10, 10);
                      textView2.setPadding(10, 10, 10, 10);
                      textView3.setPadding(10, 10, 10, 10);
                      textView4.setPadding(10, 10, 10, 10);
                      textView1.setTextSize(20);
                      textView2.setTextSize(20);
                      textView3.setTextSize(20);
                      textView4.setTextSize(20);
                      textView1.setText(list.getString("fio"));
                      textView2.setText(list.getString("callNumberId"));
                      textView3.setText(list.getString("contact"));
                      textView4.setText(list.getString("result"));
                      tableRow.addView(textView1);
                      tableRow.addView(textView3);
                      tableRow.addView(textView4);
                      tableRow.addView(textView2);

                      tblLayout.addView(tableRow, i);
                      tableRow.setClickable(true);
                      tableRow.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              TableRow t = (TableRow) v;
                              TextView firstTextView = (TextView) t.getChildAt(3);
                              String callNumberId = firstTextView.getText().toString();
//                              tv_result.setText(callNumberId);
                              Intent intent = new Intent(MainActivity.this, CallInfoActivity.class);
                              intent.putExtra("callNumberId", callNumberId);
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
        setContentView(R.layout.activity_main);
        Spinner spinner = (Spinner) findViewById(R.id.et_search_close_event);
        // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, search_ssmp_list);
        // Определяем разметку для использования при выборе элемента
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к элементу spinner
        spinner.setAdapter(adapter);

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
//                et_search_result.setText(generatedURL.toString());
                // master
                new QueryTask().execute(generatedURL);
            }
        };
        b_search_send.setOnClickListener(onClickListener);


    }

}
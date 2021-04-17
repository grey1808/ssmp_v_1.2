package com.example.ssmp_v_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ssmp_v_1.utils.NetworkGetList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.example.ssmp_v_1.utils.NetworkAppUtis.getResponseFromURL;
import static com.example.ssmp_v_1.utils.NetworkGetList.generateURL;

public class LineActivity extends AppCompatActivity {

    private EditText et_search_date;
    private DatePicker datePicker;
    private Button b_search_send;
    private TextView et_search_result;
    private TextView et_search_error;
    private TextView tv_new_appeal;
    private ProgressBar pb_loader_indicator;
    private ListView listView;

    private void showResultTextViewList(){
        listView.setVisibility(View.VISIBLE);
        et_search_result.setVisibility(View.GONE);
        et_search_error.setVisibility(View.GONE);
    } // Скрывает текст с ошибкой, показывает результат запроса
    private void showResultTextView(){
        listView.setVisibility(View.GONE);
        et_search_result.setVisibility(View.VISIBLE);
        et_search_error.setVisibility(View.GONE);
        tv_new_appeal.setVisibility(View.GONE);
    }
    private void showErrorTextView(){
        listView.setVisibility(View.GONE);
        et_search_result.setVisibility(View.GONE);
        tv_new_appeal.setVisibility(View.GONE);
        et_search_error.setVisibility(View.VISIBLE);
    } // Показывает результат запроса, крывает текст с ошибкой
    private void showResultTextViewNewAppeal(){
        listView.setVisibility(View.VISIBLE);
        tv_new_appeal.setVisibility(View.VISIBLE);
        et_search_error.setVisibility(View.GONE);
    } // Скрывает текст все, показывает результат сохранения обращения

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);

        et_search_date = findViewById(R.id.et_search_date);
        datePicker = findViewById(R.id.datePicker);
        b_search_send = findViewById(R.id.b_search_send);
        et_search_result = findViewById(R.id.et_search_result);
        et_search_error = findViewById(R.id.et_search_error);
        pb_loader_indicator = findViewById(R.id.pb_loader_indicator);
        listView = findViewById(R.id.listView);
        tv_new_appeal = findViewById(R.id.tv_new_appeal);


        // Календарь
        et_search_date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                datePicker.setVisibility(View.VISIBLE);
                setCalendar();
            }
        });


        // Поиск
        b_search_send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getList();
            }
        });
        getList(); // получить список очереди автоматически
        tv_new_appeal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tv_new_appeal.setVisibility(View.GONE);
            }
        });
        // Если было сохранено обращение
        if (getIntent().getExtras() != null){
            showResultTextViewNewAppeal();
            String request_message = getIntent().getExtras().getString("request_message");
            String addMessage = getIntent().getExtras().getString("addMessage");

            SharedPreferences new_appeal = getSharedPreferences("new_appeal", MODE_PRIVATE);
//            String addMessage = new_appeal.getString("addMessage", "");
            new_appeal.edit().clear().commit();
            if (addMessage.trim().length() != 0){
                tv_new_appeal.setText(Html.fromHtml(request_message + " <hr><br> " + addMessage));
            }else {
                tv_new_appeal.setText(Html.fromHtml(request_message));
            }
        }

    }




    // Календарь
    protected void setCalendar() {
        datePicker = (DatePicker) findViewById(R.id.datePicker);

        Calendar today = Calendar.getInstance();

        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                        et_search_date.setText(new StringBuilder()
                                // Месяц отсчитывается с 0, поэтому добавляем 1
                                .append(datePicker.getDayOfMonth()).append(".")
                                .append(datePicker.getMonth() + 1).append(".")
                                .append(datePicker.getYear()));
                        datePicker.setVisibility(View.GONE);
                    }
                });
    }

    // Получить очередь
    protected void getList(){
        URL generatedURL = null;
        try {
            String setDate = et_search_date.getText().toString();
            // Получаем из хранилища
            SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
            String person_id = auth.getString("person_id", "");
            generatedURL = generateURL(person_id,setDate);
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
            if (response != null && !response.equals("")){
                try {
                    JSONObject list = new JSONObject(response);
                    Integer status = (Integer) list.get("status");
                    String massage = (String) list.get("message");
                    if(status != 0){
                        String jsonArray = list.getString("result");
                        printListSearch(jsonArray);
                        showResultTextViewList();
                    }else {
                        et_search_result.setText(Html.fromHtml(massage));
                        showResultTextView();
                    }

                } catch (JSONException e) {
                    showErrorTextView();
                    e.printStackTrace();
                }
            }else {
                showErrorTextView();
            }
            pb_loader_indicator.setVisibility(View.GONE);
        }

        /*Распечатать response*/
        protected void printListSearch(String response){
            try {
                JSONArray jsonArray = new JSONArray(response); // получаем ответ от сервера
                ListView listView = findViewById(R.id.listView);
                // создаем массив списков
                ArrayList<HashMap<String, Object>> searchList = new ArrayList<>();
                HashMap<String, Object> hashMap;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject list = jsonArray.getJSONObject(i);
                    String client_id = list.getString("client_id");
                    String directionDate = list.getString("directionDate");
                    String fullName = list.getString("fullName");
                    String birthDate = list.getString("birthDate");
                    String sex = list.getString("sex");
                    String snils = list.getString("snils");
                    String registration = list.getString("registration");
                    String residence = list.getString("residence");
                    String contact = list.getString("contact");
                    String time_and_fullName = list.getString("directionDate") + " | " + list.getString("fullName");
                    if (contact == null || contact.equals("") || contact == "1"){
                        contact = "не указан";
                    }
                    if (snils == null || snils.equals("") || snils == "1"){
                        snils = "не указан";
                    }
                    hashMap = new HashMap<>();
                    hashMap.put("client_id", client_id); // Идентификатор
                    hashMap.put("directionDate", directionDate); // Время записи
                    hashMap.put("fullName", fullName); // Полное имя
                    hashMap.put("birthDate", birthDate); // Описание
                    hashMap.put("sex", sex); // Пол
                    hashMap.put("registration", registration); // Регистрация
                    hashMap.put("residence", residence); // проживаение
                    hashMap.put("contact", "тел: " + contact); // Контакты
                    hashMap.put("time_and_fullName", time_and_fullName); // Время записи + полное имя
                    hashMap.put("snils", snils); // Время записи + полное имя
                    searchList.add(hashMap);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            HashMap<String, Object> itemHashMap =
                                    (HashMap<String, Object>) parent.getItemAtPosition(position);
                            String client_id = itemHashMap.get("client_id").toString();
                            String directionDate = itemHashMap.get("directionDate").toString();
                            String fullName = itemHashMap.get("fullName").toString();
                            String birthDate = itemHashMap.get("birthDate").toString();
                            String sex = itemHashMap.get("sex").toString();
                            String registration = itemHashMap.get("registration").toString();
                            String residence = itemHashMap.get("residence").toString();
                            String contact = itemHashMap.get("contact").toString();
                            String time_and_fullName = itemHashMap.get("time_and_fullName").toString();
                            String snils = itemHashMap.get("snils").toString();

                            Intent intent = new Intent(LineActivity.this, ClientInfoActivity.class);
                            intent.putExtra("client_id", client_id);
                            intent.putExtra("directionDate", directionDate);
                            intent.putExtra("fullName", fullName);
                            intent.putExtra("birthDate", birthDate);
                            intent.putExtra("sex", sex);
                            intent.putExtra("registration", registration);
                            intent.putExtra("residence", residence);
                            intent.putExtra("contact", contact);
                            intent.putExtra("time_and_fullName", time_and_fullName);
                            intent.putExtra("snils", snils);
                            startActivity(intent);
                        }
                    });
                }
                SimpleAdapter adapter = new SimpleAdapter(
                        LineActivity.this,
                        searchList,
                        R.layout.list_list,
                        new String[]{
                                "client_id",
                                "directionDate",
                                "fullName",
                                "birthDate",
                                "sex",
                                "registration",
                                "residence",
                                "contact",
                                "time_and_fullName",
                                "snils",
                        },
                        new int[]{
                                R.id.client_id,
                                R.id.directionDate,
                                R.id.fullName,
                                R.id.birthDate,
                                R.id.sex,
                                R.id.registration,
                                R.id.residence,
                                R.id.contact,
                                R.id.time_and_fullName,
                                R.id.snils,
                        });
                // Устанавливаем адаптер для списка
                listView.setAdapter(adapter);



            } catch (JSONException e) {
                showErrorTextView();
                e.printStackTrace();
            }
        }

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
            case R.id.main_activity :
                new_appeal_clear();
                Intent intent1 = new Intent(LineActivity.this, MainActivity.class);
                startActivity(intent1);
                finish();
                return true;
            case R.id.main_line:
                new_appeal_clear();
                Intent intent3 = new Intent(LineActivity.this, LineActivity.class);
                startActivity(intent3);
                finish();
                return true;
            case R.id.main_search_client:
                new_appeal_clear();
                Intent intent2 = new Intent(LineActivity.this, SearchClientActivity.class);
                startActivity(intent2);
                finish();
                return true;

            case R.id.main_exit:
                SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
                auth.edit().remove("person_id").commit();
                String savedText = auth.getString("person_id", "");
                Intent intent = new Intent(LineActivity.this, AuthActivity.class);
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
}


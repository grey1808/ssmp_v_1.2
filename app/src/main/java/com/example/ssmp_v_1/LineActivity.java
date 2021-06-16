package com.example.ssmp_v_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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
    private RecyclerView tweetsRecyclerView;
    private LinearLayout ll_main_block;

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
        Boolean test_auth = test_auth();
        setContentView(R.layout.activity_line);


        et_search_date = findViewById(R.id.et_search_date);
        datePicker = findViewById(R.id.datePicker);
        b_search_send = findViewById(R.id.b_search_send);
        et_search_result = findViewById(R.id.et_search_result);
        et_search_error = findViewById(R.id.et_search_error);
        pb_loader_indicator = findViewById(R.id.pb_loader_indicator);
        listView = findViewById(R.id.listView);
        tv_new_appeal = findViewById(R.id.tv_new_appeal);
        ll_main_block = findViewById(R.id.ll_main_block);


        // Календарь
        et_search_date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                datePicker.setVisibility(View.VISIBLE);
                setCalendar();
            }
        });

        // свернуть каледарь
        ll_main_block.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                datePicker.setVisibility(View.GONE);
            }
        });

        // Поиск
        b_search_send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getList();
            }
        });

        tv_new_appeal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tv_new_appeal.setVisibility(View.GONE);
            }
        });
        // Если было сохранено обращение
        Bundle extars = getIntent().getExtras();
        if ((extars != null) && (extars.containsKey("addMessage"))){
            showResultTextViewNewAppeal();
            String addMessage = getIntent().getExtras().getString("addMessage");
            String request_message = getIntent().getExtras().getString("request_message");


            SharedPreferences new_appeal = getSharedPreferences("new_appeal", MODE_PRIVATE);
//            String addMessage = new_appeal.getString("addMessage", "");
            new_appeal.edit().clear().commit();
            if (addMessage.trim().length() != 0){
                tv_new_appeal.setText(Html.fromHtml(request_message + " <hr><br> " + addMessage));
            }else {
                tv_new_appeal.setText(Html.fromHtml(request_message));
            }
        }else {
            if (test_auth){
                getList(); // получить список очереди автоматически
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

            /*Получить абазовый URL */
            SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
            String baseURL = setting.getString("address", "");

            generatedURL = generateURL(baseURL,person_id,setDate);
        } catch (MalformedURLException e) {
            showErrorTextView();
            e.printStackTrace();
        }
        new QueryTask().execute(generatedURL);
    }
    public static class ViewHolder {
        public TextView textView;
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
                HashMap<String, Object> hashMapAll = new HashMap<>();

                // если вызов ССМП не принят
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
                    String action_id = list.getString("action_id");
                    String status = list.getString("status");
                    String callNumberId = list.getString("callNumberId");
                    String eventId = list.getString("eventId");
                    String type = null;
                    String isDone = list.getString("isDone");


                    if (contact == null || contact.equals("") || contact == "1" || contact == "null"){
                        contact = "не указан";
                    }
                    if (snils == null || snils.equals("") || snils == "1" || contact == "null"){
                        snils = "не указан";
                    }
                    if (callNumberId == null || callNumberId.equals("") || callNumberId == "1" || callNumberId == "null"){
                        type = "НА ДОМ";
                    }else {
                        type = "ССМП";
                    }
                    hashMap = new HashMap<>();
                    hashMap.put("client_id", client_id); // Идентификатор
                    hashMap.put("action_id", action_id); // Идентификатор очереди
                    hashMap.put("directionDate", directionDate); // Время записи
                    hashMap.put("fullName", fullName); // Полное имя
                    hashMap.put("birthDate", birthDate); // дата рождения
                    hashMap.put("sex", sex); // Пол
                    hashMap.put("registration", registration); // Регистрация
                    hashMap.put("residence", residence); // Проживаение
                    hashMap.put("contact", "тел: " + contact); // Контакты
                    hashMap.put("time_and_fullName", time_and_fullName); // Время записи + полное имя
                    hashMap.put("snils", snils); // Время записи + полное имя
                    hashMap.put("status", status); // Статус
                    hashMap.put("callNumberId", callNumberId); // Номер вызова ССМП
                    hashMap.put("eventId", eventId); // Номер события ССМП
                    hashMap.put("type", type); // Тип
                    hashMap.put("isDone", isDone); // Тип
                    // если вызов не выполнен
                    if (status.equals("0") && isDone.equals("null")|| status.equals("1")  && isDone.equals("1")){

                    }else {
                        if (type == "ССМП"){
                            // если не вызов принят
                            if (status.equals("0") && isDone.equals("0")){
                                searchList.add(hashMap);
                            }
                        }
                    }
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
                            String action_id = itemHashMap.get("action_id").toString();
                            String status = itemHashMap.get("status").toString();
                            String callNumberId = itemHashMap.get("callNumberId").toString();
                            String type = itemHashMap.get("type").toString();
                            String isDone = itemHashMap.get("isDone").toString();
                            String eventId = itemHashMap.get("eventId").toString();
                            if (callNumberId != null && callNumberId != "null" ){
                                Intent intent = new Intent(LineActivity.this, CallInfoActivity.class);
                                intent.putExtra("eventId", eventId);
                                intent.putExtra("callNumberId", callNumberId);
                                startActivity(intent);
                            }else {
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
                                intent.putExtra("action_id", action_id);
                                intent.putExtra("status", status);
                                startActivity(intent);
                            }


                        }
                    });

                }
                // если вызов ССМП принят
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
                    String action_id = list.getString("action_id");
                    String status = list.getString("status");
                    String callNumberId = list.getString("callNumberId");
                    String eventId = list.getString("eventId");
                    String type = null;
                    String isDone = list.getString("isDone");


                    if (contact == null || contact.equals("") || contact == "1" || contact == "null"){
                        contact = "не указан";
                    }
                    if (snils == null || snils.equals("") || snils == "1" || contact == "null"){
                        snils = "не указан";
                    }
                    if (callNumberId == null || callNumberId.equals("") || callNumberId == "1" || callNumberId == "null"){
                        type = "НА ДОМ";
                    }else {
                        type = "ССМП";
                    }
                    hashMap = new HashMap<>();
                    hashMap.put("client_id", client_id); // Идентификатор
                    hashMap.put("action_id", action_id); // Идентификатор очереди
                    hashMap.put("directionDate", directionDate); // Время записи
                    hashMap.put("fullName", fullName); // Полное имя
                    hashMap.put("birthDate", birthDate); // дата рождения
                    hashMap.put("sex", sex); // Пол
                    hashMap.put("registration", registration); // Регистрация
                    hashMap.put("residence", residence); // Проживаение
                    hashMap.put("contact", "тел: " + contact); // Контакты
                    hashMap.put("time_and_fullName", time_and_fullName); // Время записи + полное имя
                    hashMap.put("snils", snils); // Время записи + полное имя
                    hashMap.put("status", status); // Статус
                    hashMap.put("callNumberId", callNumberId); // Номер вызова ССМП
                    hashMap.put("eventId", eventId); // Номер события ССМП
                    hashMap.put("type", type); // Тип
                    hashMap.put("isDone", isDone); // Тип
                    // если вызов не выполнен
                    if (status.equals("0") && isDone.equals("null")|| status.equals("1")  && isDone.equals("1")){

                    }else {
                        if (type == "ССМП"){
                            // если не вызов принят
                            if (status.equals("0") && isDone.equals("0")){

                            }else {
                                searchList.add(hashMap);
                            }
                        }
                    }
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
                            String action_id = itemHashMap.get("action_id").toString();
                            String status = itemHashMap.get("status").toString();
                            String callNumberId = itemHashMap.get("callNumberId").toString();
                            String type = itemHashMap.get("type").toString();
                            String isDone = itemHashMap.get("isDone").toString();
                            String eventId = itemHashMap.get("eventId").toString();
                            if (callNumberId != null && callNumberId != "null" ){
                                Intent intent = new Intent(LineActivity.this, CallInfoActivity.class);
                                intent.putExtra("eventId", eventId);
                                intent.putExtra("callNumberId", callNumberId);
                                startActivity(intent);
                            }else {
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
                                intent.putExtra("action_id", action_id);
                                intent.putExtra("status", status);
                                startActivity(intent);
                            }


                        }
                    });

                }
                // Если обычная очередь через МИС
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
                    String action_id = list.getString("action_id");
                    String status = list.getString("status");
                    String callNumberId = list.getString("callNumberId");
                    String eventId = list.getString("eventId");
                    String type = null;
                    String isDone = list.getString("isDone");


                    if (contact == null || contact.equals("") || contact == "1" || contact == "null"){
                        contact = "не указан";
                    }
                    if (snils == null || snils.equals("") || snils == "1" || contact == "null"){
                        snils = "не указан";
                    }
                    if (callNumberId == null || callNumberId.equals("") || callNumberId == "1" || callNumberId == "null"){
                        type = "НА ДОМ";
                    }else {
                        type = "ССМП";
                    }
                    hashMap = new HashMap<>();
                    hashMap.put("client_id", client_id); // Идентификатор
                    hashMap.put("action_id", action_id); // Идентификатор очереди
                    hashMap.put("directionDate", directionDate); // Время записи
                    hashMap.put("fullName", fullName); // Полное имя
                    hashMap.put("birthDate", birthDate); // дата рождения
                    hashMap.put("sex", sex); // Пол
                    hashMap.put("registration", registration); // Регистрация
                    hashMap.put("residence", residence); // Проживаение
                    hashMap.put("contact", "тел: " + contact); // Контакты
                    hashMap.put("time_and_fullName", time_and_fullName); // Время записи + полное имя
                    hashMap.put("snils", snils); // Время записи + полное имя
                    hashMap.put("status", status); // Статус
                    hashMap.put("callNumberId", callNumberId); // Номер вызова ССМП
                    hashMap.put("eventId", eventId); // Номер события ССМП
                    hashMap.put("type", type); // Тип
                    hashMap.put("isDone", isDone); // Тип
                    // если вызов не выполнен
                    if (status.equals("0") && isDone.equals("null")|| status.equals("1")  && isDone.equals("1")){

                    }else {
                        if (type != "ССМП"){
                            searchList.add(hashMap);
                        }
                    }
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
                            String action_id = itemHashMap.get("action_id").toString();
                            String status = itemHashMap.get("status").toString();
                            String callNumberId = itemHashMap.get("callNumberId").toString();
                            String type = itemHashMap.get("type").toString();
                            String isDone = itemHashMap.get("isDone").toString();
                            String eventId = itemHashMap.get("eventId").toString();
                            if (callNumberId != null && callNumberId != "null" ){
                                Intent intent = new Intent(LineActivity.this, CallInfoActivity.class);
                                intent.putExtra("eventId", eventId);
                                intent.putExtra("callNumberId", callNumberId);
                                startActivity(intent);
                            }else {
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
                                intent.putExtra("action_id", action_id);
                                intent.putExtra("status", status);
                                startActivity(intent);
                            }


                        }
                    });

                }
                // если вызов обработан, неважно какой ССМП или через МИС
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
                    String action_id = list.getString("action_id");
                    String status = list.getString("status");
                    String callNumberId = list.getString("callNumberId");
                    String eventId = list.getString("eventId");
                    String type = null;
                    String isDone = list.getString("isDone");


                    if (contact == null || contact.equals("") || contact == "1" || contact == "null"){
                        contact = "не указан";
                    }
                    if (snils == null || snils.equals("") || snils == "1" || contact == "null"){
                        snils = "не указан";
                    }
                    if (callNumberId == null || callNumberId.equals("") || callNumberId == "1" || callNumberId == "null"){
                        type = "НА ДОМ";
                    }else {
                        type = "ССМП";
                    }
                    hashMap = new HashMap<>();
                    hashMap.put("client_id", client_id); // Идентификатор
                    hashMap.put("action_id", action_id); // Идентификатор очереди
                    hashMap.put("directionDate", directionDate); // Время записи
                    hashMap.put("fullName", fullName); // Полное имя
                    hashMap.put("birthDate", birthDate); // дата рождения
                    hashMap.put("sex", sex); // Пол
                    hashMap.put("registration", registration); // Регистрация
                    hashMap.put("residence", residence); // Проживаение
                    hashMap.put("contact", "тел: " + contact); // Контакты
                    hashMap.put("time_and_fullName", time_and_fullName); // Время записи + полное имя
                    hashMap.put("snils", snils); // Время записи + полное имя
                    hashMap.put("status", status); // Статус
                    hashMap.put("callNumberId", callNumberId); // Номер вызова ССМП
                    hashMap.put("eventId", eventId); // Номер события ССМП
                    hashMap.put("type", type); // Тип
                    hashMap.put("isDone", isDone); // Тип
                    // если вызов выполнен
                    if (status.equals("0") && isDone.equals("null") || status.equals("1")  && isDone.equals("1") ){
                        searchList.add(hashMap);
                    }
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
                            String action_id = itemHashMap.get("action_id").toString();
                            String status = itemHashMap.get("status").toString();
                            String callNumberId = itemHashMap.get("callNumberId").toString();
                            String type = itemHashMap.get("type").toString();
                            String isDone = itemHashMap.get("isDone").toString();
                            String eventId = itemHashMap.get("eventId").toString();
                            if (callNumberId != null && callNumberId != "null" ){
                                Intent intent = new Intent(LineActivity.this, CallInfoActivity.class);
                                intent.putExtra("eventId", eventId);
                                intent.putExtra("callNumberId", callNumberId);
                                startActivity(intent);
                            }else {
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
                                intent.putExtra("action_id", action_id);
                                intent.putExtra("status", status);
                                startActivity(intent);
                            }


                        }
                    });

                }

                SimpleAdapter adapter = new SimpleAdapter(
                        LineActivity.this,
                        searchList,
                        R.layout.list_list_table,
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
                                "action_id",
                                "status",
                                "callNumberId",
                                "eventId",
                                "type",
                                "isDone",
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
                                R.id.action_id,
                                R.id.status,
                                R.id.callNumberId,
                                R.id.eventId,
                                R.id.type,
                                R.id.isDone,
                        }){
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textView = (TextView) view.findViewById(R.id.status);
                        String status = (String) textView.getText();
                        TextView isDoneView = (TextView) view.findViewById(R.id.isDone);
                        String isDone = (String) isDoneView.getText();
                        TextView residenceView = (TextView) view.findViewById(R.id.residence);
                        TextView fullNameView = (TextView) view.findViewById(R.id.fullName);
                        TextView tv_status_call = (TextView) view.findViewById(R.id.tv_status_call);
                        TextView typeView = (TextView) view.findViewById(R.id.type);
                        String type = (String) typeView.getText();
                        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll_row);

                        if (status.equals("1") && isDone.equals("null") || status.equals("1")  && isDone.equals("0")){

                            fullNameView.setTextColor(getResources().getColor(R.color.black));
                            residenceView.setTextColor(getResources().getColor(R.color.Danger));
                            tv_status_call.setText("Ожидание");
                            tv_status_call.setVisibility(View.VISIBLE);
                            if (type == "ССМП"){
                                linearLayout.setBackgroundResource(R.drawable.cell_shape_new_yellow);
                                typeView.setTextColor(getResources().getColor(R.color.Red));
                            }else {
                                linearLayout.setBackgroundResource(R.drawable.alert_primary);
                                typeView.setTextColor(getResources().getColor(R.color.Primary));
                            }
                        }
                        else if (status.equals("0") && isDone.equals("null") || status.equals("1")  && isDone.equals("1") ){
                            linearLayout.setBackgroundResource(R.drawable.alert_sussess);
                            fullNameView.setTextColor(getResources().getColor(R.color.black));
                            residenceView.setTextColor(getResources().getColor(R.color.Warning));
                            tv_status_call.setText("Выполнен");
                            tv_status_call.setVisibility(View.VISIBLE);

                            if (type == "ССМП"){
                                typeView.setTextColor(getResources().getColor(R.color.Red));
                            }else {
                                typeView.setTextColor(getResources().getColor(R.color.Primary));
                            }
                        }
                        else if  (status.equals("0") && isDone.equals("0")){
                            linearLayout.setBackgroundResource(R.drawable.call_shape_new_red);
                            fullNameView.setTextColor(getResources().getColor(R.color.black));
                            residenceView.setTextColor(getResources().getColor(R.color.Warning));

                            tv_status_call.setText("Новый вызов");
                            tv_status_call.setVisibility(View.VISIBLE);
                            if (type == "ССМП"){
                                typeView.setTextColor(getResources().getColor(R.color.Yellow));
                            }else {
                                typeView.setTextColor(getResources().getColor(R.color.Primary));
                            }
                        }

                        return view;
                    };
                };
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

            case R.id.main_reports:
                new_appeal_clear();
                Intent intent4 = new Intent(LineActivity.this, ReportsActivity.class);
                startActivity(intent4);
                finish();
                return true;
            case R.id.main_setting:
                new_appeal_clear();
                Intent intent5 = new Intent(LineActivity.this, SettingActivity.class);
                startActivity(intent5);
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

            case R.id.main_about:
                new_appeal_clear();
                Intent intent6 = new Intent(LineActivity.this, AboutActivity.class);
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
    /*Конец меню*/

    // проверка авторизации
    private boolean test_auth(){
        // проверка переменной
        SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
        String savedText = auth.getString("person_id", "");
        if (savedText == null || savedText.equals("")){
            Intent intent = new Intent(LineActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
            return false;
        }
        return true;
    }

}


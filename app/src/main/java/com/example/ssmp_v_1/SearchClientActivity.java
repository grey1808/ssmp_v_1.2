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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.ssmp_v_1.utils.NetworkAppUtis.getResponseFromURL;
import static com.example.ssmp_v_1.utils.NetworkSearchClient.generateURLSearchClient;

public class SearchClientActivity extends AppCompatActivity {

    ProgressBar loadingIndicator;
    TextView tv_result_search;
    TextView et_search_error;
    TextView et_lastName;
    TextView et_firstName;
    TextView et_patrName;
    TextView et_birthDate;
    TextView et_snils;
    Button b_search_clear;
    Button b_search_send;
    ListView listView;



    private void showResultTextView(){
        tv_result_search.setVisibility(View.GONE);
        et_search_error.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    } // Скрывает текст с ошибкой, показывает результат запроса
    private void showErrorTextView(){
        tv_result_search.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        et_search_error.setVisibility(View.VISIBLE);
    } // Показывает результат запроса, крывает текст с ошибкой
    private void showMessTextView(){
        tv_result_search.setVisibility(View.VISIBLE);
        et_search_error.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
    } // Скрыватет сообщение ошибки



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        test_auth();
        setContentView(R.layout.activity_search_client);

        tv_result_search = findViewById(R.id.tv_result_search);
        et_search_error = findViewById(R.id.et_search_error);
        et_lastName = findViewById(R.id.et_lastName);
        et_firstName = findViewById(R.id.et_firstName);
        et_patrName = findViewById(R.id.et_patrName);
        et_birthDate = findViewById(R.id.et_birthDate);
        et_snils = findViewById(R.id.et_snils);
        b_search_clear = findViewById(R.id.b_search_clear);
        b_search_send = findViewById(R.id.b_search_send);
        loadingIndicator = findViewById(R.id.pb_loader_indicator);
        listView = findViewById(R.id.listView);

        b_search_send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getListSearch(); // получить список пациентов
            }
        });
//        getListSearch(); // получить список пациентов автоматически


        // Если было сохранено обращение
//        if (getIntent().getExtras() != null){
//            showMessTextView();
//            String request_message = getIntent().getExtras().getString("request_message");
//            String addMessage = getIntent().getExtras().getString("addMessage");
//
//            SharedPreferences new_appeal = getSharedPreferences("new_appeal", MODE_PRIVATE);
////            String addMessage = new_appeal.getString("addMessage", "");
//            new_appeal.edit().clear().commit();
//            if (addMessage.trim().length() != 0){
//                tv_result_search.setText(Html.fromHtml(request_message + " <hr><br> " + addMessage));
//            }else {
//                tv_result_search.setText(Html.fromHtml(request_message));
//            }
//        }

        // проверка на переменную в хранилище из модуля ССМП
        if_new_appeal();

        b_search_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_lastName.setText("");
                et_firstName.setText("");
                et_patrName.setText("");
                et_birthDate.setText("");
                et_snils.setText("");
            }
        });



 }

    protected void if_new_appeal(){

        // проверка на переменную в хранилище из модуля ССМП
        SharedPreferences new_appeal = getSharedPreferences("new_appeal", MODE_PRIVATE);
        String callNumberId = new_appeal.getString("callNumberId", "");
        String eventId = new_appeal.getString("eventId", "");
        String fio = new_appeal.getString("fio", "");
        if (callNumberId != null || !callNumberId.equals("")){
            if (fio != null || !fio.equals("")){
                String[] subStr;
                String delimeter = " ";
                subStr = fio.split(delimeter);
                JSONObject obj = new JSONObject();
                for(int i = 0; i < subStr.length; i++) {
                    try {
                        obj.put(""+i,subStr[i]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    et_lastName.setText(obj.get("0").toString());
                    et_firstName.setText(obj.get("1").toString());
                    et_patrName.setText(obj.get("2").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

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
    // переход на пункт меню
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){

            case R.id.main_line:
                new_appeal_clear();
                Intent intent3 = new Intent(SearchClientActivity.this, LineActivity.class);
                startActivity(intent3);
                finish();
                return true;

            case R.id.main_search_client:
                new_appeal_clear();
                Intent intent2 = new Intent(SearchClientActivity.this, SearchClientActivity.class);
                startActivity(intent2);
                finish();
                return true;
            case R.id.main_setting:
                new_appeal_clear();
                Intent intent5 = new Intent(SearchClientActivity.this, SettingActivity.class);
                startActivity(intent5);
                finish();
                return true;
            case R.id.main_reports:
                new_appeal_clear();
                Intent intent4 = new Intent(SearchClientActivity.this, ReportsActivity.class);
                startActivity(intent4);
                finish();
                return true;
            case R.id.main_exit:
                SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
                auth.edit().remove("person_id").commit();
                String savedText = auth.getString("person_id", "");
                Intent intent = new Intent(SearchClientActivity.this, AuthActivity.class);
                startActivity(intent);
                finish();
                return true;

            case R.id.main_about:
                new_appeal_clear();
                Intent intent6 = new Intent(SearchClientActivity.this, AboutActivity.class);
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

    protected void getListSearch(){
        URL generatedURL = null;
        try {
            String lastName = et_lastName.getText().toString();
            String firstName = et_firstName.getText().toString();
            String patrName = et_patrName.getText().toString();
            String birthDate = et_birthDate.getText().toString();
            String snils = et_snils.getText().toString();

            /*Получить абазовый URL */
            SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
            String baseURL = setting.getString("address", "");

            generatedURL = generateURLSearchClient(baseURL,lastName,firstName,patrName,birthDate,snils);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//                et_search_result.setText(generatedURL.toString());
        // master
        new QueryTaskSearchclient().execute(generatedURL);
    }

    class QueryTaskSearchclient extends AsyncTask<URL, Void, String> {

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
            tv_result_search.setText(response);
            if (response != null && !response.equals("")){
                try {
                    JSONObject list = new JSONObject(response);
                    Integer status = (Integer) list.get("status");
                    String massage = (String) list.get("message");
                    if(status != 0){
                        String jsonArray = list.getString("result");
                        printListSearch(jsonArray);
                        showResultTextView();
                    }else {
                        tv_result_search.setText(Html.fromHtml(massage));
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
        protected void printListSearch(String response){
            try {
                JSONArray jsonArray = new JSONArray(response); // получаем ответ от сервера
                ListView listView = findViewById(R.id.listView);
                // создаем массив списков
                ArrayList<HashMap<String, Object>> searchList = new ArrayList<>();
                HashMap<String, Object> hashMap;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject list = jsonArray.getJSONObject(i);
                    String fullName = list.getString("lastName") + " " + list.getString("firstName") + " " + list.getString("patrName");
                    String contact = list.getString("contact");
                    String address =
                            list.getString("name_type") + ". " +
                            list.getString("name_city") + " " +
                            list.getString("street_type") + " " +
                            list.getString("street_name") + " " +
                            list.getString("number") + " " +
                            list.getString("corpus");
                    if (contact == null || contact.equals("") || contact == "1"){
                        contact = "не указан";
                    }
                    hashMap = new HashMap<>();
                    hashMap.put("client_id", list.getString("id")); // Идентификатор
                    hashMap.put("snils", list.getString("SNILS"));
                    hashMap.put("fullName", fullName); // Полное имя
                    hashMap.put("birthDate", list.getString("birthDate")); // Описание
                    hashMap.put("contact", "тел: " + contact); // Контакты
                    hashMap.put("address", address); // Адрес
                    searchList.add(hashMap);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            HashMap<String, Object> itemHashMap =
                                    (HashMap<String, Object>) parent.getItemAtPosition(position);
                            String client_id = itemHashMap.get("client_id").toString();
                            String fullName = itemHashMap.get("fullName").toString();
                            String snils = itemHashMap.get("snils").toString();
                            String contact = itemHashMap.get("contact").toString();
                            String address = itemHashMap.get("address").toString();

                            Intent intent = new Intent(SearchClientActivity.this, ClientInfoActivity.class);
                            intent.putExtra("client_id", client_id);
                            intent.putExtra("fullName", fullName);
                            intent.putExtra("contact", contact);
                            intent.putExtra("snils", snils);
                            intent.putExtra("address", address);
                            startActivity(intent);
                        }
                    });
                }
                SimpleAdapter adapter = new SimpleAdapter(
                        SearchClientActivity.this,
                        searchList,
                        R.layout.search_list,
                        new String[]{"id","snils", "fullName", "birthDate", "contact", "address"},
                        new int[]{R.id.client_id,R.id.snils,R.id.fullName, R.id.birthDate, R.id.contact, R.id.address});
                // Устанавливаем адаптер для списка
                listView.setAdapter(adapter);



            } catch (JSONException e) {
                showErrorTextView();
                e.printStackTrace();
            }
        }

    }

    // проверка авторизации
    private void test_auth(){
        // проверка переменной
        SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
        String savedText = auth.getString("person_id", "");
        if (savedText == null || savedText.equals("")){
            Intent intent = new Intent(SearchClientActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
        }
    }

}

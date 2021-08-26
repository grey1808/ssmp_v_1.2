package com.example.ssmp_v_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ssmp_v_1.utils.NetworkAddEvent;
import com.example.ssmp_v_1.utils.NetworkCallInfo;
import com.example.ssmp_v_1.utils.NetworkDeleteGoingPerson;
import com.example.ssmp_v_1.utils.NetworkAddGoingPerson;
import com.example.ssmp_v_1.utils.NetworkUpdEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.ssmp_v_1.utils.NetworkAppUtis.getResponseFromURL;

public class CallInfoActivity extends AppCompatActivity {

    private TextView tv_ssmp_text_message;
    private TextView tv_ssmp_text_error;
    private TextView tv_error;
    private TextView tv_message;
    private EditText et_ssmp_note;
    private ProgressBar loadingIndicator;
    private Button b_to_accept_call;
    private Button b_to_accept_call_and_going;
    private Button b_to_accept_goind;
    private Button b_add_event_ssmp_local;
    private Button b_new_appeal;
    private Button b_to_add_going;
    private Button b_to_delete_going;
    private String fio;
    private LinearLayout ll_block_button;
    private View v_demarcatio_line;
    private boolean flag = false;
    String[] s_list = { "Выберите статус вызова", "Отказ от НП", "Вызов выполнен", "Вызов безрезультатный (снят с НП)", "Назначен ошибочный вызов НП"}; // Выпадающие список в форме


    private void showResultTextView(){
        tv_ssmp_text_message.setVisibility(View.VISIBLE);
        tv_ssmp_text_error.setVisibility(View.GONE);
    } // Скрывает результат запроса, показывает результат запроса
    private void showErrorTextView(){
        tv_ssmp_text_message.setVisibility(View.GONE);
        tv_ssmp_text_error.setVisibility(View.VISIBLE);
    } // Скрывает текст с ошибкой, показывает результат запроса
    private void showResultMessage(){
        tv_message.setVisibility(View.VISIBLE);
        tv_error.setVisibility(View.GONE);
    } // Скрывает текст с ошибкой, показывает результат запроса
    private void showErrorMessage(){
        tv_message.setVisibility(View.GONE);
        tv_error.setVisibility(View.VISIBLE);
    } // Скрывает результат запроса, показывает результат запроса
    private void showAddGoing(){
        b_to_add_going.setVisibility(View.VISIBLE);
        b_to_delete_going.setVisibility(View.GONE);
        ll_block_button.setVisibility(View.GONE);
    } // Скрывает текст с ошибкой, показывает результат запроса
    private void showDeleteGoing(String active_person_id){
        SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
        String person_id = auth.getString("person_id", "");
        if (active_person_id.equals(person_id)){
            b_to_add_going.setVisibility(View.GONE);
            b_to_delete_going.setVisibility(View.VISIBLE);
            ll_block_button.setVisibility(View.VISIBLE);
        }else {
            b_to_delete_going.setVisibility(View.GONE);
            ll_block_button.setVisibility(View.GONE);
        }
    } // Скрывает результат запроса, показывает результат запроса

    private void showGoing(){
        b_to_add_going.setVisibility(View.GONE);
        b_to_delete_going.setVisibility(View.VISIBLE);
        ll_block_button.setVisibility(View.VISIBLE);
    } // Показать о
    private void hiddenGoing(){
        b_to_add_going.setVisibility(View.VISIBLE);
        b_to_delete_going.setVisibility(View.GONE);
        ll_block_button.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_call);

        tv_ssmp_text_message = findViewById(R.id.tv_ssmp_text_message);
        tv_ssmp_text_error = findViewById(R.id.tv_ssmp_text_error);
        loadingIndicator = findViewById(R.id.pb_loader_indicator);
        b_to_accept_call = findViewById(R.id.b_to_accept_call);
        b_to_accept_call_and_going = findViewById(R.id.b_to_accept_call_and_going);
        b_to_accept_goind = findViewById(R.id.b_to_accept_goind);
        b_add_event_ssmp_local = findViewById(R.id.b_add_event_ssmp_local);
        b_to_add_going = findViewById(R.id.b_to_add_going);
        b_to_delete_going = findViewById(R.id.b_to_delete_going);
        tv_error = findViewById(R.id.tv_error);
        tv_message = findViewById(R.id.tv_message);
        et_ssmp_note = findViewById(R.id.et_ssmp_note);
        b_new_appeal = findViewById(R.id.b_new_appeal);
        ll_block_button = findViewById(R.id.ll_block_button);
        v_demarcatio_line = findViewById(R.id.v_demarcatio_line);

        Spinner spinner = (Spinner) findViewById(R.id.s_ssmp_resoult);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.ssmp_result, R.layout.spiner);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);



        String callNumberId = getIntent().getExtras().getString("callNumberId");
        String eventId = getIntent().getExtras().getString("eventId");
        if (callNumberId != null){
            URL generatedURL = null;
            try {
                /*Получить абазовый URL */
                SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                String baseURL = setting.getString("address", "");
                generatedURL = NetworkCallInfo.generateURLGetCallInfo(baseURL,callNumberId);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
//            tv_ssmp_text_message.setText(generatedURL.toString());
            new QueryTask().execute(generatedURL);
        }else {
            tv_ssmp_text_message.setText("Нет переданного номера для получения вызова!");
        }


        // Принять вызов
        b_to_accept_call.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                URL generatedURL = null;
                try {
                    /*Получить абазовый URL */
                    SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                    SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
                    String baseURL = setting.getString("address", "");
                    String person_id = auth.getString("person_id", "");

                    generatedURL = NetworkUpdEvent.generateURLUpdEvent(baseURL,person_id,eventId);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
//                tv_ssmp_text_message.setText(generatedURL.toString());
                new QueryTaskUpdEvent().execute(generatedURL);
            }
        });

        // Добавить событие к вызову
        b_add_event_ssmp_local.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                URL generatedURL = null;
                try {
                    Spinner mySpinner=(Spinner) findViewById(R.id.s_ssmp_resoult);
                    String ssmpresoult_text = mySpinner.getSelectedItem().toString(); // получить значение
                    Integer ssmpresoult = (mySpinner.getSelectedItemPosition()); // получить порядковый номер
                    String note = et_ssmp_note.getText().toString();
                    if (ssmpresoult == 0){
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Сначала выберите результат вызова!", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
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
        });

        // Передать вызов на создание нового обращения
        b_new_appeal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                // запсиcь переменной
                SharedPreferences sPref = getSharedPreferences("new_appeal", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("callNumberId", callNumberId);
                ed.putString("eventId", eventId);
                ed.putString("fio", fio);
                ed.commit();
                Intent intent = new Intent(CallInfoActivity.this, SearchClientActivity.class);
//                intent.putExtra("fio", fio);
                startActivity(intent);
//                finish();
            }
        });

        // Принять вызов и забронировать его
        b_to_accept_call_and_going.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                flag = true;
                URL generatedURL = null;
                URL generatedURLSetGoingPerson = null;
                try {
                    /*Получить абазовый URL */
                    SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                    SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
                    String baseURL = setting.getString("address", "");
                    String person_id = auth.getString("person_id", "");
                    generatedURL = NetworkUpdEvent.generateURLUpdEvent(baseURL,person_id,eventId);
                    generatedURLSetGoingPerson = NetworkAddGoingPerson.generateURL(baseURL,person_id,eventId);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
//                tv_ssmp_text_message.setText(generatedURL.toString());
//                new QueryTaskUpdEvent().execute(generatedURL);
                new QueryTaskUpdEventAndAddGoing().execute(generatedURL);
//                new QueryTaskSetGoingPerson().execute(generatedURLSetGoingPerson);
            }
        });

        // Забронировать вызов
        b_to_add_going.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                URL generatedURLSetGoingPerson = null;
                try {
                    /*Получить абазовый URL */
                    SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                    SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
                    String baseURL = setting.getString("address", "");
                    String person_id = auth.getString("person_id", "");
                    generatedURLSetGoingPerson = NetworkAddGoingPerson.generateURL(baseURL,person_id,eventId);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
//                tv_ssmp_text_message.setText(generatedURL.toString());
                new QueryTaskSetGoingPerson().execute(generatedURLSetGoingPerson);
            }
        });


        // Разбронировать вызов
        b_to_delete_going.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                URL generatedURL = null;
                try {
                    /*Получить абазовый URL */
                    SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                    SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
                    String baseURL = setting.getString("address", "");
                    String person_id = auth.getString("person_id", "");
                    generatedURL = NetworkDeleteGoingPerson.generateURL(baseURL,person_id,eventId);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
//                tv_ssmp_text_message.setText(generatedURL.toString());
                new QueryTaskDeleteGoingPerson().execute(generatedURL);
            }
        });



    }

    // получить информацию о вызове
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
//            tv_ssmp_text_message.setText(response);
            if (response != null && !response.equals("")){
                try {
                    JSONObject list = new JSONObject(response);
                    Integer status = (Integer) list.get("status");
                    String message = (String) list.get("message");
                    if(status != 0){
                        String jsonArray = list.getString("result");
                        printListTable(jsonArray);
                        showResultTextView();
                    }else {
//                        String message = (String) list.get("message");
                        tv_ssmp_text_message.setText(message);
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
                JSONObject list = new JSONObject(response);
                Integer sex = list.getInt("sex");
                String active_person_id = list.getString("active_person_id");
                String fullname_post = list.getString("fullname_post");
                String sex1 = null;
                if (sex == 0){
                    sex1 = "М";
                }else {
                    sex1 = "Ж";
                }
                String result = "<b>Идентификатор вызова: </b>" + list.getString("callNumberId") + "<br>";
                result += "<b>Идентификатор события: </b>"  + list.getString("eventId") + "<br>";
                result += "<b>ФИО: </b>"  + list.getString("fio") + "<br>";
                result += "<b>Адрес: </b>"  + list.getString("address") + "<br>";
                result += "<b>Симптомы: </b>"  + list.getString("occasion") + "<br>";
                result += "<b>Дата вызова: </b>"  + list.getString("callDate") + "<br>";
                result += "<b>Время вызова: </b>"  + list.getString("eventTime") + "<br>";
                result += "<b>Пол: </b>"  + sex1 + "<br>";
                result += "<b>Вид вызова: </b>"  + list.getString("callKind") + "<br>";
                result += "<b>Возраст: </b>"  + list.getString("age") + "<br>";
                result += "<b>ФИО вызывавшего: </b>"  + list.getString("callerName") + "<br>";
                result += "<b>Принявший вызов: </b>"  + list.getString("receiver") + "<br>";
                result += "<b>Контактный телефон: </b>"  + list.getString("contact") + "<br>";
                result += "<b>Категория срочности: </b>"  + list.getString("urgencyCategory") + "<br><hr>";
                fio = list.getString("fio");
                tv_ssmp_text_message.setText(Html.fromHtml(result));
                String status = list.getString("status");
                String isDone = list.getString("isDone");

                if (status.equals("1")){

                    tv_message.setText("Пока этот вызов никто не обслуживает");
                    if (!active_person_id.equals("null")){
                        showDeleteGoing(active_person_id);
                        active_person_id = "Этот вызов обслуживает: <b>" + fullname_post + "</b>";
                        tv_message.setText(Html.fromHtml(active_person_id));
                    }else {
                        showAddGoing();
                    }
                    tv_message.setBackgroundResource(R.color.Warning);
                    tv_message.setTextColor(getResources().getColor(R.color.Warning_text));
                    tv_message.setVisibility(View.VISIBLE);
                    b_to_accept_call.setVisibility(View.GONE);
                    b_to_accept_call_and_going.setVisibility(View.GONE);
                    v_demarcatio_line.setVisibility(View.VISIBLE);
                }else {
                    b_to_accept_call.setVisibility(View.VISIBLE);
                    b_to_accept_call_and_going.setVisibility(View.VISIBLE);
                    ll_block_button.setVisibility(View.GONE);
                }

                if(isDone.equals("1")){
                    tv_message.setText("Этот вызов завершён!");
                    tv_message.setBackgroundResource(R.color.Success);
                    tv_message.setTextColor(getResources().getColor(R.color.Success_text));
                    Spinner s_ssmp_resoult = findViewById(R.id.s_ssmp_resoult);
                    s_ssmp_resoult.setVisibility(View.GONE);
                    et_ssmp_note.setVisibility(View.GONE);
                    b_add_event_ssmp_local.setVisibility(View.GONE);
                    v_demarcatio_line.setVisibility(View.GONE);
                    b_to_add_going.setVisibility(View.GONE);
                    b_to_delete_going.setVisibility(View.GONE);
                    ll_block_button.setVisibility(View.GONE);
                }

                showResultTextView();

            } catch (JSONException e) {
                showErrorTextView();
                e.printStackTrace();
            }
        }

    }
    // Принять вызов
    class QueryTaskUpdEvent extends AsyncTask<URL, Void, String> {

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
            if (response != null && !response.equals("")){
                try {
                    JSONObject list = new JSONObject(response);
                    Integer status = (Integer) list.get("status");
                    String message = (String) list.get("message");
                    if(status != 0){
                        showResultMessage();
                        b_to_accept_call.setVisibility(View.GONE);
                        b_to_accept_call_and_going.setVisibility(View.GONE);
                        tv_message.setText(Html.fromHtml(message));
                        if (!flag){ // если была нажата кнопка забронировать вызов
                            Intent intent = new Intent(CallInfoActivity.this, LineActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }else {
                        showErrorMessage();
                        tv_error.setText(Html.fromHtml(message));
                    }

                } catch (JSONException e) {
                    showErrorMessage();
                    e.printStackTrace();
                }
            }else {
                showErrorMessage();
            }
            loadingIndicator.setVisibility(View.GONE);
        }
    }


    // Принять вызов и выехать
    class QueryTaskUpdEventAndAddGoing extends AsyncTask<URL, Void, String> {

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
            if (response != null && !response.equals("")){
                try {
                    JSONObject list = new JSONObject(response);
                    Integer status = (Integer) list.get("status");
                    String message = (String) list.get("message");
                    if(status != 0){
                        showResultMessage();
                        b_to_accept_call.setVisibility(View.GONE);
                        b_to_accept_call_and_going.setVisibility(View.GONE);
                        tv_message.setText(Html.fromHtml(message));
                        /*Получить абазовый URL */
                        SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                        SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
                        String baseURL = setting.getString("address", "");
                        String person_id = auth.getString("person_id", "");
                        String eventId = getIntent().getExtras().getString("eventId");
                        URL generatedURLSetGoingPerson = null;
                        try {
                            generatedURLSetGoingPerson = NetworkAddGoingPerson.generateURL(baseURL, person_id, eventId);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        new QueryTaskSetGoingPerson().execute(generatedURLSetGoingPerson);
                    }else {
                        showErrorMessage();
                        tv_error.setText(Html.fromHtml(message));
                    }

                } catch (JSONException e) {
                    showErrorMessage();
                    e.printStackTrace();
                }
            }else {
                showErrorMessage();
            }
            loadingIndicator.setVisibility(View.GONE);
        }
    }


    // Забронировать вызов врачом
    class QueryTaskSetGoingPerson extends AsyncTask<URL, Void, String> {

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
            if (response != null && !response.equals("")){
                try {
                    JSONObject list = new JSONObject(response);
                    Integer status = (Integer) list.get("status");
                    String message = (String) list.get("message");
                    if(status != 0){
                        showResultMessage();
                        b_to_accept_call.setVisibility(View.GONE);
                        b_to_accept_call_and_going.setVisibility(View.GONE);
                        tv_message.setText(Html.fromHtml(message));
//                        Intent intent = new Intent(CallInfoActivity.this, LineActivity.class);
//                        startActivity(intent);
//                        finish();
                        showGoing();
                    }else {
                        showErrorMessage();
                        tv_error.setText(Html.fromHtml(message));
                    }

                } catch (JSONException e) {
                    showErrorMessage();
                    e.printStackTrace();
                }
            }else {
                showErrorMessage();
            }
            loadingIndicator.setVisibility(View.GONE);
        }
    }

    // Разбронированть вызов
    class QueryTaskDeleteGoingPerson extends AsyncTask<URL, Void, String> {

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
            if (response != null && !response.equals("")){
                try {
                    JSONObject list = new JSONObject(response);
                    Integer status = (Integer) list.get("status");
                    String message = (String) list.get("message");
                    if(status != 0){
                        showResultMessage();
                        b_to_accept_call.setVisibility(View.GONE);
                        b_to_accept_call_and_going.setVisibility(View.GONE);
                        tv_message.setText(Html.fromHtml(message));
//                        Intent intent = new Intent(CallInfoActivity.this, LineActivity.class);
//                        startActivity(intent);
//                        finish();
                        hiddenGoing();
                    }else {
                        showErrorMessage();
                        tv_error.setText(Html.fromHtml(message));
                    }

                } catch (JSONException e) {
                    showErrorMessage();
                    e.printStackTrace();
                }
            }else {
                showErrorMessage();
            }
            loadingIndicator.setVisibility(View.GONE);
        }
    }



    // Закрыть вызов
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
            if (response != null && !response.equals("")){
                try {
                    JSONObject list = new JSONObject(response);
                    Integer status = (Integer) list.get("status");
                    String message = (String) list.get("message");
                    if(status != 0){
                        tv_message.setVisibility(View.VISIBLE);
                        tv_error.setVisibility(View.GONE);
                        tv_message.setText(Html.fromHtml(message));
                        Intent intent = new Intent(CallInfoActivity.this, LineActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        tv_message.setVisibility(View.GONE);
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(Html.fromHtml(message));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                showErrorMessage();
            }
            loadingIndicator.setVisibility(View.GONE);
        }

    }

}

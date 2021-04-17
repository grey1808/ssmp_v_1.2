package com.example.ssmp_v_1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ssmp_v_1.utils.NetworkAddEvent;
import com.example.ssmp_v_1.utils.NetworkCallInfo;
import com.example.ssmp_v_1.utils.NetworkUpdEvent;

import org.json.JSONArray;
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
    private Button b_add_event_ssmp_local;
    private Button b_new_appeal;
    private String fio;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_call);

        tv_ssmp_text_message = findViewById(R.id.tv_ssmp_text_message);
        tv_ssmp_text_error = findViewById(R.id.tv_ssmp_text_error);
        loadingIndicator = findViewById(R.id.pb_loader_indicator);
        b_to_accept_call = findViewById(R.id.b_to_accept_call);
        b_add_event_ssmp_local = findViewById(R.id.b_add_event_ssmp_local);
        tv_error = findViewById(R.id.tv_error);
        tv_message = findViewById(R.id.tv_message);
        et_ssmp_note = findViewById(R.id.et_ssmp_note);
        b_new_appeal = findViewById(R.id.b_new_appeal);

        Spinner spinner = (Spinner) findViewById(R.id.s_ssmp_resoult);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ssmp_result, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);



        String callNumberId = getIntent().getExtras().getString("callNumberId");
        String eventId = getIntent().getExtras().getString("eventId");
        if (callNumberId != null){
            URL generatedURL = null;
            try {
                generatedURL = NetworkCallInfo.generateURLGetCallInfo(callNumberId);
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
                    generatedURL = NetworkUpdEvent.generateURLUpdEvent(eventId);
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


                    generatedURL = NetworkAddEvent.generateURLAddEvent(eventId,callNumberId,"" + ssmpresoult,ssmpresoult_text,note);


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
//                tv_ssmp_text_message.setText(generatedURL.toString());
                new QueryTaskAddEvent().execute(generatedURL);
            }
        });

        // Передать вызов в создание нового обращения
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
                String result = "<b>Идентификатор вызова: </b>" + list.getString("callNumberId") + "<br>";
                result += "<b>ФИО: </b>"  + list.getString("fio") + "<br>";
                result += "<b>Адрес: </b>"  + list.getString("address") + "<br>";
                result += "<b>Симптомы: </b>"  + list.getString("occasion") + "<br>";
                result += "<b>Дата вызова: </b>"  + list.getString("callDate") + "<br>";
                result += "<b>Время вызова: </b>"  + list.getString("eventTime") + "<br>";
                result += "<b>Пол: </b>"  + list.getString("sex") + "<br>";
                result += "<b>Вид вызова: </b>"  + list.getString("callKind") + "<br>";
                result += "<b>Возраст: </b>"  + list.getString("age") + "<br>";
                result += "<b>ФИО вызывавшего: </b>"  + list.getString("callerName") + "<br>";
                result += "<b>Принявший вызов: </b>"  + list.getString("receiver") + "<br>";
                result += "<b>Контактный телефон: </b>"  + list.getString("contact") + "<br>";
                result += "<b>Категория срочности: </b>"  + list.getString("urgencyCategory") + "<br>";
                fio = list.getString("fio");
                tv_ssmp_text_message.setText(Html.fromHtml(result));
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
                        tv_message.setText(Html.fromHtml(message));
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


    // Принять вызов
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
//            tv_ssmp_text_message.setText(response);
            if (response != null && !response.equals("")){
                try {
                    JSONObject list = new JSONObject(response);
                    Integer status = (Integer) list.get("status");
                    String message = (String) list.get("message");
                    if(status != 0){
                        tv_message.setVisibility(View.VISIBLE);
                        tv_error.setVisibility(View.GONE);
                        tv_message.setText(Html.fromHtml(message));
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

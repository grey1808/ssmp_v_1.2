package com.example.ssmp_v_1;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.ssmp_v_1.utils.NetworkCallInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.ssmp_v_1.utils.NetworkUtilsGetSsmp.getResponseFromURL;

public class CallInfoActivity extends Activity {

    private TextView tv_ssmp_text_message;
    private TextView tv_ssmp_text_error;
    private ProgressBar loadingIndicator;

    private void showResultTextView(){
        tv_ssmp_text_message.setVisibility(View.VISIBLE);
        tv_ssmp_text_error.setVisibility(View.GONE);
    } // Скрывает результат запроса, показывает результат запроса
    private void showErrorTextView(){
        tv_ssmp_text_message.setVisibility(View.GONE);
        tv_ssmp_text_error.setVisibility(View.VISIBLE);
    } // Скрывает текст с ошибкой, показывает результат запроса

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_call);

        tv_ssmp_text_message = findViewById(R.id.tv_ssmp_text_message);
        tv_ssmp_text_error = findViewById(R.id.tv_ssmp_text_error);
        loadingIndicator = findViewById(R.id.pb_loader_indicator);

        Spinner spinner = (Spinner) findViewById(R.id.s_ssmp_resoult);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ssmp_result, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);



        String callNumberId = getIntent().getExtras().getString("callNumberId");
//        tv_ssmp_text_message.setText(callNumberId);
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
                tv_ssmp_text_message.setText(Html.fromHtml(result));
                showResultTextView();

            } catch (JSONException e) {
                showErrorTextView();
                e.printStackTrace();
            }
        }

    }


}

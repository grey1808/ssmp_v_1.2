package com.example.ssmp_v_1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ssmp_v_1.utils.NetworkGetLinkPortal;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.ssmp_v_1.utils.NetworkAppUtis.getResponseFromURL;

public class ClientInfoActivity extends AppCompatActivity {


    private ProgressBar loadingIndicator;
    private ProgressBar pb_loader_indicator_portal;
    private TextView tv_result;
    private TextView et_error;
    private TextView et_error_potral;
    private Button b_new_appeal;
    private Button b_portal;
    private Button b_appointment;
    private String portal_url;

    private void showResultTextView(){
        tv_result.setVisibility(View.VISIBLE);
        et_error.setVisibility(View.GONE);
    } // Скрывает результат запроса, показывает результат запроса
    private void showErrorTextView(){
        tv_result.setVisibility(View.GONE);
        et_error.setVisibility(View.VISIBLE);
    } // Скрывает текст с ошибкой, показывает результат запроса
    private void showResultTextViewPortal(){
        b_portal.setVisibility(View.VISIBLE);
        et_error_potral.setVisibility(View.GONE);
    }
    private void showErrorTextViewPortal(){
        b_portal.setVisibility(View.GONE);
        et_error_potral.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        tv_result = findViewById(R.id.tv_result);
        et_error = findViewById(R.id.et_error);
        loadingIndicator = findViewById(R.id.pb_loader_indicator);
        pb_loader_indicator_portal = findViewById(R.id.pb_loader_indicator_portal);
        b_new_appeal = findViewById(R.id.b_new_appeal);
        b_portal = findViewById(R.id.b_portal);
        et_error_potral = findViewById(R.id.et_error_potral);
        b_appointment = findViewById(R.id.b_appointment);

        String client_id = getIntent().getExtras().getString("client_id");
        String action_id = getIntent().getExtras().getString("action_id");
        String fullName = getIntent().getExtras().getString("fullName");
        String contact = getIntent().getExtras().getString("contact");
        String snils = getIntent().getExtras().getString("snils");
        String address = getIntent().getExtras().getString("address");

        String time_and_fullName = getIntent().getExtras().getString("time_and_fullName");
        String registration = getIntent().getExtras().getString("registration");
        String residence = getIntent().getExtras().getString("residence");
        String sex = getIntent().getExtras().getString("sex");
        String directionDate = getIntent().getExtras().getString("directionDate");


        if (client_id != null){
            String message = null;
            if (time_and_fullName != null){ // Если пршло из Очереди пациентов
                message =
                        "<b>Карточка номер: </b> " + client_id + "<br>" +
                                "<b>Время записи: </b> " + directionDate + "<br>" +
                                "<b>ФИО: </b> " + fullName + "<br>" +
                                "<b>Контактный номер телефона: </b> " + contact + "<br>" +
                                "<b>СНИЛС: </b> " + snils + "<br>" +
                                "<b>Адрес регистрации: </b> " + registration + "<br>" +
                                "<b>Адрес проживания: </b> " + residence + "<br>"
                        ;
            }else { // если пршло из поиска
                message =
                        "<b>Карточка номер: </b> " + client_id + "<br>" +
                                "<b>ФИО: </b> " + fullName + "<br>" +
                                "<b>Контактный номер телефона: </b> " + contact + "<br>" +
                                "<b>СНИЛС: </b> " + snils + "<br>" +
                                "<b>Адрес: </b> " + address + "<br>";
            }
            tv_result.setText(Html.fromHtml(message));

            b_new_appeal.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ClientInfoActivity.this, NewAppealActivity.class);
                    intent.putExtra("client_id", client_id);
                    intent.putExtra("action_id", action_id);
                    startActivity(intent);
                }
            });


            b_portal.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ClientInfoActivity.this, PortalDoctorActivity.class);
                    intent.putExtra("portal_url", portal_url);
                    startActivity(intent);
                }
            });
            b_appointment.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ClientInfoActivity.this, AppointmentActivity.class);
//                    intent.putExtra("portal_url", "portal_url");
                    startActivity(intent);
                }
            });

            getLinkToThePortal(client_id);
        }else {
            showResultTextView();
            tv_result.setText("Нет идентификатора пациента, поробуйте еще раз!");
        }








    }


    protected void getLinkToThePortal(String client_id){
        URL generatedURL = null;
        try {
            // Получение переменной из хранилища
            SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
            String person_id = auth.getString("person_id", "");
            /*Получить абазовый URL */
            SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
            String baseURL = setting.getString("address", "");

            generatedURL = NetworkGetLinkPortal.generateURL(baseURL,client_id,person_id);
        } catch (MalformedURLException e) {
            showErrorTextView();
            e.printStackTrace();
        }
//                tv_ssmp_text_message.setText(generatedURL.toString());
        new QueryTask().execute(generatedURL);
    }
    // получить информацию о вызове
    class QueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute(){
            pb_loader_indicator_portal.setVisibility(View.VISIBLE);
            b_portal.setVisibility(View.GONE);
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
                        portal_url = list.getString("result");
                        showResultTextViewPortal();
                    }else {
                        showErrorTextViewPortal();
                        et_error_potral.setText(Html.fromHtml(message));

                    }

                } catch (JSONException e) {
                    showErrorTextViewPortal();
                    e.printStackTrace();
                }
            }else {
                showErrorTextViewPortal();
            }
            pb_loader_indicator_portal.setVisibility(View.GONE);

        }



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return super.onOptionsItemSelected(item);
    }
}

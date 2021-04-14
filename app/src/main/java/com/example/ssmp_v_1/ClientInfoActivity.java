package com.example.ssmp_v_1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ssmp_v_1.utils.NetworkCallInfo;

import java.net.MalformedURLException;
import java.net.URL;

public class ClientInfoActivity extends Activity {


    private ProgressBar loadingIndicator;
    private TextView tv_result;
    private TextView tv_error;
    private Button new_appeal;

    private void showResultTextView(){
        tv_result.setVisibility(View.VISIBLE);
        tv_error.setVisibility(View.GONE);
    } // Скрывает результат запроса, показывает результат запроса
    private void showErrorTextView(){
        tv_result.setVisibility(View.GONE);
        tv_error.setVisibility(View.VISIBLE);
    } // Скрывает текст с ошибкой, показывает результат запроса

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_info);


        tv_result = findViewById(R.id.tv_result);
        tv_error = findViewById(R.id.tv_error);
        loadingIndicator = findViewById(R.id.pb_loader_indicator);
        new_appeal = findViewById(R.id.new_appeal);

        String client_id = getIntent().getExtras().getString("client_id");
        String fullName = getIntent().getExtras().getString("fullName");
        String contact = getIntent().getExtras().getString("contact");
        String snils = getIntent().getExtras().getString("snils");
        String address = getIntent().getExtras().getString("address");
        if (client_id != null){
            String message =
                    "<p><b>Карточка номер: </b> " + client_id + "<p>" +
                    "<p><b>ФИО: </b> " + fullName + "<p>" +
                    "<p><b>Контактный номер телефона: </b> " + contact + "<p>" +
                    "<p><b>СНИЛС: </b> " + snils + "<p>" +
                    "<p><b>Адрес: </b> " + address + "<p>";
            tv_result.setText(Html.fromHtml(message));

            new_appeal.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ClientInfoActivity.this, NewAppealActivity.class);
                    intent.putExtra("client_id", client_id);
                    startActivity(intent);
                }
            });
        }else {
            showResultTextView();
            tv_result.setText("Нет идентификатора пациента, поробуйте еще раз!");
        }







    }
}

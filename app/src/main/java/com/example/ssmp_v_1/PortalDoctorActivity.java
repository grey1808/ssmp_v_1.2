package com.example.ssmp_v_1;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PortalDoctorActivity extends AppCompatActivity {


    private ProgressBar loadingIndicator;
    private TextView tv_result;
    private TextView tv_error;
    private WebView webView;

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
        setContentView(R.layout.activity_portal_doctor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        webView = findViewById(R.id.wv_webView);
        // включаем поддержку JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        String portal_url = getIntent().getExtras().getString("portal_url");
        // указываем страницу загрузки
        webView.loadUrl(portal_url);
    }

}


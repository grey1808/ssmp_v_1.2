package com.example.ssmp_v_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity  extends AppCompatActivity {

    private EditText et_address;
    private TextView tv_error;
    private Button b_send;
    private ProgressBar pb_loader_indicator;

    private void showResultTextView(){
        tv_error.setVisibility(View.GONE);
    } // Скрывает текст с ошибкой, показывает результат запроса
    private void showErrorTextView(){
        tv_error.setVisibility(View.VISIBLE);
    } // Показывает результат запроса, крывает текст с ошибкой

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        et_address = findViewById(R.id.et_address);
        tv_error = findViewById(R.id.tv_error);
        b_send = findViewById(R.id.b_send);
        pb_loader_indicator = findViewById(R.id.pb_loader_indicator);

        b_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = et_address.getText().toString();
                if (address == "" || address.equals("")){
                    tv_error.setText("Введите Адрес!");
                    showErrorTextView();
                    return;
                }
                // запсиcь переменной
                SharedPreferences sPref = getSharedPreferences("setting", MODE_PRIVATE);
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("address", address);
                ed.commit();
                Intent intent = new Intent(SettingActivity.this, LineActivity.class);
                startActivity(intent);
                finish();
            }
        });


        SharedPreferences auth = getSharedPreferences("setting", MODE_PRIVATE);
        String savedText = auth.getString("address", "");
        if (savedText != null || !savedText.equals("")){
            et_address.setText(savedText);
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
                Intent intent3 = new Intent(SettingActivity.this, LineActivity.class);
                startActivity(intent3);
                finish();
                return true;

            case R.id.main_search_client:
                new_appeal_clear();
                Intent intent2 = new Intent(SettingActivity.this, SearchClientActivity.class);
                startActivity(intent2);
                finish();
                return true;
            case R.id.main_setting:
                new_appeal_clear();
                Intent intent5 = new Intent(SettingActivity.this, SettingActivity.class);
                startActivity(intent5);
                finish();
                return true;
            case R.id.main_reports:
                new_appeal_clear();
                Intent intent4 = new Intent(SettingActivity.this, ReportsActivity.class);
                startActivity(intent4);
                finish();
                return true;
            case R.id.main_exit:
                SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
                auth.edit().remove("person_id").commit();
                String savedText = auth.getString("person_id", "");
                Intent intent = new Intent(SettingActivity.this, AuthActivity.class);
                startActivity(intent);
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

}

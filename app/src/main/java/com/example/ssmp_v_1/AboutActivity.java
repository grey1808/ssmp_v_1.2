package com.example.ssmp_v_1;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AboutActivity extends AppCompatActivity {

    private TextView tv_about_content;
    private TextView tv_error;
    private Button b_download;
    private ProgressBar loader_indicator;

    private void showResultTextView(){
        tv_error.setVisibility(View.GONE);
    } // Скрывает текст с ошибкой, показывает результат запроса
    private void showErrorTextView(){
        tv_error.setVisibility(View.VISIBLE);
    } // Показывает результат запроса, крывает текст с ошибкой


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tv_about_content = findViewById(R.id.tv_about_content);
        tv_error = findViewById(R.id.tv_error);
        b_download = findViewById(R.id.b_download);
        loader_indicator = findViewById(R.id.pb_loader_indicator);


        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        String message = "<p><b>Версия ПО:</b> " + versionName + " </p>";
        tv_about_content.setText(Html.fromHtml(message));

        // обновить
        b_download.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /*Получить абазовый URL */
                SharedPreferences setting = getSharedPreferences("setting", MODE_PRIVATE);
                String baseURL = setting.getString("address", "");
                try {
                    URL generatedURL = null;
//                    generatedURL = new URL(baseURL + "/download");
                    generatedURL = new URL("http://85.172.11.152:9871/ssmp11/download/app-debug.apk");
                    if (haveStoragePermission()){
                        new DowloandTask().execute(generatedURL);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public  boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission error","You have permission");
                return true;
            } else {

                Log.e("Permission error","You have asked for permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //you dont need to worry about these stuff below api level 23
            Log.e("Permission error","You already have the permission");
            return true;
        }
    }

    class DowloandTask extends AsyncTask<URL, Void, Void>{

        @Override
        protected void onPreExecute(){
            loader_indicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(URL... urls) {
            URL url = urls[0];
            Download(url);
            return null;
        }

        protected void Download(URL url){

            //check whether Sdcard present or not
            Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
            if(isSDPresent)
            {
                // yes SD-card is present
//                String directory = "/her";
                String directory = "/" + Environment.DIRECTORY_DOWNLOADS;
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + directory;

                File dir = new File(path);
                if(!dir.exists())           //check if not created then create the firectory
                    dir.mkdirs();
            }




            String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
            String fileName = "app-debug.apk";
            destination += fileName;
            final Uri uri = Uri.parse("file://" + destination);

            // Delete update file if exists Удалить файл обновления, если он существует
            File file = new File(destination);
            if (file.exists())
                //file.delete() - test this, I think sometimes it doesnt work
                file.delete();


            //set downloadmanager установить менеджер загрузок
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(String.valueOf(url)));
            request.setDescription(AboutActivity.this.getString(R.string.app_name));
            request.setTitle(AboutActivity.this.getString(R.string.app_name));
//
//            //set destination установить пункт назначения
            request.setDestinationUri(uri);

            // get download service and enqueue file получаем службу загрузки и помещаем файл в очередь
            final DownloadManager manager = (DownloadManager) getSystemService(AboutActivity.DOWNLOAD_SERVICE);
            final long downloadId = manager.enqueue(request);






            //set BroadcastReceiver to install app when .apk is downloaded установите BroadcastReceiver для установки приложения при загрузке .apk
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.setDataAndType(uri,manager.getMimeTypeForDownloadedFile(downloadId));

                    startActivity(install);
                    unregisterReceiver(this);
                    finish();

//                    String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
//                    String fileName = "app-debug.apk";
//                    destination += fileName;
//
//                    File apkFile = new File(destination);
//                    Intent webIntent = new Intent(Intent.ACTION_VIEW);
//                    webIntent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
//                    webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(webIntent);    //ActivityNotFoundException

                }
            };




            //register receiver for when .apk download is compete зарегистрируйте получателя, когда загрузка .apk будет завершена
            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }




        public class GenericFileProvider extends FileProvider {

        }
        protected void onPostExecute(){

            loader_indicator.setVisibility(View.GONE);
        }
    }






    public void saveUrl(final String filename, final String urlString)
            throws MalformedURLException, IOException {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL(urlString).openStream());
            fout = new FileOutputStream(filename);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }















    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Boolean test_auth = test_auth();
        if (test_auth){
            SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
            String login = auth.getString("login", "");
            getMenuInflater().inflate(R.menu.menu_main, menu);
            MenuItem dsd = menu.findItem(R.id.main_login);
            dsd.setTitle(login);
            return true;
        }else {
            return  false;
        }
    } // Для меню

    @Override
    // переход на пункт меню
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.main_line:
                new_appeal_clear();
                Intent intent3 = new Intent(AboutActivity.this, LineActivity.class);
                startActivity(intent3);
                finish();
                return true;

            case R.id.main_search_client:
                new_appeal_clear();
                Intent intent2 = new Intent(AboutActivity.this, SearchClientActivity.class);
                startActivity(intent2);
                finish();
                return true;
            case R.id.main_setting:
                new_appeal_clear();
                Intent intent5 = new Intent(AboutActivity.this, SettingActivity.class);
                startActivity(intent5);
                finish();
                return true;
            case R.id.main_reports:
                new_appeal_clear();
                Intent intent4 = new Intent(AboutActivity.this, ReportsActivity.class);
                startActivity(intent4);
                finish();
                return true;
            case R.id.main_exit:
                SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
                auth.edit().remove("person_id").commit();
                String savedText = auth.getString("person_id", "");
                Intent intent = new Intent(AboutActivity.this, AuthActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.main_about:
                new_appeal_clear();
                Intent intent6 = new Intent(AboutActivity.this, AboutActivity.class);
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

    // проверка авторизации
    private boolean test_auth(){
        // проверка переменной
        SharedPreferences auth = getSharedPreferences("auth", MODE_PRIVATE);
        String savedText = auth.getString("person_id", "");
        if (savedText == null || savedText.equals("")){
            return false;
        }
        return true;
    }
}

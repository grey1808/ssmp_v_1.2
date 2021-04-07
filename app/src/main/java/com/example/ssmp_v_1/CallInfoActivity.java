package com.example.ssmp_v_1;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class CallInfoActivity extends Activity {

    private TextView ssmp_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_call);

        ssmp_text = findViewById(R.id.ssmp_text);

        int callNumberId = getIntent().getIntExtra("callNumberId", 0);
        ssmp_text.setText(callNumberId);
    }
}

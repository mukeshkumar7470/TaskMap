package com.mukeshkpdeveloper.taskmap.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mukeshkpdeveloper.taskmap.R;

public class UserDetsailActivity extends AppCompatActivity {
    LinearLayout liBack;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detsail);

        liBack = findViewById(R.id.li_back);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        liBack.setOnClickListener(view -> finish());

        if (getIntent().getStringExtra("NAME") != null && getIntent().getStringExtra("EMAIL") != null) {
            TextView tv_fullName = findViewById(R.id.tv_fullName);
            TextView tvEmail = findViewById(R.id.tv_email);
            tv_fullName.setText(getIntent().getStringExtra("NAME"));
            tvEmail.setText(getIntent().getStringExtra("EMAIL"));
        }

    }

}
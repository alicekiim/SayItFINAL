package com.example.siateacher.Student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.siateacher.LoginActivity;
import com.example.siateacher.R;
import com.example.siateacher.RegisterActivity;

public class TeacherLoginRegActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mToolbar;

    private Button mRegBtn;
    private Button mLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login_reg);

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Say It App - Teacher Area");

        mRegBtn = (Button) findViewById(R.id.startPage_regButton);
        mLoginBtn = (Button) findViewById(R.id.startPage_loginButton);

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent reg_intent = new Intent(TeacherLoginRegActivity.this, RegisterActivity.class);
                startActivity(reg_intent);

            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent login_intent = new Intent(TeacherLoginRegActivity.this, LoginActivity.class);
                startActivity(login_intent);

            }
        });
    }
}

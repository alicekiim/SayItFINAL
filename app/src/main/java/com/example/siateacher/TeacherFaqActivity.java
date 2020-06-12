package com.example.siateacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class TeacherFaqActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar toolB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_faq);

        //create toolbar
        toolB = (Toolbar) findViewById(R.id.faq_page_toolbar);
        setSupportActionBar(toolB);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("SayIt! - FAQ's");


    }
}

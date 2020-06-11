package com.example.siateacher.Student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.example.siateacher.R;

public class StudentFaqActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_faq);

        //create toolbar
        mToolbar = (Toolbar) findViewById(R.id.student_faq_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("SayIt! - Student FAQ's");
    }
}

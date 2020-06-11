package com.example.siateacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class TeacherFaqActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_faq);

        //create toolbar
        mToolbar = (Toolbar) findViewById(R.id.faq_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("SayIt! - FAQ's");

       /*

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back_intent = new Intent(TeacherFaqActivity.this, TeacherMainActivity.class);
                startActivity(back_intent);
            }
        });

        */

    }
}

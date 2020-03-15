package com.example.siateacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class createQuizActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mToolbar;

    private Button mSaveQuizButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        mToolbar = (Toolbar) findViewById(R.id.createQuizPage_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Say It App - Activity Area");

        mSaveQuizButton = (Button) findViewById(R.id.createQuizPage_saveQuizButton);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back_intent = new Intent(createQuizActivity.this, quizActivity.class);
                startActivity(back_intent);
            }
        });

        mSaveQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent save_intent = new Intent(createQuizActivity.this, quizActivity.class);
                startActivity(save_intent);

            }
        });
    }
}

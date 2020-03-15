package com.example.siateacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class quizActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mToolbar;

    private Button mCreateQuizButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        mToolbar = (Toolbar) findViewById(R.id.quizPage_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Say It App - Activity Area");

        mCreateQuizButton = (Button) findViewById(R.id.quizPage_createQuizButton);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back_intent = new Intent(quizActivity.this, MainActivity.class);
                startActivity(back_intent);
            }
        });

        mCreateQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent createQuiz_intent = new Intent(quizActivity.this, createQuizActivity.class);
                startActivity(createQuiz_intent);

            }
        });

    }
}

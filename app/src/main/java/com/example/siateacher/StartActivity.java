package com.example.siateacher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.siateacher.Student.StudentMainActivity;
import com.example.siateacher.Student.TeacherLoginRegActivity;

public class StartActivity extends AppCompatActivity {

        private Button mTeacher;
        private Button mStudent;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_start);

            mTeacher = (Button) findViewById(R.id.iam_teacher);
            mStudent = (Button) findViewById(R.id.iam_student);

            mTeacher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent reg_intent = new Intent(StartActivity.this, TeacherLoginRegActivity.class);
                    startActivity(reg_intent);

                }
            });

            mStudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent login_intent = new Intent(StartActivity.this, StudentMainActivity.class);
                    startActivity(login_intent);

                }
            });

        }
}


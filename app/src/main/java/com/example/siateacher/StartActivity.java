package com.example.siateacher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.siateacher.Student.StudentMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    private Button mTeacher;
    private Button mStudent;
    /////////////////////////////////////////
    private FirebaseUser firebaseUser;


    // @Override
    public void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null) {
            Toast.makeText(getApplicationContext(), firebaseUser.getUid(), Toast.LENGTH_SHORT).show();
            if (firebaseUser.isAnonymous()) {

                Intent Smain_intent = new Intent(StartActivity.this, StudentMainActivity.class);
                startActivity(Smain_intent);

                //Toast.makeText(getApplicationContext(), "stm", Toast.LENGTH_SHORT).show();
            }else{
                Intent Tmain_intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(Tmain_intent);

                //Toast.makeText(getApplicationContext(), "m", Toast.LENGTH_SHORT).show();
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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


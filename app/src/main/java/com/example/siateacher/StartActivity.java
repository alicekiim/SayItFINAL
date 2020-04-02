package com.example.siateacher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.siateacher.Student.StudentMainActivity;
import com.example.siateacher.TeacherLoginRegActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    private Button mTeacher;
    private Button mStudent;
    private FirebaseUser firebaseUser;

    // @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser != null) {//사용자 정보가 남아 있을경우 로그인화면이 아닌 로그인된 화면을 불러온다
            Toast.makeText(getApplicationContext(), firebaseUser.getUid(), Toast.LENGTH_SHORT).show();
            if (firebaseUser.isAnonymous()) {//익명사용자체크(익명사용자면 학생화면,아니면 선생화면)
                //backToStartPage();
                Intent Smain_intent = new Intent(StartActivity.this, StudentMainActivity.class);
                startActivity(Smain_intent);
            }else{
                Intent Tmain_intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(Tmain_intent);
            }
        }
    }

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
                //finish();

            }
        });

        mStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent login_intent = new Intent(StartActivity.this, StudentMainActivity.class);
                startActivity(login_intent);
                //finish();

            }
        });

    }
}

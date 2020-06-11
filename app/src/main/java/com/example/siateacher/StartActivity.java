// Code adapted from tutorial 'Chat App with Firebase' by KODDev.
// Tutorial found at: https://www.youtube.com/watch?v=LyAmpfm4ndo&list=PLzLFqCABnRQftQQETzoVMuteXzNiXmnj8&index=3

package com.example.siateacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.siateacher.Student.StudentMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

public class StartActivity extends AppCompatActivity {

    //create variables
    private Button mTeacher;
    private Button mStudent;
    private FirebaseUser mUser;

    // @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mUser != null){
            //To retrieve the ID token from the client, make sure the user is signed in and then get the ID token from the signed-in user

            //Check if the user is logged in.
            mUser.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                String idToken = task.getResult().getToken();

                                if (mUser.isAnonymous()) {
                                    //Anonymous user check
                                    //show student screen if anonymous user..
                                    Intent Smain_intent = new Intent(StartActivity.this, StudentMainActivity.class);
                                    startActivity(Smain_intent);
                                    finish();
                                }else{ // if not anonymous, show teacher screen.
                                    Intent Tmain_intent = new Intent(StartActivity.this, TeacherMainActivity.class);
                                    startActivity(Tmain_intent);
                                    finish();
                                }
                            } else {

                            }
                        }
                    });
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

                Intent reg_intent = new Intent(StartActivity.this, TeacherLoginActivity.class);
                startActivity(reg_intent);
                finish();

            }
        });

        mStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent login_intent = new Intent(StartActivity.this, StudentMainActivity.class);
                startActivity(login_intent);

                finish();

            }
        });

    }
}


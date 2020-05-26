package com.example.siateacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class TeacherResetPasswordActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mToolbar;

    private TextInputLayout mEmail;
    private Button mResetButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_reset_password);

        //toolbar
        mToolbar = findViewById(R.id.resetPw_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Say It App - Reset Password");

        //initialise the variables
        mEmail = findViewById(R.id.resetPw_email);
        mResetButton = findViewById(R.id.resetPw_sendButton);

        //instantiating the firebase auth
        mAuth = FirebaseAuth.getInstance();

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the email and store it in a string variable
                String email = mEmail.getEditText().getText().toString();

                //if email inputted is blank, show error message
                if (email.equals("")){
                    Toast.makeText(TeacherResetPasswordActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    //if email is inputted correctly, send email to user's email with instructions on how to reset password
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(TeacherResetPasswordActivity.this, "Please check your email for password reset directions.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(TeacherResetPasswordActivity.this, TeacherLoginActivity.class));
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(TeacherResetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
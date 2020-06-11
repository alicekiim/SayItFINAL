// Code adapted from tutorial 'Android Firebase Send a Password Reset Email' by Coding Demo.
// Tutorial found at: https://www.youtube.com/watch?v=t8vUdt1eEzE

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

    private androidx.appcompat.widget.Toolbar toolB;

    private TextInputLayout emailInput;
    private Button resetButton;

    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_reset_password);

        //toolbar
        toolB = findViewById(R.id.resetPw_toolbar);
        setSupportActionBar(toolB);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("SayIt! - Reset Password");

        //initialise the variables
        emailInput = findViewById(R.id.resetPw_email);
        resetButton = findViewById(R.id.resetPw_sendButton);

        //instantiating the firebase auth
        fbAuth = FirebaseAuth.getInstance();

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the email and store it in a string variable
                String email = emailInput.getEditText().getText().toString();

                //if email inputted is blank, show error message
                if (email.equals("")){
                    Toast.makeText(TeacherResetPasswordActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    //if email is inputted correctly, send email to user's email with instructions on how to reset password
                    fbAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
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
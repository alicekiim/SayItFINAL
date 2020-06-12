// Code adapted from tutorial 'Chat App with Firebase' by KOD Dev.
// Tutorial found at: https://www.youtube.com/watch?v=b9nNm-xxmOY&list=PLzLFqCABnRQftQQETzoVMuteXzNiXmnj8&index=2

package com.example.siateacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class TeacherLoginActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar toolB;

    private TextInputLayout emailInputLogin;
    private TextInputLayout pwInputLogin;

    private Button loginButton;

    private TextView forgotPW;

    private ProgressDialog progressDiaglog;

    private Button regButton;
    private FirebaseAuth fbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);

        fbAuth = FirebaseAuth.getInstance(); //instantiating firebase authentication

        //create toolbar
        toolB = (Toolbar) findViewById(R.id.login_page_toolbar);
        setSupportActionBar(toolB);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("SayIt! - Teacher Login");

        //creating a new progress dialog
        progressDiaglog = new ProgressDialog(this);

        //initialising the variables
        emailInputLogin = (TextInputLayout) findViewById(R.id.loginPage_email);
        pwInputLogin = (TextInputLayout) findViewById(R.id.loginPage_pw);
        loginButton = (Button) findViewById(R.id.loginPage_loginButton);
        forgotPW = findViewById(R.id.forgot_password);

        //if forgot button is clicked, open reset password activity
        forgotPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeacherLoginActivity.this, TeacherResetPasswordActivity.class));
            }
        });


        toolB.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back_intent = new Intent(TeacherLoginActivity.this, StartActivity.class);
                startActivity(back_intent);
            }
        });




        regButton = (Button) findViewById(R.id.startPage_regButton);


        //send to register activity
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent reg_intent = new Intent(TeacherLoginActivity.this, TeacherRegisterActivity.class);
                startActivity(reg_intent);

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //get the text of the  email and password and store them as a string variable
                String email = emailInputLogin.getEditText().getText().toString();
                String password = pwInputLogin.getEditText().getText().toString();

                //if email/password is not empty,
                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){
                    //show progress dialog title
                    progressDiaglog.setTitle("Logging in");
                    //body message of prog diaglog
                    progressDiaglog.setMessage("Please wait while we check your details");
                    //even if user clicks outside the dialog box, progress log will not be dismissed
                    progressDiaglog.setCanceledOnTouchOutside(false);
                    progressDiaglog.show();

                    //calls loginUser function
                    loginUser(email, password);
                }
            }
        });

    }

    private void loginUser(String email, String password) {

        fbAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){ //if task is successful,
                    progressDiaglog.dismiss(); //dismiss the progress dialog

                    Intent mainIntent = new Intent (TeacherLoginActivity.this, TeacherMainActivity.class); //intent method to take user from the login to main activity
                    startActivity(mainIntent);
                    finish();

                } else{
                    //if log in fails, hide the progress log,
                    progressDiaglog.hide();
                    // and display a message to the user.
                    Toast.makeText(TeacherLoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

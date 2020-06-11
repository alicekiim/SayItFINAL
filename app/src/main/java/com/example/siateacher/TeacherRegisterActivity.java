// Code adapted from tutorial 'Chat App with Firebase' by KODDev.
// Tutorial found at: https://www.youtube.com/watch?v=b9nNm-xxmOY&list=PLzLFqCABnRQftQQETzoVMuteXzNiXmnj8&index=2

package com.example.siateacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class TeacherRegisterActivity extends AppCompatActivity {

    //create variables
    private TextInputLayout mName;
    private TextInputLayout mEmail;
    private TextInputLayout mPw;
    private Button mCreateButton;

    private androidx.appcompat.widget.Toolbar mToolbar;

    private DatabaseReference mDatabase;

    private ProgressDialog mRegProgress;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_register);

        //instantiating the firebase auth
        mAuth = FirebaseAuth.getInstance();

        //toolbar
        mToolbar = findViewById(R.id.reg_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("SayIt! - Teacher Registration");


        //progress dialog
        mRegProgress = new ProgressDialog(this);


        //initialise the variables
        mName = findViewById(R.id.regPage_name);
        mEmail = findViewById(R.id.regPage_email);
        mPw = findViewById(R.id.regPage_pw);
        mCreateButton = findViewById(R.id.regPage_createButton);


        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the text and store it in a string variable
                String display_name = mName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPw.getEditText().getText().toString();

                // if statement to check the textboxes are not empty
                if (!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    //display progress bar
                    mRegProgress.setTitle("Registering your teacher account");
                    mRegProgress.setMessage("We are creating your account");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    //call registerTeacher function
                    registerTeacher(display_name, email, password);
                } else if (password.length()<6){
                    //if password is less than 6 characters, show error message
                    Toast.makeText(TeacherRegisterActivity.this, "Password must be 7 characters or longer.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void registerTeacher(final String display_name, String email, String password) {
        //function to create new user via email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { //if successful

                            //get the current user
                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            //get their uid
                            String userid = current_user.getUid();

                            long temp1 = System.currentTimeMillis(); //Returns the current current date and time in milliseconds.
                            String temp2 =String.valueOf(temp1).substring(4); //convert the time into string
                            int num = Integer.parseInt(temp2); //convert string to int

                            //store the database reference of the current user
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Teachers").child(userid);

                            //store details in hash map, by adding values to the child nodes in db
                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("id", userid);
                            userMap.put("name", display_name);
                            userMap.put("status", "no chat target");
                            userMap.put("image", "default");
                            userMap.put("num", num); //creates a unique serial number for use in notifications


                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){//if successful
                                        mRegProgress.dismiss();//dismiss prog bar

                                        // Sign in success, update UI with the signed-in user's information
                                        //and redirect to main activity
                                        Intent mainIntent = new Intent(TeacherRegisterActivity.this, TeacherMainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                }
                            });

                        } else {//If sign in fails..

                            mRegProgress.hide(); //..hide prog bar
                            // If sign in fails, display a message to the user.

                            //.. and display a message to the user
                            Toast.makeText(TeacherRegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }

                });


    }
}

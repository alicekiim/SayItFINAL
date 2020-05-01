package com.example.siateacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class statusActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mToolbar;

    private TextInputLayout mStatus;
    private Button mSaveBtn;

    //fb
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    //prog
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //instantiate firebase, get current user
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        //get id as string
        String current_uid = mCurrentUser.getUid();

        //getting the database reference for the current user
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mToolbar = (Toolbar) findViewById(R.id.status_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Say It App - Activity Area");

        //receive "status_value" from settingsActivity and store in string
        String status_value = getIntent().getStringExtra("status_value");

        mStatus= (TextInputLayout)findViewById(R.id.status_input);
        mSaveBtn = (Button)findViewById(R.id.statusPage_saveButton);

        //set the inputted text to status_value
        mStatus.getEditText().setText(status_value);

        //save button
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //prog
                mProgress = new ProgressDialog(statusActivity.this);
                mProgress.setTitle("Saving your status..");
                mProgress.setMessage("Please wait.");
                mProgress.show();

                //set status value into string
                String status = mStatus.getEditText().getText().toString();

                //and store the status value into firebase database- in "status" node
                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){ //if its successful, dismiss the progress bar
                            mProgress.dismiss();
                        }else{//if not successful, show error
                            Toast.makeText(getApplicationContext(),"There was an error uploading the status.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }
}

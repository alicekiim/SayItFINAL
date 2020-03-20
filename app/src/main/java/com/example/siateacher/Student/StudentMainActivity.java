package com.example.siateacher.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.siateacher.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class StudentMainActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mToolbar;

    private ViewPager mViewPager;
    private StudentSectionsPagerAdapter mStudentSectionsPagerAdapter;
    private TabLayout mTabLayout;

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.student_main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Say It App - Student");

        // tabs
        mViewPager = (ViewPager) findViewById(R.id.StudentMain_tabPager);
        mStudentSectionsPagerAdapter = new StudentSectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mStudentSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.StudentTabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabTextColors(Color.WHITE, Color.WHITE);
    }

    @Override
    public void onStart(){
        super.onStart();
        //check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI (FirebaseUser user){
        if(user==null){
            mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI (user);



/*
                        //add student users to database
                        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String userid = current_user.getUid();

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Students").child(userid);

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("id", userid);

                            userMap.put("image", "default");
*/

                    } else {
                        updateUI(null);
                    }

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.student_main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.studentMainPage_logoutButton){
            FirebaseAuth.getInstance().signOut();
           /* finish();
            System.exit(0);*/
            finishAffinity();

        }

        return true;
    }
}

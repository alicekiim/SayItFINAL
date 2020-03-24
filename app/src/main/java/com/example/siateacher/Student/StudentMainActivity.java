package com.example.siateacher.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.siateacher.MainActivity;
import com.example.siateacher.R;
import com.example.siateacher.Users;
import com.example.siateacher.faqActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentMainActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mToolbar;

    private ViewPager mViewPager;
    private StudentSectionsPagerAdapter mStudentSectionsPagerAdapter;
    private TabLayout mTabLayout;;

    //private DatabaseReference mDatabase;

    //private FirebaseAuth mAuth;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        // Initialize Firebase Auth
        //mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.student_main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Say It App - Students");

        // tabs
        mViewPager = (ViewPager) findViewById(R.id.StudentMain_tabPager);
        mStudentSectionsPagerAdapter = new StudentSectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mStudentSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.StudentTabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabTextColors(Color.WHITE, Color.WHITE);


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
            finishAffinity();

        }

        if(item.getItemId() == R.id.studentMainPage_faqButton){
            Intent faq_intent = new Intent(StudentMainActivity.this, faqActivity.class);
            startActivity(faq_intent);
        }

        return true;
    }
}

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

import com.example.siateacher.R;
import com.example.siateacher.faqActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentMainActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mToolbar;

    private ViewPager mViewPager;
    private StudentSectionsPagerAdapter mStudentSectionsPagerAdapter;
    private TabLayout mTabLayout;;

    private String mAuth; //로그인한 학생 아이디


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

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
        mAuth = FirebaseAuth.getInstance().getCurrentUser().getUid(); //로그인한 사용자 Uid

        if(item.getItemId() == R.id.studentMainPage_logoutButton){

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatList");//Database [ChatList] read
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    DatabaseReference delRef = FirebaseDatabase.getInstance().getReference("ChatList");
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String user = snapshot.getKey(); //ChatList 키값(선생)을 읽어온다

                        delRef.child(user).child(mAuth).removeValue(); //Database [ChatList] 학생정보 삭제
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            FirebaseAuth.getInstance().getCurrentUser().delete(); //익명사용자 Authentication 에서 삭제

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Students");
            ref.child(mAuth).removeValue(); //익명사용자 Database [Students]에서 삭제


            FirebaseAuth.getInstance().signOut(); //사용자 로그아웃

            finishAffinity(); // APP 종료

        }

        if(item.getItemId() == R.id.studentMainPage_faqButton){
            Intent faq_intent = new Intent(StudentMainActivity.this, faqActivity.class);
            startActivity(faq_intent);
        }

        return true;
    }

}


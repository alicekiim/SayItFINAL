// Code adapted from tutorial 'Chat App with Firebase' by KODDev.
// Tutorial found at: https://www.youtube.com/watch?v=7H_xBEQPHhw&list=PLzLFqCABnRQftQQETzoVMuteXzNiXmnj8&index=4

package com.example.siateacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherMainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mfirebaseUser;
    private DatabaseReference mReference;

    private androidx.appcompat.widget.Toolbar mToolbar;

    private ViewPager mViewPager;
    private TeacherSectionsPagerAdapter mTeacherSectionsPagerAdapter;

    private TabLayout mTabLayout;

    private CircleImageView profile_image;
    private TextView username;

    private Button mChatButton;
    //private Button mQuizButton;
    // private Button mFAQButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // storing the toolbar in the toolbar variable
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("SayIt! - Teacher");

        // storing the viewpager in the viewpager variable
        mViewPager = (ViewPager) findViewById(R.id.mainPage_tabPager);
        mTeacherSectionsPagerAdapter = new TeacherSectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mTeacherSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabTextColors(Color.WHITE, Color.WHITE);

        //initialising the variables
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.name);

        //startService(new Intent(this, UnCatchTaskService.class)); //task종료시점을 알기 위해 학생 초기화면에 startService 시작한다.
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            //Go to login
        }
        else {

            //get current user
            mfirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            //storing the database reference of the user
            mReference = FirebaseDatabase.getInstance().getReference("Teachers").child(mfirebaseUser.getUid());

            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Get a DataSnapshot for the location at the specified relative path
                    Teachers user = dataSnapshot.getValue(Teachers.class);

                    // if user is found
                    if(user != null) {

                        //get their name
                        username.setText(user.getName());

                        //get their profile image
                        if ("default".equals(user.getImage())) {
                            profile_image.setImageResource(R.mipmap.ic_launcher);
                        } else {
                            Glide.with(getApplicationContext()).load(user.getImage()).into(profile_image);
                        }
                    }else {
                        //if username isn't found, put as blank
                        username.setText("");
                        //if profile image isn't found, put in a default image
                        profile_image.setImageResource(R.mipmap.ic_launcher);

                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }



    }


 /*   private void backToStartPage() {
        Intent startIntent = new Intent(TeacherMainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }*/

    private void backToStartPage() {

        Intent startIntent = new Intent(TeacherMainActivity.this, StartActivity.class);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(startIntent);
        //finish();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        // takes user to account settings
        if(item.getItemId() == R.id.mainPage_accountsettingsButton){
            Intent settings_intent = new Intent(TeacherMainActivity.this, TeacherSettingsActivity.class);
            startActivity(settings_intent);
        }

        // takes user to faq page
        if(item.getItemId() == R.id.mainPage_faqButton){
            Intent faq_intent = new Intent(TeacherMainActivity.this, TeacherFaqActivity.class);
            startActivity(faq_intent);
        }

        //if user signs out, go back to start page
        if (item.getItemId() == R.id.mainPage_logoutButton) {

            FirebaseAuth.getInstance().signOut();
            backToStartPage();

        }



        return true;
    }

}

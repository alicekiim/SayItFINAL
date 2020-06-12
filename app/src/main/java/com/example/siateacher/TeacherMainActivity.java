// Code adapted from tutorial 'Chat App with Firebase' by KOD Dev.
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

    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;
    private DatabaseReference dbRef;

    private androidx.appcompat.widget.Toolbar toolB;

    private ViewPager viewPager;
    private TeacherSectionsPagerAdapter teacherSectionsPagerAdapter;

    private TabLayout tabLayout;

    private CircleImageView profileImage;
    private TextView username;

    private Button mChatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        // Initialize Firebase Auth
        fbAuth = FirebaseAuth.getInstance();

        // storing the toolbar in the toolbar variable
        toolB = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolB);
        getSupportActionBar().setTitle("SayIt! - Teacher");

        // storing the viewpager in the viewpager variable
        viewPager = (ViewPager) findViewById(R.id.mainPage_tabPager);
        teacherSectionsPagerAdapter = new TeacherSectionsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(teacherSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(Color.WHITE, Color.WHITE);

        //initialising the variables
        profileImage = findViewById(R.id.profile_image);
        username = findViewById(R.id.name);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            //Go to login
        }
        else {

            //get current user
            fbUser = FirebaseAuth.getInstance().getCurrentUser();
            //storing the database dbRef of the user
            dbRef = FirebaseDatabase.getInstance().getReference("Teachers").child(fbUser.getUid());

            dbRef.addValueEventListener(new ValueEventListener() {
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
                            profileImage.setImageResource(R.mipmap.ic_launcher);
                        } else {
                            Glide.with(getApplicationContext()).load(user.getImage()).into(profileImage);
                        }
                    }else {
                        //if username isn't found, put as blank
                        username.setText("");
                        //if profile image isn't found, put in a default image
                        profileImage.setImageResource(R.mipmap.ic_launcher);

                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }



    }




    private void backToStartPage() {

        Intent startIntent = new Intent(TeacherMainActivity.this, StartActivity.class);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(startIntent);

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

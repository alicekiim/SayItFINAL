package com.example.siateacher.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.siateacher.R;
import com.example.siateacher.UnCatchTaskService;
import com.example.siateacher.faqActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StudentMainActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mToolbar;

    private ViewPager mViewPager;
    private StudentSectionsPagerAdapter mStudentSectionsPagerAdapter;
    private TabLayout mTabLayout;;

    private String mAuth;

    private FirebaseUser mfirebaseUser;
    private DatabaseReference mReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        //create toolbar
        mToolbar = (Toolbar) findViewById(R.id.student_main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Say It App - Student");

        //set up ViewPager
        mViewPager = (ViewPager) findViewById(R.id.StudentMain_tabPager);
        //pager adapter flip between the fragments
        mStudentSectionsPagerAdapter = new StudentSectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mStudentSectionsPagerAdapter);

        //populate the tabs
        mTabLayout = (TabLayout) findViewById(R.id.StudentTabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabTextColors(Color.WHITE, Color.WHITE);

        //startService: Request that a given application service be started
        // In order to know when the task is finished, startService starts on the initial screen of the student.
        startService(new Intent(this, UnCatchTaskService.class));

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            //Go to login
            Log.e("Error", "TTTTTTTTTTTTTTTTTTT== null");
        }
        else {

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

        //current user id
        mAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //if click on logout button
        if(item.getItemId() == R.id.studentMainPage_logoutButton){

            //read [ChatList] in firebase database
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatList");

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //get reference to chatlist in database
                    DatabaseReference delRef = FirebaseDatabase.getInstance().getReference("ChatList");
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //Read ChatList key value (teacher)
                        String user = snapshot.getKey();

                        //Delete student information from Database [ChatList]
                        delRef.child(user).child(mAuth).removeValue();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            FirebaseAuth.getInstance().getCurrentUser().delete(); //Removes anonymous user details from firebase authentication

            //point to [Students] database
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Students");
            //Deletes anonymous user from database [Students]
            ref.child(mAuth).removeValue();

            //logs out student user
            FirebaseAuth.getInstance().signOut();

            //remove app from running apps
            finishAndRemoveTask();

            //terminates and exits system
            System.exit(0);



        }

        //if click on faq button, take them to faq activity
        if(item.getItemId() == R.id.studentMainPage_faqButton){
            Intent faq_intent = new Intent(StudentMainActivity.this, StudentFaqActivity.class);
            startActivity(faq_intent);
        }

        return true;
    }

}


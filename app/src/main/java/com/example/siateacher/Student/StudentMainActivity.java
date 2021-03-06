// Code adapted from tutorial 'Chat App with Firebase' by KOD Dev.
// Tutorial found at: https://www.youtube.com/watch?v=7H_xBEQPHhw&list=PLzLFqCABnRQftQQETzoVMuteXzNiXmnj8&index=4

package com.example.siateacher.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.siateacher.R;
import com.example.siateacher.UnCatchTaskService;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentMainActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar toolB;

    private ViewPager viewPager;
    private StudentSectionsPagerAdapter studentSectionsPagerAdapter;
    private TabLayout tabLayout;

    private String mAuth;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        //create toolbar
        toolB = (Toolbar) findViewById(R.id.student_main_toolbar);
        setSupportActionBar(toolB);
        getSupportActionBar().setTitle("SayIt! - Student");

        //set up ViewPager
        viewPager = (ViewPager) findViewById(R.id.StudentMain_tabPager);
        //pager adapter flip between the fragments
        studentSectionsPagerAdapter = new StudentSectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(studentSectionsPagerAdapter);

        //populate the tabs
        tabLayout = (TabLayout) findViewById(R.id.StudentTabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(Color.WHITE, Color.WHITE);

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

            //The number of times you call the singleEventValueListener, it get triggers once every time it is called.
            //**(While on the other hand addValueEventListener() fetches the value every time the value is changed in your firebase realtime DB node to which it is referencing.)
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //get reference to Chatlist in database
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

            //Removes anonymous user details from firebase authentication
            FirebaseAuth.getInstance().getCurrentUser().delete();

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


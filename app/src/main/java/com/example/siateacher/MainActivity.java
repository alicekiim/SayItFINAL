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

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mfirebaseUser;
    private DatabaseReference mReference;

    private androidx.appcompat.widget.Toolbar mToolbar;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TabLayout mTabLayout;

    private CircleImageView profile_image;
    private TextView username;

    private Button mChatButton;
    //private Button mQuizButton;
    // private Button mFAQButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Say It App - Teacher Area");

// tabs
        mViewPager = (ViewPager) findViewById(R.id.mainPage_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.mainTabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabTextColors(Color.WHITE, Color.WHITE);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.name);
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            //Go to login
        }
        else {

            mfirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            mReference = FirebaseDatabase.getInstance().getReference("Users").child(mfirebaseUser.getUid());

            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Users user = dataSnapshot.getValue(Users.class);
////////////////////////////////////////////////////////////////////////////////////////////////////////
                    if(user != null) {
                        username.setText(user.getName());
                        //if (user.getImage().equals("default")) {
                        if ("default".equals(user.getImage())) {
                            profile_image.setImageResource(R.mipmap.ic_launcher);
                        } else {
                            Glide.with(getApplicationContext()).load(user.getImage()).into(profile_image);
                        }
                    }else {
                        username.setText("");
                        profile_image.setImageResource(R.mipmap.ic_launcher);

                    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //  mFAQButton = (Button) findViewById(R.id.mainPage_faqButton);

/*        mChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent chat_intent = new Intent(MainActivity.this, chatActivity.class);
                startActivity(chat_intent);

            }
        });*/


    }
    //////////////////////////////////////////////////////////////////////////////////////////////////
   /* @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            backToStartPage();
        }
    }
*/
////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void backToStartPage() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
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

        if(item.getItemId() == R.id.mainPage_accountsettingsButton){
            Intent settings_intent = new Intent(MainActivity.this, settingsActivity.class);
            startActivity(settings_intent);
        }

        if(item.getItemId() == R.id.mainPage_faqButton){
            Intent faq_intent = new Intent(MainActivity.this, faqActivity.class);
            startActivity(faq_intent);
        }

        if (item.getItemId() == R.id.mainPage_logoutButton) {

            FirebaseAuth.getInstance().signOut();
            backToStartPage();

        }

        return true;
    }


}

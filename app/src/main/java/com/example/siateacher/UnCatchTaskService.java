package com.example.siateacher;

import android.app.Service;
import android.content.Intent;

import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//code framework found and adapted from https://mine-it-record.tistory.com/228

public class UnCatchTaskService extends Service {

    private String mAuth;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        mAuth = FirebaseAuth.getInstance().getCurrentUser().getUid(); //get current users uid

        // if the current user is anonymous (therefore a student)
        if(FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
            FirebaseAuth.getInstance().getCurrentUser().delete(); //Remove user from anonymous authentication

            //point to student database in firebase
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Students");
            //delete the current anonymous user from student database in firebase
            ref.child(mAuth).removeValue();
            //and log out
            FirebaseAuth.getInstance().signOut();
        }

        //ends app
        stopSelf();



    }
}

package com.example.siateacher.Student;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.siateacher.R;
import com.example.siateacher.Users;
import com.example.siateacher.chatActivity;
import com.example.siateacher.usersActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentHomeFragment extends Fragment {

    private List<Users> mUsers;

    private View mMainView;
    ///////////////////////////////////////////////////////////////////////////
    private Button mStartChatButton;
    int mListCnt = 0;
    //////////////////////////////////////////////////////////////////////////

    private FirebaseAuth mFirebaseAuth;

    private DatabaseReference mDatabase;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public StudentHomeFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFBAuthentication();
        initFBAuthState();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_student_home, container, false);

        mStartChatButton = mMainView.findViewById(R.id.student_startchat);

        return mMainView;

    }
    public void onStart(){
        super.onStart();
        mUsers = new ArrayList<>();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        signInAnonymously();//Anonymous authentication

        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        mStartChatButton = mMainView.findViewById(R.id.student_startchat);

        mStartChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chat_intent = new Intent(getContext(), chatActivity.class);
                if (mListCnt == mUsers.size()) {
                    mListCnt = 0;
                }
                chat_intent.putExtra("id", mUsers.get(mListCnt).getId());
                getContext().startActivity(chat_intent);
                mListCnt++;
                //mStartChatButton.setText("aaaaa");
            }

        });
    }
    private void initFBAuthentication() {
        mFirebaseAuth = FirebaseAuth.getInstance();
    }
    private void initFBAuthState() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser != null) {
                    // message = "onAuthStateChanged signed in : " + firebaseUser.getUid();
                } else {
                    // message = "onAuthStateChanged signed out";
                }
                //mAuthStateTextview.setText(message);
            }
        };
    }
    private void signInAnonymously() {
        mFirebaseAuth.signInAnonymously()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();

                            String studentsid = user.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Students").child(studentsid);

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("id", studentsid);
                            userMap.put("image", "default");
                            userMap.put("name", "guest");

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        readUsers();
                                    }
                                }
                            });
                        } else {

                        }

                    }
                });
    }

    private void readUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users user = snapshot.getValue(Users.class);

                    //if (!user.getName().equals(firebaseUser.getUid())) {
                    if (!firebaseUser.getUid().equals(user.getId())) {
                        mUsers.add(user);
                    }

                }

                //usersActivity = new usersActivity(getContext(), mUsers);
                //mUsersListRecycler.setAdapter(usersActivity);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

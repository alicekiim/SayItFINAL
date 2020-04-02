package com.example.siateacher;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class chatsFragment extends Fragment {

    private RecyclerView mUsersListRecycler;
    private usersActivity usersActivity;
    private List<Users> mUsers;

    private View mMainView;

    public chatsFragment(){//선생 시작화면

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_users, container, false);

        mUsersListRecycler = mMainView.findViewById(R.id.fragments_users_list);
        mUsersListRecycler.setHasFixedSize(true);
        mUsersListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();

        readUsers(); //연결된 학생list를 가져온다

        return mMainView;
    }

    private void readUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                int num =1;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users user = snapshot.getValue(Users.class);

                    if("online".equals(user.getStatus())) {//학생의 status 값이 online 인 경우만 보여준다
                        user.setName("Students" + num); //학생리스트에서 순서대로 번호를 지정해서 보여준다
                        mUsers.add(user);

                        num++;
                    }
                }
                usersActivity = new usersActivity(getContext(), mUsers);
                mUsersListRecycler.setAdapter(usersActivity);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}

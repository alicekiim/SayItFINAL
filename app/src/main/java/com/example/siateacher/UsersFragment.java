/*

package com.example.siateacher;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;



import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


*/
/**
 * A simple {@link Fragment} subclass.
 *//*



public class UsersFragment extends Fragment {

    private RecyclerView mUsersListRecycler;
    private usersActivity usersActivity;
    private List<Users> mUsers;

    private View mMainView;
    ///////////////////////////////////////////////////////////////////////////
    private Button mStartChatButton;
    int mListCnt = 0;
    //////////////////////////////////////////////////////////////////////////
    public UsersFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_users, container, false);

        //mUsersListRecycler = mMainView.findViewById(R.id.fragments_users_list);
        //mUsersListRecycler.setHasFixedSize(true);
        //mUsersListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();



        readUsers();


        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        mStartChatButton = mMainView.findViewById(R.id.home_startchat);


        mStartChatButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent chat_intent = new Intent(getContext(), chatActivity.class);
                if(mListCnt==mUsers.size()){
                    mListCnt = 0;
                }
                chat_intent.putExtra("id", mUsers.get(mListCnt).getId());
                getContext().startActivity(chat_intent);
                mListCnt++;
                //mStartChatButton.setText("aaaaa");
            }

        });
        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        return mMainView;
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
                    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    if (!firebaseUser.getUid().equals(user.getId())) {
                        mUsers.add(user);
                    }
//////////////////////////////////////////////////////////////////////////////////////////////////////
                }

                usersActivity = new usersActivity(getContext(), mUsers);
                //mUsersListRecycler.setAdapter(usersActivity);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}

*/

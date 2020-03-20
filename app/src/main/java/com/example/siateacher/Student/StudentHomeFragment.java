package com.example.siateacher.Student;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.List;

public class StudentHomeFragment extends Fragment {

    private List<Users> mUsers;

    private View mMainView;
    ///////////////////////////////////////////////////////////////////////////
    private Button mStartChatButton;
    int mListCnt = 0;
    //////////////////////////////////////////////////////////////////////////

    public StudentHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_student_home, container, false);

        mUsers = new ArrayList<>();

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
        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        return mMainView;

    }
}

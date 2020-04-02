package com.example.siateacher.Student;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.siateacher.R;
import com.example.siateacher.Users;
import com.example.siateacher.chatActivity;
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
    int mListCnt = 0;//선생List 반복을 위한 값
    //////////////////////////////////////////////////////////////////////////
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabase;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public StudentHomeFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUsers = new ArrayList<>();
        initFBAuthentication(); //FirebaseAuth 인스턴스를 초기화합니다.
        signInAnonymously();
        initFBAuthState(); //FirebaseAuth 상태체크
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

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        //signInAnonymously();//Anonymous authentication

        //////////////////////////////////////////////////////////////////////////////////////////////////////////
        mStartChatButton = mMainView.findViewById(R.id.student_startchat);

        mStartChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chat_intent = new Intent(getContext(), chatActivity.class); //chatActivity 보낼 intent를 정의

                if (mListCnt == mUsers.size()) {//Database [Users] 사이즈와 mListCnt 같은지 체크
                    mListCnt = 0; //같으면 mListCnt 값을 0으로 초기화 시켜 반복 하게 해준다
                }
                chat_intent.putExtra("id", mUsers.get(mListCnt).getId()); //intent 값으로 mUsers에서 mListCnt번째에 해당하는 Id
                chat_intent.putExtra("classification", "student"); //학생으로 채팅에 들어간건지 선생으로 들어간건지 체크하기 위한 값
                getContext().startActivity(chat_intent);
                mListCnt++; //클릭할때마다 증가 시켜준다
            }

        });
    }
    private void initFBAuthentication() {
        mFirebaseAuth = FirebaseAuth.getInstance(); //FirebaseAuth 인스턴스를 초기화합니다.
    }

    private void initFBAuthState() { //FirebaseAuth 상태체크
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser != null) {
                    //mStartChatButton.setEnabled(true); //start chat버튼 활성화
                    // message = "onAuthStateChanged signed in : " + firebaseUser.getUid();
                } else {
                    mStartChatButton.setEnabled(false); //start chat버튼 비활성화
                    // message = "onAuthStateChanged signed out";
                }
                //mAuthStateTextview.setText(message);
            }
        };
    }
    private void signInAnonymously() {// 익명 사용자 로그인
        mFirebaseAuth.signInAnonymously()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            //익명 사용자 생성후 Database [Students] 에 학생 정보를 넣어준다
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();

                            String studentsid = user.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Students").child(studentsid);

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("id", studentsid);
                            userMap.put("image", "default");
                            userMap.put("name", "Student");
                            userMap.put("status", "online");

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        readUsers(); //채팅에 사용할 선생 list 불러오기
                                    }
                                }
                            });
                        } else {

                        }

                    }
                });
    }

    private void readUsers() {// 선생 list read
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users user = snapshot.getValue(Users.class);

                    if (!firebaseUser.getUid().equals(user.getId())) {
                        mUsers.add(user);
                    }
                }
                mStartChatButton.setEnabled(true); //start chat버튼 활성화(체팅에서 사용할 사용자 정보를 가져온 후 채팅버튼 활성화
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

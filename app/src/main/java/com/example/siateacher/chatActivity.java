package com.example.siateacher;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class chatActivity extends AppCompatActivity {


    private androidx.appcompat.widget.Toolbar mToolbar;

    CircleImageView profile_image;
    TextView username;

    ImageButton sendbtn;
    EditText chatbox;
    private Button mEndChat;


    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    private Intent intent;
    FirebaseUser fuser;

    DatabaseReference reference;

    ArrayList<String> Ckeylist = new ArrayList<String>(); //채팅내용 삭제때 사용하기 위한 리스트

    private String classification; //채팅창에 들어온 사람이 학생인지 선생인지 구분하기 위한 intent 값을 담을 변수
    private String offUserid; //채팅상대 아이디 intent 값을 담을 변수
    private boolean removeButton =false; //채팅을 뒤로가기로 나간건지 mEndChat버튼으로 나간건지 구분
    private boolean chatEmpty =true; //채팅 내용이 있는지를 구분 false로 변경되면 채팅 내용이 있었다가 학생쪽이 삭제하거나 로그아웃한것이기 때문에 채팅창(선생쪽)에서 나간다

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mToolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //mine
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.name);

        sendbtn = findViewById(R.id.sendbtn);
        chatbox = findViewById(R.id.chatbox);
        mEndChat = findViewById(R.id.button4);
        intent = getIntent();
        final String userid = intent.getStringExtra("id"); //채팅상대 Uid
        classification = intent.getStringExtra("classification");

        offUserid = userid; //protected void onPause() 에서 사용하기위해 userid를 offUserid에 넣어줌
        fuser = FirebaseAuth.getInstance().getCurrentUser(); //로그인한 User정보

        //my own uid

        if(classification.equals("teacher")) {//채팅창에 상대정보를 보여주기위해
            reference = FirebaseDatabase.getInstance().getReference("Students").child(userid);
        }else {//equals("student")
            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
            //학생이 선생채팅창에 들어가면 ChatList에 해당선생 아래 학생정보를 넣어준다(선생화면에서 학생list 가져오기 위한 추가)
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            HashMap<String, String> chatuserMap = new HashMap<>();
            chatuserMap.put("id", fuser.getUid());
            chatuserMap.put("image", "default");
            chatuserMap.put("name", "student");
            chatuserMap.put("status", "online");
            ref.child("ChatList").child(userid).child(fuser.getUid()).setValue(chatuserMap);
        }

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = chatbox.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(fuser.getUid(), userid, msg);
                } else {
                    Toast.makeText(chatActivity.this, "Can't send empty messages.",
                            Toast.LENGTH_SHORT).show();
                }
                chatbox.setText("");

            }
        });

        mEndChat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(chatActivity.this);

                builder.setTitle("chat delete"); //AlertDialog title
                builder.setMessage("Are you sure you want to end chat? The chat will be deleted."); //AlertDialog Message

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {//yes click

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
                        //채팅 내용을 읽어올때 Ckeylist에 키값을 저장하여 닫기 버튼 클릭할때 채팅내용을 삭제한다.
                        for (int i = 0; i < Ckeylist.size(); i++){
                            if (Ckeylist.get(i) != null) {
                                ref.child(Ckeylist.get(i)).removeValue();
                            }
                        }
                        //닫기 버튼 클릭할때 ChatList에서 학생의 정보를 삭제한다.
                        if(classification.equals("student")) {
                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("ChatList").child(userid);
                            ref2.child(fuser.getUid()).removeValue();

                        }else{
                            //선생의경우 삭제버튼으로 나갔어도 학생은 안나가고 있을경우가 있기때문에 주석처리함
                            //DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("ChatList").child(fuser.getUid());
                            //ref2.child(userid).removeValue();
                        }
                        //protected void onPause() 에서 삭제버튼으로 화면을 나갔을경우 ChatList의 학생정보도 삭제되기때문에 status->offline 를 변경하면 안됨
                        removeButton = true ;
                        finish();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener(){//no click
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        //Toast.makeText(getApplicationContext(), "NO", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }

        });





        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) { // 값이 없는지 있는지를 체크
                    Users user = dataSnapshot.getValue(Users.class);
                    username.setText(user.getName());

                    if (user.getImage().equals("default")) {
                        profile_image.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Glide.with(chatActivity.this).load(user.getImage()).into(profile_image);
                    }

                    readMessages(fuser.getUid(), userid, user.getImage());
                }else{
                    finish(); //선생 채팅창의경우 학생이 로그아웃 하면 채팅창 종료
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String message) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("sender", sender);
        hashmap.put("receiver", receiver);
        hashmap.put("message", message);
        hashmap.put("isseen", false);

        ref.child("Chats").push().setValue(hashmap);
    }

    private void readMessages (final String myid, final String userid, final String imageurl){
        mchat = new ArrayList<>();

        DatabaseReference chtReference = FirebaseDatabase.getInstance().getReference("Chats");
        chtReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                Ckeylist.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(dataSnapshot.exists()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        //if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        if (myid != null && userid != null) {
                            if (myid.equals(chat.getReceiver()) && userid.equals(chat.getSender()) || userid.equals(chat.getReceiver()) && myid.equals(chat.getSender())) {
                                mchat.add(chat);
                                Ckeylist.add(snapshot.getKey()); //채팅내용 삭제를 위한 리스트
                                chatEmpty = false;//닫기버튼으로 삭제를 한건지 처음 채팅방에 들어가서 내용이 없는건지 판단
                            }
                        }

                        messageAdapter = new MessageAdapter(getApplicationContext(), mchat, imageurl);
                        recyclerView.setAdapter(messageAdapter);
                    }


                }
                if(mchat.size()== 0){
                    //mEndChat.setEnabled(false);
                    messageAdapter = new MessageAdapter(getApplicationContext(), mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                    if(classification.equals("teacher") && chatEmpty == false) {
                        finish(); //학생이 닫기버튼을 눌렀을때 선생 채팅창 닫기
                    }
                }else{
                    //mEndChat.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //focus를 잃었을경우 학생상태를 offline 로 변경해 선생창에 리스트에서 안보이게 한다
        if(classification.equals("student") && removeButton == false) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatList").child(offUserid).child(fuser.getUid());
            reference.child("status").setValue("offline");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //focus를 다시 얻었을경우 학생상태를 online 로 변경해 선생창에 리스트에서 보이게 한다
        if(classification.equals("student") && removeButton == false) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatList").child(offUserid).child(fuser.getUid());
            reference.child("status").setValue("online");
        }

    }
}

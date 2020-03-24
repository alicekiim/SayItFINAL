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
    //////////////////////////////////////////////////////////////////////////////////////
    TextView username;
    //////////////////////////////////////////////////////////////////////////////////////

    ImageButton sendbtn;
    EditText chatbox;
    //////////////////////////////////////////////////////////////////////////////////////
    private Button mEndChat;//chat deleted
    //////////////////////////////////////////////////////////////////////////////////////

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    private Intent intent;
    FirebaseUser fuser;

    DatabaseReference reference;

    ValueEventListener seenListener;

    /////////////////////////////////////////////////////////////////////////////////////////////////
    final ArrayList<String> Ckeylist = new ArrayList<String>();//Firebase Chats Uid
////////////////////////////////////////////////////////////////////////////////////////////////////

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
        /////////////////////////////////////////////////////////////////////////////////////////////
        username = findViewById(R.id.name);
        //////////////////////////////////////////////////////////////////////////////////////////

        sendbtn = findViewById(R.id.sendbtn);
        chatbox = findViewById(R.id.chatbox);
        ///////////////////////////////////////////////////////////////////////////////////////////
        mEndChat = findViewById(R.id.button4);
        ///////////////////////////////////////////////////////////////////////////////////////////
        intent = getIntent();
        final String userid = intent.getStringExtra("id");
        //final String stat = intent.getStringExtra("status");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        //my own uid
        //reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

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
/////////////////////////////////////////////////////////////////////////////////////////////////////////C: ¡°end chat¡± button AlertDialog
        mEndChat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(chatActivity.this);

                builder.setTitle("chat delete");
                builder.setMessage("Are you sure you want to end chat? The chat will be deleted.");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {//yes click

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
                        for (int i = 0; i < Ckeylist.size(); i++){
                            if (Ckeylist.get(i) != null) {
                                ref.child(Ckeylist.get(i)).removeValue();
                                finish();
                            }
                        }
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

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////



        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*Toast.makeText(chatActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();*/

                Users user = dataSnapshot.getValue(Users.class);
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////
                username.setText(user.getName());
                /////////////////////////////////////////////////////////////////////////////////////////////////

                if (user.getImage().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(chatActivity.this).load(user.getImage()).into(profile_image);
                }

                readMessages(fuser.getUid(), userid, user.getImage());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userid);

    }

    private void seenMessage(final String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String,Object> hashmap = new HashMap<>();
                        hashmap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashmap);
                    }
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

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                ////////////////////////////////////////////////////////////////////////////////////
                Ckeylist.clear();
                ////////////////////////////////////////////////////////////////////////////////////
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mchat.add(chat);
                        //////////////////////////////////////////////////////////////////////////////////////
                        Ckeylist.add(snapshot.getKey());
                        /////////////////////////////////////////////////////////////////////////////////////
                    }

                    messageAdapter = new MessageAdapter(getApplicationContext(), mchat,imageurl);
                    recyclerView.setAdapter(messageAdapter);
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
        reference.removeEventListener(seenListener);
    }
}


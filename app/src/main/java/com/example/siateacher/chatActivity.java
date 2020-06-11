// Code adapted from tutorial 'Chat App with Firebase' by KOD Dev.
// Tutorial found at: https://www.youtube.com/watch?v=1mJv4XxWlu8&list=PLzLFqCABnRQftQQETzoVMuteXzNiXmnj8&index=8

package com.example.siateacher;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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


    private androidx.appcompat.widget.Toolbar toolB;

    CircleImageView profile_image;
    TextView username;

    ImageButton sendMsgButton;
    EditText msgInputBox;
    private Button endChat;


    MessageAdapter messageAdapter;
    List<Chat> aChat;

    RecyclerView recyclerView;

    private Intent intent;
    FirebaseUser fbUser;

    DatabaseReference dbRef;
    DatabaseReference dbRef2;

    ArrayList<String> chatLogList = new ArrayList<String>(); //List to use when deleting chat contents

    private String classification; //Variable to hold the intent value to distinguish whether the person who entered the chat window is a student or a teacher
    private String offUserid; //Variable to hold the chat partner ID intent value
    private boolean endButton =false; //variable to determine whether the user left the chat room via end chat button or back button
    private boolean chatContent =true; //determines if there is chat content, if false- if chatContent becomes false (so the student logged out or ended chat, causing the chat content to be deleted), it will make the teacher leave the chat page too
    private String statusValue; //Chat partner's status value

    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //create toolbar
        toolB = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolB);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.name);

        sendMsgButton = findViewById(R.id.sendbtn);
        msgInputBox = findViewById(R.id.chatbox);
        endChat = findViewById(R.id.button4);
        intent = getIntent();

        // retrieve the id data from StudentChatsFragment/TeacherChatsFragment using "id"
        final String userid = intent.getStringExtra("id");

        //retrieve the classification data
        //determines if the entered user is a student or teacher
        classification = intent.getStringExtra("classification");

        //copy of userid for use in onPause()
        offUserid = userid;

        //gets current user
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

//what is the purpose of this? explain
        if(classification.equals("teacher")) {//If teacher user..
            //get teacher id
            dbRef = FirebaseDatabase.getInstance().getReference("Teachers").child(fbUser.getUid());
            //make their status "online"
            dbRef.child("status").setValue("online");
            //get the id of the student
            dbRef = FirebaseDatabase.getInstance().getReference("Students").child(userid);

        }else {//if student user
            //get students id
            dbRef = FirebaseDatabase.getInstance().getReference("Students").child(fbUser.getUid());
            //make their status "online"
            dbRef.child("status").setValue("online");
            //get the id of the teacher user
            dbRef = FirebaseDatabase.getInstance().getReference("Teachers").child(userid);


            //..and create new hashmap
            //when the student starts chat with teacher, input the student details under the teacher's ID under chatlist branch
            //Chatlist
            //--teacher id (userid)
            //----student id (fbUser.getUid())
            //------id, image, name, status
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            HashMap<String, String> chatuserMap = new HashMap<>();
            chatuserMap.put("id", fbUser.getUid()); //student id
            chatuserMap.put("image", "default");
            chatuserMap.put("name", "student");
            chatuserMap.put("status", "online");

            //chatlist - teacher id - student id - id, image, name, status
            ref.child("ChatList").child(userid).child(fbUser.getUid()).setValue(chatuserMap);
        }

        //only allow sending of message if there is content
        sendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = msgInputBox.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(fbUser.getUid(), userid, msg);
                } else {
                    Toast.makeText(chatActivity.this, "Can't send empty messages.",
                            Toast.LENGTH_SHORT).show();
                }
                msgInputBox.setText("");

            }
        });

        //if click end chat button
        endChat.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(chatActivity.this);

                //AlertDialog title
                builder.setTitle("End Chat");
                //AlertDialog Message
                builder.setMessage("Are you sure you want to end chat? The chat will be deleted.");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //(if click endchat + select yes)

                        //point to "Chats" in firebase database..
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
                        //..and store the chats in "chatLogList"
                        for (int i = 0; i < chatLogList.size(); i++){
                            if (chatLogList.get(i) != null) {
                                //and delete the chat contents
                                ref.child(chatLogList.get(i)).removeValue();
                            }
                        }

                        //..and also if as a student initiated end chat..
                        if(classification.equals("student")) {
                            //..get the database dbRef of the teacher user in "Chatlist"..
                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("ChatList").child(userid);
                            //..and delete the teacher + student users branch.
                            ref2.child(fbUser.getUid()).removeValue();

                            //(therefore, clicking the endchat button will delete the student's information from the ChatList.)

                        }else{

                        }

                        //user has left the screen via "end chat" button, so change to "true"
                        endButton = true ;

                        finish();

                    }
                });

                //if "no" selected
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        //do nothing
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

        });



        //retrieve user's image and status if chat is engaged
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //if datasnapshot exists
                if(dataSnapshot.exists()) {
                    //get username of user
                    Teachers user = dataSnapshot.getValue(Teachers.class);
                    username.setText(user.getName());

                    //get image of user
                    if (user.getImage().equals("default")) {
                        //load default
                        profile_image.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Activity activity = chatActivity.this;
                        if (activity.isFinishing())
                            return;
                        //load unique image user selected
                        Glide.with(chatActivity.this).load(user.getImage()).into(profile_image);
                    }
                    //get the users status (online/offline)
                    statusValue = user.getStatus();
                    readMessages(fbUser.getUid(), userid, user.getImage());

                }else{
                    //Log.e("Error", "chatActivity249"+classification);

                    //if no datasnapshot exists, close the chat page
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Change the isseen value when the other side reads the chat
        seenMessage(userid);
    }

    //when message is seen by chat partner..
    private void seenMessage(final String userid){
        //get dbRef to Chats in database
        dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //get a snapshot of chat class
                    Chat chat = snapshot.getValue(Chat.class);
                    //if message receiver equals current user and message sender equals
                    if (chat.getReceiver().equals(fbUser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String,Object> hashmap = new HashMap<>();
                        //if read, set to true
                        hashmap.put("isseen", true);
                        //update hashmap
                        snapshot.getRef().updateChildren(hashmap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    //when sending message..
    private void sendMessage(String sender, String receiver, String message) {

        //get dbRef to database
        //A DatabaseReference represents a specific location in database and can be used for reading or writing data to that database location.
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        //create hashmap
        HashMap<String, Object> hashmap = new HashMap<>();
        hashmap.put("sender", sender);
        hashmap.put("receiver", receiver);
        hashmap.put("message", message);

        //If the message receiver's status value is the sender's ID.. (meaning that the receiver has entered the chat room with the sender)
        if(fbUser.getUid().equals(statusValue)) {
            //.. the message is marked as read.
            hashmap.put("isseen", true);

            //else if the receiver's status is a different value other than the sender's ID (such as "online", "offline" or "no chat target")..
        }else{
            //..the message is marked as delivered.
            hashmap.put("isseen", false);//
        }

        //the hashmap info (isseen, sender, reciever, message) is added to "Chats" in firebase database
        ref.child("Chats").push().setValue(hashmap);

    }

    //when
    private void readMessages (final String myid, final String userid, final String imageurl){

        //list to store chat
        aChat = new ArrayList<>();

        //get dbRef to "chats" in database
        //A DatabaseReference represents a specific location in database and can be used for reading or writing data to that database location.
        DatabaseReference chtReference = FirebaseDatabase.getInstance().getReference("Chats");
        chtReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //clear to make sure previous data is not still left in the array list
                aChat.clear();
                chatLogList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(dataSnapshot.exists()) {
                        Chat chat = snapshot.getValue(Chat.class);

                        //if current users ID and chatting partner's ID exists..
                        if (myid != null && userid != null) {

                            //and if current user's ID is the message receiver AND the chatting partner's ID is the sender
                            //OR
                            //if current user's ID is the sender AND if the chatting partner ID is the message receiver
                            if (myid.equals(chat.getReceiver()) && userid.equals(chat.getSender()) || myid.equals(chat.getSender()) && userid.equals(chat.getReceiver()) ) {

                                //and, if the message receiver's status value is the sender's ID.. (meaning that the receiver has entered the chat room with the sender)
                                if(fbUser.getUid().equals(statusValue)) {
                                    //do nothing

                                    //Toast.makeText(getApplicationContext(), "working", Toast.LENGTH_SHORT).show();

                                }

                                //..add message to the aChat list;
                                aChat.add(chat);
                                //add key to list so the message can be deleted later.
                                chatLogList.add(snapshot.getKey());
                                //determines whether chat was deleted with the endchat button or if its empty bc they entered a new chat
                                chatContent = false;


                            }
                        }

                        //pass the message and image through MessageAdapter
                        messageAdapter = new MessageAdapter(getApplicationContext(), aChat, imageurl);
                        //and display it using the appropriate layout (which is on the right side if sender)
                        recyclerView.setAdapter(messageAdapter);

                    }


                }

                //code sourced from Link2me at https://link2me.tistory.com/1524

                //Added because input contents are obscured when the keyboard window comes up on the chat screen
                final SoftKeyboard softKeyboardDetector = new SoftKeyboard(chatActivity.this);
                addContentView(softKeyboardDetector, new FrameLayout.LayoutParams(-1, -1));

                softKeyboardDetector.setOnShownKeyboard(new SoftKeyboard.OnShownKeyboardListener() {
                    @Override
                    public void onShowSoftKeyboard() {

                        //When the keyboard appears, the page scrolls up automatically so the last input of the chat window is displayed above the keyboard and therefore visible
                        recyclerView.scrollToPosition(aChat.size()-1);
                    }

                });

                //if the chat list size is 0, ie there are no messages
                if(aChat.size()== 0){

                    //set chat layout
                    messageAdapter = new MessageAdapter(getApplicationContext(), aChat, imageurl);
                    recyclerView.setAdapter(messageAdapter);

                    //and if chatContent is false for the teacher (so the student logged out or ended chat from their end)..
                    if(classification.equals("teacher") && chatContent == false) {

                        //..close the chat window on the teachers screen.
                        finish();
                    }

                    //else if chat messages still exist
                }else{
                    //make the page scroll automatically so the last input of the chat window is displayed above the keyboard
                    recyclerView.scrollToPosition(aChat.size()-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //back button
    public void onBackPressed() {

        //if the user is a student and messages exist in the chat
        if(classification.equals("student") && aChat.size()!= 0) {

            //show alert dialog asking if they want to end and delete the chat
            AlertDialog.Builder builder = new AlertDialog.Builder(chatActivity.this);
            builder.setTitle("chat delete"); //AlertDialog title
            builder.setMessage("Are you sure you want to end chat? The chat will be deleted."); //AlertDialog Message

            //if yes is selected
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                    //get dbRef to "chats" in database
                    //A DatabaseReference represents a specific location in database and can be used for reading or writing data to that database location.
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");

                    //When reading the chat contents, save the key value in the chatLogList

                    //read the chat contents and
                    for (int i = 0; i < chatLogList.size(); i++) {
                        if (chatLogList.get(i) != null) {
                            //delete the chat contents when clicking the endchat button.
                            ref.child(chatLogList.get(i)).removeValue();


                        }
                    }
                    //delete the student's information from the ChatList database
                    if (classification.equals("student")) {
                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("ChatList").child(offUserid);
                        ref2.child(fbUser.getUid()).removeValue();

                    } else {
                        //If there is no chat content, just exit without deleting
                    }

                    //Log.e("Error", "chatActivity367"+classification);

                    finish();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {//no clicked
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    //do nothing, stay on chat page
                }
            });

            AlertDialog alertDialog = builder.create();

            alertDialog.show();

        }else{
            //Log.e("Error", "chatActivity383"+classification);
            if(classification.equals("student")){
                //if you click back button and there are no messages sent yet, it only deletes senders ID from ChatList.
                // (no need to have alert dialog because there are no messages to delete)
                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("ChatList").child(offUserid);
                ref2.child(fbUser.getUid()).removeValue();
            }

            finish();
        }
    }




    @Override
    protected void onPause() {
        super.onPause();

        if(fbUser.getUid() != null) {
            //change status to "non chat target" when user is not actively in the chat page/window
            if (classification.equals("student")) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Students").child(fbUser.getUid());
                reference.child("status").setValue("no chat target");


            } else if (classification.equals("teacher")) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Teachers").child(fbUser.getUid());
                reference.child("status").setValue("no chat target");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //when active in the chat page/window
        if(classification.equals("student")) {
            //show the teachers id in the students status
            dbRef2 = FirebaseDatabase.getInstance().getReference("Teachers").child(offUserid);
        } else if (classification.equals("teacher")) {
            //show the students id in the teachers status
            dbRef2 = FirebaseDatabase.getInstance().getReference("Students").child(offUserid);
        }

        //The number of times you call the singleEventValueListener, it get triggers once every time it is called.
        //(While on the other hand addValueEventListener() fetches the value every time the value is changed in your firebase realtime DB node to which it is referencing.)
        dbRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Teachers user = dataSnapshot.getValue(Teachers.class);

                if (user != null) {
                    //show the teachers id in the students status
                    if (classification.equals("student")) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Students").child(fbUser.getUid());
                        reference.child("status").setValue(offUserid);

                        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(user.getNum());//stop the notification from appearing (bc they're already in the chat window, there's no point in having a notif pop up)
                    }
                    //show the students id in the teachers status
                    else if (classification.equals("teacher")) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Teachers").child(fbUser.getUid());
                        reference.child("status").setValue(offUserid);

                        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(user.getNum());//stop the notification from appearing (bc they're already in the chat window, there's no point in having a notif pop up)

                    }
                    if(fbUser.getUid().equals(user.getStatus())) {

                        //If sender's ID is in the recievers status (meaning that they are in the chat window and has read the message), call seenMessage()
                        seenMessage(offUserid);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}

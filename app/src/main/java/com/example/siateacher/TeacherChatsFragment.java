// Code adapted from tutorial 'Chat App with Firebase' by KOD Dev.
// Tutorial found at: https://www.youtube.com/watch?v=BJkzVc2D0iY&list=PLzLFqCABnRQftQQETzoVMuteXzNiXmnj8&index=6

package com.example.siateacher;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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


public class TeacherChatsFragment extends Fragment {

    private RecyclerView usersListRecycler;
    private usersActivity usersActivity;
    private List<Teachers> teacherUsers;
    private List<Chat> aChat;

    private View aMainView;

    private NotificationManager notificationManager;
    private Notification.Builder notification;

    FirebaseUser fbUser;

    private String CHANNEL_ID;

    public TeacherChatsFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get current user
        fbUser = FirebaseAuth.getInstance().getCurrentUser();

        //create new array list for users
        teacherUsers = new ArrayList<>();
        //create new array list for chat
        aChat = new ArrayList<>();
        //create notifications
        createNotificationChannel();
        //Load list of teachers to use for chat
        readUsers();
        readChatForNotification();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        aMainView = inflater.inflate(R.layout.fragment_teacher_chats, container, false);
        usersListRecycler = aMainView.findViewById(R.id.fragment_chat);
        usersListRecycler.setHasFixedSize(true);
        usersListRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        return aMainView;
    }

    //(shows student list on teachers screen)
    private void readUsers() {

        //gets the user id of the current user within "Chatlist" in the database of firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatList").child(fbUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teacherUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Teachers user = snapshot.getValue(Teachers.class);

                    //Show user only if the student's status value shows they are online and chatting with teacher
                    // (it'll say "status: [teacher id]" on firebase database)
                    if("online".equals(user.getStatus())) {
                        teacherUsers.add(user);
                    }
                }
                usersActivity = new usersActivity(getContext(), teacherUsers);
                usersListRecycler.setAdapter(usersActivity);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //reads chat for notifications
    private void readChatForNotification() {

        //point to chats in database
        DatabaseReference chtReference = FirebaseDatabase.getInstance().getReference("Chats");

        chtReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                Chat chat = dataSnapshot.getValue(Chat.class);

                if (fbUser.getUid() != null) {
                    //Get only the unread data
                    if (fbUser.getUid().equals(chat.getReceiver()) && chat.isIsseen() == false) {
                        String mMessage;
                        String mId;

                        //the chatting partners ID to be shown in Notification
                        mId = chat.getSender();
                        //the content of the chat to be shown in Notification
                        mMessage = chat.getMessage();

                        //calls notification function, passing through the senders ID + content of msg
                        chatNotification(mId, mMessage);

                    } else {

                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}

        });

    }

    //Channel setting (after android 8.0, you must create a channel to use Notification)
    private void createNotificationChannel(){
        //version check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            //create NotificationChannel
            NotificationChannel channel = new NotificationChannel("channel", "SIATeacher", NotificationManager.IMPORTANCE_HIGH);
            notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            //Register NotifcationChannel created in notificationManager
            notificationManager.createNotificationChannel(channel);

            //Enter the registered channel ID.
            // Used in NotificationCompat.Builder.
            CHANNEL_ID = notificationManager.getNotificationChannel("channel").getId();

        }

    }



    private void chatNotification(final String notifyId, final String mMessage) {

        if(true){
            //Since a different number (int) must be specified for each notification, a serial number is created in num
            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Students").child(notifyId);

            //The number of times you call the singleEventValueListener, it get triggers once every time it is called.
            //(While on the other hand addValueEventListener() fetches the value every time the value is changed in your firebase realtime DB node to which it is referencing.)
            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Teachers user = dataSnapshot.getValue(Teachers.class);
                    if(user != null) {

                        //Setting the Intent value to go to the corresponding chat screen when notification is clicked
                        Intent chat_intent = new Intent(getContext(), chatActivity.class);
                        chat_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP); //Prevents multiple chatActivity windows from being opened

                        //pass teacher "id" to chatActivity. Value of id is: user.getId()
                        chat_intent.putExtra("id", user.getId());
                        //pass "classification" to chatActivity. the value is "teacher"
                        //so it checks if teacher entered the chat
                        chat_intent.putExtra("classification", "teacher");
                        //Needed activity for when a notification is clicked.
                        PendingIntent cont = PendingIntent.getActivity(getActivity().getApplicationContext(), (int) (System.currentTimeMillis() / 1000), chat_intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID);
                        } else {
                            builder = new NotificationCompat.Builder(getContext());
                        }
                        builder.setSmallIcon(R.drawable.ic_notification)//the icon image
                                .setContentTitle("Chat notification")//title
                                .setContentText(notifyId + ": " + mMessage)//message content
                                .setPriority(NotificationCompat.PRIORITY_MAX)//Importance (set as max priority so the notification is at the top)
                                .setDefaults(Notification.DEFAULT_ALL) //sound, vibration settings
                                .setContentIntent(cont) //PendingIntent value
                                .setAutoCancel(true);//If true, notification will disappear automatically when clicked

                        notificationManager.notify(user.getNum(), builder.build());//If user.getNum () value is the same, only one notification is generated
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }

    }




    public void onPause() {
        super.onPause();
        //determines if user is in the chat activity or not
        //when user presses the home button (i.e. leaves the app for a moment w/o logging out)..
        //..on firebase database it will show their status as "no chat target"
        if(fbUser.getUid() != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Teachers").child(fbUser.getUid());
            reference.child("status").setValue("no chat target");
        }

    }
    public void onResume() {
        super.onResume();
        //same as onPause()
        //determines if user is in the chat activity or not
        if(fbUser.getUid() != null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Teachers").child(fbUser.getUid());
            reference.child("status").setValue("no chat target");
        }

    }

}

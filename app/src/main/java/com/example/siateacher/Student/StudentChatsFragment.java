// Code adapted from tutorial 'Chat App with Firebase' by KOD Dev.
// Tutorial found at: https://www.youtube.com/watch?v=BJkzVc2D0iY&list=PLzLFqCABnRQftQQETzoVMuteXzNiXmnj8&index=6

package com.example.siateacher.Student;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.siateacher.Chat;
import com.example.siateacher.R;
import com.example.siateacher.Teachers;
import com.example.siateacher.chatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentChatsFragment extends Fragment {

    //creating variables
    private List<Teachers> teacherUsers;  //list of Teachers class
    private List<Chat> aChat;

    private View aMainView;

    private Button startChatButton;
    int mListCnt = 0; //Value to start from in order to repeat the teacher list

    private FirebaseAuth fbAuth;
    private DatabaseReference dbRef;

    private FirebaseAuth.AuthStateListener authStateListener;

    private NotificationManager notificationManager;
    private Notification.Builder notification;

    private String CHANNEL_ID;


    //If you attach an AuthStateListener you will get a callback every time the underlying token state changes.
    private boolean AuthState =false;


    public StudentChatsFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        teacherUsers = new ArrayList<>();
        aChat = new ArrayList<>();

        //Initialize the FirebaseAuth
        initFBAuthentication();
        signInAnonymously();
        //FirebaseAuth state check
        initFBAuthState();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        aMainView = inflater.inflate(R.layout.fragment_student_home, container, false);
        startChatButton = aMainView.findViewById(R.id.student_startchat);

        return aMainView;

    }
    public void onStart(){
        super.onStart();

        //addAuthStateListener is called when there is a change in the authentication state.
        fbAuth.addAuthStateListener(authStateListener);
        startChatButton = aMainView.findViewById(R.id.student_startchat);

        //click start chat button
        startChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create intent to send user to chat activity
                Intent chat_intent = new Intent(getContext(), chatActivity.class);

                //Check whether Database [Teachers] size and mListCnt are the same
                if (mListCnt == teacherUsers.size()) {
                    //If it is the same, it initializes the mListCnt value to 0
                    mListCnt = 0;

                }

                //pass student "id" to chatActivity. (Value of id is: teacherUsers.get(mListCnt).getId()
                chat_intent.putExtra("id", teacherUsers.get(mListCnt).getId());
                //pass "classification" to chatActivity. the value is "student"
                //purpose is to check whether the student or the teacher entered the chat
                chat_intent.putExtra("classification", "student");
                //send user to chat activity
                getContext().startActivity(chat_intent);

                //Increases with each click. (goes through the list of teachers)
                mListCnt++;
            }

        });
    }
    private void initFBAuthentication() {
        //Initialize the FirebaseAuth instance
        fbAuth = FirebaseAuth.getInstance();
    }

    //FirebaseAuth state check
    private void initFBAuthState() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            //OnAuthStateChanged gets invoked in the UI thread on changes in the authentication state:
            //- Right after the listener has been registered
            //- When a user is signed in (this)
            //- When the current user is signed out
            //- When the current user changes
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //get current user
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                //if user exists
                if (firebaseUser != null) {
                    //make auth state as true (and thus activates "start chat" button)
                    AuthState = true;

                } else {
                    //if user not found, set as false
                    AuthState = false;
                    //and disable start chat button
                    startChatButton.setEnabled(false);
                }

            }
        };
    }

    //anonymous student user login
    private void signInAnonymously() {
        fbAuth.signInAnonymously()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //After successfully creating an anonymous user, put student information in Database [Students]
                        if (task.isSuccessful()) {

                            //get current user
                            FirebaseUser user = fbAuth.getCurrentUser();

                            //student id into string
                            String studentsid = user.getUid();

                            long temp1 = System.currentTimeMillis(); //Returns the current current date and time in milliseconds.
                            String temp2 =String.valueOf(temp1).substring(4); //convert the time into string
                            int num = Integer.parseInt(temp2); //convert string to int

                            //store the database reference of the current student user
                            dbRef = FirebaseDatabase.getInstance().getReference().child("Students").child(studentsid);

                            //store details in hash map, by adding values to the child nodes in db
                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("id", studentsid);
                            userMap.put("image", "default");
                            userMap.put("name", "Student");
                            userMap.put("status", "online");
                            userMap.put("num", num); //creates a unique serial number for use in notifications

                            dbRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        //create notification
                                        createNotificationChannel();
                                        //Load list of teachers to use for chat
                                        readUsers();
                                        //reads chat for unread messages, to create notification
                                        readChatForNotification();
                                    }
                                }
                            });

                        } else {

                        }

                    }
                });
    }

    //reads list of teachers (stored in database under "teachers")
    private void readUsers() {

        //point to users database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Teachers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teacherUsers.clear();

                //retrieve value of users class
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    //get snapshot of users class
                    Teachers user = snapshot.getValue(Teachers.class);

                    //add the user
                    teacherUsers.add(user);

                }

                //Activate the start chat button (ONLY activates the chat button once getting user information)
                startChatButton.setEnabled(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readChatForNotification() { //reads chat for notifications

        //point to chats in database
        DatabaseReference chtReference = FirebaseDatabase.getInstance().getReference("Chats");

        chtReference.addChildEventListener(new ChildEventListener() {

            @Override
            //onChildAdded: is triggered when any child node changes value
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                Chat chat = dataSnapshot.getValue(Chat.class);

                //if user id exists
                if (fbAuth.getUid() != null) {

                    //"if isseen==false" - therfore, gets only the unread data.
                    if (fbAuth.getUid().equals(chat.getReceiver()) && chat.isIsseen() == false) {

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

            //Since a different number (int) must be specified for each notification, a serial number is created in num (line 184)
            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Teachers").child(notifyId);

            //The number of times you call the singleEventValueListener, it get triggers once every time it is called.
            //**(While on the other hand addValueEventListener() fetches the value every time the value is changed in your firebase realtime DB node to which it is referencing.)
            mReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Teachers user = dataSnapshot.getValue(Teachers.class);
                    if(user != null) {

                        //Setting the Intent value to go to the corresponding chat screen when notification is clicked
                        Intent chat_intent = new Intent(getContext(), chatActivity.class);
                        chat_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_SINGLE_TOP);//Prevents multiple chatActivity windows from being opened

                        //get user id
                        chat_intent.putExtra("id", user.getId());
                        chat_intent.putExtra("classification", "student"); //Value to check whether the student entered the chat
                        //Needed activity for when a notification is clicked.
                        PendingIntent cont = PendingIntent.getActivity(getActivity().getApplicationContext(), (int) (System.currentTimeMillis() / 1000), chat_intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        //notification builder
                        NotificationCompat.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID);
                        } else {
                            builder = new NotificationCompat.Builder(getContext());
                        }
                        builder.setSmallIcon(R.drawable.ic_notification)//the icon image
                                .setContentTitle("Chat notification")//title
                                .setContentText(user.getName() + ": " + mMessage)//get message content with the senders name
                                .setPriority(NotificationCompat.PRIORITY_MAX)//Importance (set as max priority so the notification is at the top)
                                .setDefaults(Notification.DEFAULT_ALL) // sound, vibration settings
                                .setContentIntent(cont) //PendingIntent value
                                .setAutoCancel(true);//If true, notification will disappear automatically when clicked

                        //Build notification
                        // If user.getNum () value is the same, only one notification is generated
                        notificationManager.notify(user.getNum(), builder.build());

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
        if(AuthState) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Students").child(fbAuth.getUid());
            reference.child("status").setValue("no chat target");
        }
    }
    public void onResume() {
        super.onResume();
        //same as onPause()
        //determines if user is in the chat activity or not
        if(AuthState) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Students").child(fbAuth.getUid());
            reference.child("status").setValue("no chat target");
        }
    }
}

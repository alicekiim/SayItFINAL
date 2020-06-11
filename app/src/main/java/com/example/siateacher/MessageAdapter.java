// Code adapted from tutorial 'Chat App with Firebase' by KOD Dev.
// Tutorial found at: https://www.youtube.com/watch?v=1mJv4XxWlu8&list=PLzLFqCABnRQftQQETzoVMuteXzNiXmnj8&index=8

package com.example.siateacher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

//import static com.example.siateacher.R.layout.chat_item_left;


//ALICE CHANGE ALL THESE VARS

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_VIEW_LEFT = 0;
    public static final int MSG_VIEW_RIGHT = 1;

    private Context aContext;

    private List<Chat> aChat; //list to hold messages
    private String imageURL;

    FirebaseUser fbUser;

    public MessageAdapter (Context mContext, List<Chat> mChat, String imageurl){
        this.aChat = mChat;
        this.aContext = mContext;
        this.imageURL = imageurl;
    }

    @NonNull
    @Override
    //adding the left and right layout
    public MessageAdapter.ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_VIEW_RIGHT) {
            View view = LayoutInflater.from(aContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(aContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder (@NonNull MessageAdapter.ViewHolder holder, int position){
        //get the position of the message in the list and store in "chat"
        Chat chat = aChat.get(position);

        //get the message to show in the chat window
        holder.show_message.setText(chat.getMessage());

        //if the image url is default, load ic_launcher image
        if(imageURL.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{
            //else, if a unique profile image url is found, load that into the holder
            Glide.with(aContext).load(imageURL).into(holder.profile_image);
        }
        //checks for last message
        if (position == aChat.size()-1){
            if(chat.isIsseen()){//if last message is read
                //mark it as seen
                holder.msg_seen.setText("Seen");
            }else{//else if last message not yet read
                //mark it as delivered
                holder.msg_seen.setText("Delivered");
            }
        }else{
            //hide seen/delivered status for older messages
           holder.msg_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount(){
        //returns size of the message list
        return aChat.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message; //the message
        public ImageView profile_image;
        public TextView msg_seen;

        public ViewHolder (View itemView){
            super(itemView);

            //the layout
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            msg_seen = itemView.findViewById(R.id.txt_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //get current user and store it in "fbUser"
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        //if message sender equals fbUser(and is therefore, the current user), display their messages on the right
        if(aChat.get(position).getSender().equals(fbUser.getUid())){
            return MSG_VIEW_RIGHT;
        }else{
            //and display the chat partners messages on the left
            return MSG_VIEW_LEFT;
        }
    }
}

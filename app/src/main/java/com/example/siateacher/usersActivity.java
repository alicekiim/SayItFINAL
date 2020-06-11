// Code adapted from tutorial 'Chat App with Firebase' by KODDev.
// Tutorial found at: https://www.youtube.com/watch?v=WsyJlFjJkyE&list=PLzLFqCABnRQftQQETzoVMuteXzNiXmnj8&index=5

//this activity is to list the students in TeachersChatFragment
package com.example.siateacher;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class usersActivity extends RecyclerView.Adapter<usersActivity.ViewHolder> {

    private Context mContext;
    private List<Teachers> mUsers;


    public usersActivity (Context mContext, List<Teachers> mUsers){
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(mContext).inflate(R.layout.users_single_layout, parent, false);
        return new usersActivity.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull ViewHolder holder, int position){

        final Teachers user = mUsers.get(position);
        if(mUsers.size() !=0) {//if there ARE users in the database

            //get the students name
            holder.name.setText(user.getId());

            //and get their image, if no unique image exists, use default image
            if("default".equals(user.getImage())){
                holder.profile_image.setImageResource(R.mipmap.ic_launcher);
            }else {
                Glide.with(mContext).load(user.getImage()).into(holder.profile_image);
            }
        }

        //and direct to start activity if clicked
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent chat_intent = new Intent(mContext, chatActivity.class);
                chat_intent.putExtra("id", user.getId());
                chat_intent.putExtra("classification", "teacher"); //checks if teacher entered the chat

                mContext.startActivity(chat_intent);
            }

        });

    }

    @Override
    public int getItemCount(){
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public ImageView profile_image;

        //view object created
        public ViewHolder (View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.user_single_name);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }
}
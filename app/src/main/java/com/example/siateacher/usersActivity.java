package com.example.siateacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class usersActivity extends RecyclerView.Adapter<usersActivity.ViewHolder> {

    private Context mContext;
    private List<Users> mUsers;

    public usersActivity (Context mContext, List<Users> mUsers){
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

        final Users user = mUsers.get(position);
        holder.name.setText(user.getName());
        holder.status.setText(user.getStatus());

        if(user.getImage().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(mContext).load(user.getImage()).into(holder.profile_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent chat_intent = new Intent(mContext, chatActivity.class);
                chat_intent.putExtra("id", user.getId());
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
        public TextView status;
        public ImageView profile_image;

        public ViewHolder (View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.user_single_name);
            status = itemView.findViewById(R.id.user_single_status);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }
}
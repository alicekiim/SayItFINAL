package com.example.siateacher;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

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
        if(mUsers.size() !=0) {
            holder.name.setText(user.getName());
            //holder.name.setText(user.getId());
            holder.status.setText(user.getStatus());

            if("default".equals(user.getImage())){
                holder.profile_image.setImageResource(R.mipmap.ic_launcher);
            }else {
                Glide.with(mContext).load(user.getImage()).into(holder.profile_image);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent chat_intent = new Intent(mContext, chatActivity.class);
                chat_intent.putExtra("id", user.getId());
                chat_intent.putExtra("classification", "teacher"); //학생으로 채팅에 들어간건지 선생으로 들어간건지 체크하기 위한 값

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
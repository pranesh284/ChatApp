package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.MessageActivity;
import com.example.chatapp.R;
import com.example.chatapp.model.Chat;
import com.example.chatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    private Context mContext;
    private List<Chat> mChats;
    private String imageUrl;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context mContext,List<Chat> mChats,String imageUrl){
        this.mChats = mChats;
        this.mContext = mContext;
        this.imageUrl = imageUrl;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView profileImage;
        public  TextView txt_seen;

        public ViewHolder(View itemView){
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profileImage = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
        }
    }
    @NonNull
    @NotNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==MSG_TYPE_RIGHT)
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
        else
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
        return new ViewHolder(view);

    }
    @Override
    public void onBindViewHolder(@NonNull @NotNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = mChats.get(position);

        holder.show_message.setText(chat.getMessage());
        if(imageUrl.equals("default"))
            holder.profileImage.setImageResource(R.mipmap.ic_launcher);
        else
            Glide.with(mContext).load(imageUrl).into(holder.profileImage);

        if(position==mChats.size()-1){
            if(chat.isIsseen())
                holder.txt_seen.setText("seen");
            else
                holder.txt_seen.setText("delivered");
        }
        else holder.txt_seen.setVisibility(View.GONE);
    }
    @Override
    public int getItemCount() {
        return mChats.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChats.get(position).getSender().equals(firebaseUser.getUid()))
            return MSG_TYPE_RIGHT;
        else return MSG_TYPE_LEFT;
    }
}


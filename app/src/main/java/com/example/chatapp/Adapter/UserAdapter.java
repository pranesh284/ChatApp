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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    private Context mContext;
    private List<User> mUsers;
    private boolean ischat;
    String theLastMessage;

    public UserAdapter(Context mContext,List<User> mUsers,boolean ischat){
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.ischat = ischat;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public TextView last_msg;
        public ImageView profileImage;
        public ImageView img_on;
        public ImageView img_off;

        public ViewHolder(View itemView){
            super(itemView);
        username = itemView.findViewById(R.id.username);
        last_msg = itemView.findViewById(R.id.last_msg);
        profileImage = itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
        }
    }
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull @NotNull UserAdapter.ViewHolder holder, int position) {
        User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
         if(user.getImageUrl().equals("default"))
             holder.profileImage.setImageResource(R.mipmap.ic_launcher);
         else
             Glide.with(mContext).load(user.getImageUrl()).into(holder.profileImage);

        if(ischat)
            lastMessage(user.getId(), holder.last_msg);
        else
            holder.last_msg.setVisibility(View.GONE);

         if(ischat) {
             if (user.getStatus().equals("online")) {
                 holder.img_on.setVisibility(View.VISIBLE);
                 holder.img_off.setVisibility(View.GONE);
             } else {
                 holder.img_off.setVisibility(View.VISIBLE);
                 holder.img_on.setVisibility(View.GONE);
             }
         }
         else {
             holder.img_on.setVisibility(View.GONE);
             holder.img_off.setVisibility(View.GONE);
         }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid",user.getId());
                mContext.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    private void lastMessage(String userid,TextView last_msg)
    {
        theLastMessage = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getInstance().
                getReference("Chats");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ss:snapshot.getChildren()) {
                    Chat chat = ss.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)
                    || chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid()))
                        theLastMessage = chat.getMessage();
                }
                switch(theLastMessage){
                    case "default":
                        last_msg.setText("No Message");
                        break;
                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }
                theLastMessage="default";
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

}

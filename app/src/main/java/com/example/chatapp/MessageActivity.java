package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.Adapter.MessageAdapter;
import com.example.chatapp.Fragments.APIService;
import com.example.chatapp.Notifications.Client;
import com.example.chatapp.Notifications.Data;
import com.example.chatapp.Notifications.MyResponse;
import com.example.chatapp.Notifications.Sender;
import com.example.chatapp.Notifications.Token;
import com.example.chatapp.model.Chat;
import com.example.chatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
CircleImageView profile_image;
TextView username;

FirebaseUser firebaseUser;
DatabaseReference reference;

ImageButton btn_send;
EditText text_send;
String userid;
MessageAdapter messageAdapter;
List<Chat> mchats;
RecyclerView recyclerView;
Intent intent;
APIService apiService ;
ValueEventListener  listener;

boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);


    profile_image = findViewById(R.id.profile_image);
    username = findViewById(R.id.username);
    text_send = findViewById(R.id.text_send);
    btn_send = findViewById(R.id.btn_send);

    recyclerView =findViewById(R.id.recycler_view);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
    linearLayoutManager.setStackFromEnd(true);
    recyclerView.setLayoutManager(linearLayoutManager);

    intent = getIntent();
     userid=intent.getStringExtra("userid");
    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    btn_send.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            notify=true;
            String msg = text_send.getText().toString();
            if (!msg.equals(""))
                sendMessage(firebaseUser.getUid(), userid, msg);
            else
                Toast.makeText(MessageActivity.this, "You cant send empty message", Toast.LENGTH_SHORT);
            text_send.setText("");
        }
    });




    reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            User user = snapshot.getValue(User.class);
            username.setText(user.getUsername());
            if (user.getImageUrl().equals("default"))
                profile_image.setImageResource(R.mipmap.ic_launcher);
            else {
                Glide.with(getApplicationContext()).load(user.getImageUrl()).into(profile_image);
            }
            readMessage(firebaseUser.getUid(),userid,user.getImageUrl());
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {

        }
    });
seenMessage(userid);
    }
    private void seenMessage (String userid){
        reference =FirebaseDatabase.getInstance().getReference("Chats");
        listener= reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ss: snapshot.getChildren()) {
                    Chat chat = ss.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> hasMessage = new HashMap<>();
                        hasMessage.put("isseen", true);
                        ss.getRef().updateChildren(hasMessage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    private void sendMessage(String sender,String receiver,String message)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isseen",false);

        ref.child("Chats").push().setValue(hashMap);
        DatabaseReference chatref = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(firebaseUser.getUid()).child(userid);

        chatref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                    chatref.child("id").setValue(userid);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    final String msg = message;
    reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
    reference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
            User user = snapshot.getValue(User.class);
            if(notify)
                sendNotification(receiver,user.getUsername(),msg);
            notify=false;
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {

        }
    });
    }

    private void sendNotification(String receiver, String username, String msg) {

        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ss:snapshot.getChildren()) {
                    Token token = ss.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username + ": " + msg, "New message",userid);
                    Sender sender = new Sender(data,token.getToken());
                    apiService.sendNotifacation(sender)
                    .enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.code()==200)
                                if(response.body().success == 1){
                                    Toast.makeText(MessageActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                                }

                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }


    private void readMessage(String myid,String userid, String imageUrl ){
        mchats = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mchats.clear();
                for(DataSnapshot ss:snapshot.getChildren()){
                    Chat chat = ss.getValue(Chat.class);
                    if (chat.getReceiver().equals(myid)
                            && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) &&
                                    chat.getSender().equals(myid))
                        mchats.add(chat);
                    messageAdapter = new MessageAdapter(MessageActivity.this,mchats,imageUrl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = (SharedPreferences.Editor) getSharedPreferences("PREFS",MODE_PRIVATE).edit();
        editor.putString("currentuser",userid);
        editor.apply();
    }
    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(listener);
        status("offline");
        currentUser("none");
    }

}
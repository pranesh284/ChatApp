package com.example.chatapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Notifications.Token;
import com.example.chatapp.R;
import com.example.chatapp.model.Chat;
import com.example.chatapp.model.ChatList;
import com.example.chatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    private List<ChatList> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        usersList = new ArrayList<>();

        reference =FirebaseDatabase.getInstance().getReference("Chatlist").
                child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot ss:snapshot.getChildren())
                {
                    ChatList chatList = ss.getValue(ChatList.class);
                    usersList.add(chatList);
                }
                chatlist();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

//        reference = FirebaseDatabase.getInstance().getReference("Chats");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                usersList.clear();
//                for (DataSnapshot ss : snapshot.getChildren()) {
//                    Chat chat = ss.getValue(Chat.class);
//                    if (chat.getSender().equals(firebaseUser.getUid()))
//                        usersList.add(chat.getReceiver());
//                    if (chat.getReceiver().equals(firebaseUser.getUid()))
//                        usersList.add(chat.getSender());
//                }
//                readChats();
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        });

        updateToken(FirebaseMessaging.getInstance().getToken().getResult());
        return view;
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1=new Token(token);
        reference.child((firebaseUser.getUid())).setValue(token1);
    }

    private void chatlist() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mUsers.clear();
                for(DataSnapshot ss:snapshot.getChildren()) {
                    User user = ss.getValue(User.class);
                    for (ChatList chatList : usersList)
                        if (user.getId().equals(chatList.getId()))
                            mUsers.add(user);
                }
                userAdapter = new UserAdapter(getContext(), mUsers, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    //readchats
//    private void readChats() {
//        mUsers = new ArrayList<>();
//
//        reference = FirebaseDatabase.getInstance().getReference("Users");
//        reference.addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                mUsers.clear();
//                for (DataSnapshot ss : snapshot.getChildren()) {
//                    User user = ss.getValue(User.class);
//
//                    for (String id : usersList) {
//                        if (user.getId().equals(id))
//                            if (mUsers.size() != 0) {
//                                for (User user1 : mUsers)
//                                    if (!user.getId().equals(user1.getId()))
//                                        mUsers.add(user);
//                            }else mUsers.add(user);
//                    }
//                }
//                userAdapter = new UserAdapter(getContext(),mUsers,true);
//                recyclerView.setAdapter(userAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        });
//    }


}
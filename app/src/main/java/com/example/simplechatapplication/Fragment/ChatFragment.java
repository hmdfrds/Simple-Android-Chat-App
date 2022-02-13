package com.example.simplechatapplication.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplechatapplication.Adapter.ChatAdapter;
import com.example.simplechatapplication.AuthActivity;
import com.example.simplechatapplication.ChatActivity;
import com.example.simplechatapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;


public class ChatFragment extends Fragment {

    MaterialIconView fabLogout;
    FirebaseAuth firebaseAuth;
    RecyclerView rvChat;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    ChatAdapter chatAdapter;
    ArrayList<String> chats ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_chat, container, false);


        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        fabLogout = view.findViewById(R.id.fabLogout);
        rvChat = view.findViewById(R.id.rvChat);

        chats = new ArrayList<>();

        firebaseFirestore.collection("users").document(firebaseUser.getUid()).addSnapshotListener((value, error) -> {
            if(value.get("chats") != null){
                Log.d("TAG", "onEvent: ");
                chats = (ArrayList<String>) value.get("chats");
            }

            chatAdapter = new ChatAdapter(view.getContext(), chats, item -> {
            Intent intent = new Intent(view.getContext(), ChatActivity.class);
            intent.putExtra("documentId", item);
            startActivity(intent);
            });
            rvChat.setLayoutManager(new LinearLayoutManager(view.getContext()));
            rvChat.setAdapter(chatAdapter);
        });


        fabLogout.setOnClickListener(v -> {
            firebaseAuth.signOut();
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        return view;

    }
}
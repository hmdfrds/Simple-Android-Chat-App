package com.example.simplechatapplication.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.simplechatapplication.Adapter.FriendAdapter;
import com.example.simplechatapplication.ChatActivity;
import com.example.simplechatapplication.R;
import com.example.simplechatapplication.SearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendsFragment extends Fragment {

    Button btnSearchAccount;
    EditText edtEmail;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    RecyclerView rvFriendList;
    FriendAdapter friendAdapter;
    ArrayList<Map<String,Object>> friendsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        friendsList = new ArrayList<>();

        rvFriendList = view.findViewById(R.id.rvFriendList);
        btnSearchAccount = view.findViewById(R.id.btnSearchAccount);
        firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("friends").addSnapshotListener((value, error) -> {
            if(!value.isEmpty()){
                for (DocumentSnapshot ds : value){
                    friendsList.add(ds.getData());
                }

            }

            friendAdapter = new FriendAdapter(view.getContext(), friendsList, item -> {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("receiverId", item.get("id").toString());
                intent.putExtra("documentId", item.get("chatId").toString());
                startActivity(intent);
            });
            rvFriendList.setLayoutManager(new LinearLayoutManager(view.getContext()));
            rvFriendList.setAdapter(friendAdapter);
        });


        btnSearchAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getContext(), SearchActivity.class));
            }
        });

        return view;
    }


}
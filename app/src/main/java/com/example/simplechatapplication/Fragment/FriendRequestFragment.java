package com.example.simplechatapplication.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.simplechatapplication.Adapter.FriendAdapter;
import com.example.simplechatapplication.Adapter.FriendRequestAdapter;
import com.example.simplechatapplication.ChatActivity;
import com.example.simplechatapplication.R;
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


public class FriendRequestFragment extends Fragment {

    RecyclerView rvFriendRequest;
    FriendRequestAdapter friendRequestAdapter;
    ArrayList<DocumentSnapshot> friendRequestList;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    int bsize = 0;
    int csize = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_friend_request, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        rvFriendRequest = view.findViewById(R.id.rvFriendRequestList);
        friendRequestList = new ArrayList<>();
        friendRequestAdapter = new FriendRequestAdapter(view.getContext(), friendRequestList, item -> {

        });
        rvFriendRequest.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvFriendRequest.setAdapter(friendRequestAdapter);
        firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("requestStatus").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                csize = value.size();
                Log.e("csize",String.valueOf(csize)  );
                Log.e("bsize",String.valueOf(bsize)  );
                if(bsize != csize){
                    friendRequestList.clear();
                    for(DocumentSnapshot ds : value){
                        if(ds.getString("status").equals("waiting_approval")){
                            Log.e("bsize",String.valueOf(bsize)  );
                            friendRequestList.add(ds);
                        }

                    }
                    friendRequestAdapter.notifyDataSetChanged();
                    bsize = value.size();
                }


            }

        });

        return view;
    }
}
package com.example.simplechatapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    TextView txtUserId, txtUserEmail;
    Button btnSendFriendRequest;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    String userId = "";

    String friendStatus = "not_friend";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                userId= null;
            } else {
                userId= extras.getString("uid");
            }
        } else {
            userId= (String) savedInstanceState.getSerializable("uid");
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        txtUserId = findViewById(R.id.txtUserIdProfile);
        txtUserEmail = findViewById(R.id.txtUserEmailProfile);
        btnSendFriendRequest = findViewById(R.id.btnSendFriendRequestProfile);
        btnSendFriendRequest.setText("Send Friend Request");

        firebaseFirestore.collection("users").document(userId).get().addOnCompleteListener(task -> task.addOnSuccessListener(documentSnapshot -> {
            txtUserId.setText(documentSnapshot.getId());
            txtUserEmail.setText(documentSnapshot.getString("email"));
        }));

        firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("friends").document(userId).get().addOnCompleteListener(task -> task.addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                friendStatus = "friend";
                btnSendFriendRequest.setText("Remove Friend");
            }else {
                firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("requestStatus").document(userId).get().addOnSuccessListener(documentSnapshot1 -> {
                    if(documentSnapshot1.exists()){
                        if(documentSnapshot1.getString("status").equals("request_send")){
                            friendStatus = "request_send";
                            btnSendFriendRequest.setText("Cancel Request");
                        }
                        else if(documentSnapshot1.getString("status").equals("waiting_approval")){
                            friendStatus = "waiting_approval";
                            btnSendFriendRequest.setText("Waiting for Approval");
                        }
                        else {
                            friendStatus = "already_requested";
                            btnSendFriendRequest.setText("Accept");
                        }
                    }
                });
            }
        }));


        btnSendFriendRequest.setOnClickListener(v -> {
            switch (friendStatus) {
                case "friend":
                    firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("friends").document(userId).get().addOnSuccessListener(documentSnapshot -> {
                        String chatId = documentSnapshot.getString("chatId");
                        firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("friends").document(userId).delete().addOnSuccessListener(unused -> {
                            friendStatus = "not_friend";
                            btnSendFriendRequest.setText("Send Friend Request");
                            firebaseFirestore.collection("users").document(userId).collection("friends").document(firebaseUser.getUid()).delete();
                            assert chatId != null;
                            firebaseFirestore.collection("chats").document(chatId).delete();
                        });
                    });

                    break;
                case "request_send":
                    firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("requestStatus").document(userId).delete().addOnSuccessListener(unused -> {
                        friendStatus = "not_friend";
                        btnSendFriendRequest.setText("Send Friend Request");
                        firebaseFirestore.collection("users").document(userId).collection("requestStatus").document(firebaseUser.getUid()).delete();

                    });
                    break;
                case "waiting_approval": {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", userId);
                    m.put("chatId", "");
                    firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("requestStatus").document(userId).delete().addOnSuccessListener(unused -> firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("friends").document(userId).set(m).addOnSuccessListener(unused12 -> {
                        Map<String, Object> m12 = new HashMap<>();
                        m12.put("id", firebaseUser.getUid());
                        m12.put("chatId", "");
                        firebaseFirestore.collection("users").document(userId).collection("friends").document(firebaseUser.getUid()).set(m12).addOnSuccessListener(unused1 -> {
                            friendStatus = "friend";
                            btnSendFriendRequest.setText("Remove Friend");
                            firebaseFirestore.collection("users").document(userId).collection("requestStatus").document(firebaseUser.getUid()).delete();
                        });
                    }));

                    break;
                }
                default: {
                    Map<String, Object> m = new HashMap<>();
                    m.put("status", "request_send");
                    firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("requestStatus").document(userId).set(m).addOnSuccessListener(unused -> {
                        friendStatus = "request_send";
                        btnSendFriendRequest.setText("Cancel Friend Request");
                        Map<String, Object> m1 = new HashMap<>();
                        m1.put("status", "waiting_approval");
                        firebaseFirestore.collection("users").document(userId).collection("requestStatus").document(firebaseUser.getUid()).set(m1);
                    });
                    break;
                }
            }
        });
    }


}
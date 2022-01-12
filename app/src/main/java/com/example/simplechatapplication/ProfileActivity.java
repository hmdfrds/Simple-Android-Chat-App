package com.example.simplechatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
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

        firebaseFirestore.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        txtUserId.setText(documentSnapshot.getId());
                        txtUserEmail.setText(documentSnapshot.getString("email"));
                    }
                });
            }
        });

        firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("friends").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            friendStatus = "friend";
                            btnSendFriendRequest.setText("Remove Friend");
                        }else {
                            firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("requestStatus").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        if(documentSnapshot.getString("status").equals("request_send")){
                                            friendStatus = "request_send";
                                            btnSendFriendRequest.setText("Cancel Request");
                                        }
                                        else if(documentSnapshot.getString("status").equals("waiting_approval")){
                                            friendStatus = "waiting_approval";
                                            btnSendFriendRequest.setText("Waiting for Approval");
                                        }
                                        else {
                                            friendStatus = "already_requested";
                                            btnSendFriendRequest.setText("Accept");
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        Log.e("TAG", friendStatus );


        btnSendFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(friendStatus.equals("friend")){
                    firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("friends").document(userId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            friendStatus= "not_friend";
                            btnSendFriendRequest.setText("Send Friend Request");
                            firebaseFirestore.collection("users").document(userId).collection("friends").document(firebaseUser.getUid()).delete();
                        }
                    });
                }
                else if(friendStatus.equals("request_send")){
                    firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("requestStatus").document(userId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            friendStatus= "not_friend";
                            btnSendFriendRequest.setText("Send Friend Request");
                            firebaseFirestore.collection("users").document(userId).collection("requestStatus").document(firebaseUser.getUid()).delete();

                        }
                    });
                }
                else if(friendStatus.equals("waiting_approval")){
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", userId);
                    m.put("chatId","");
                    firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("requestStatus").document(userId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("friends").document(userId).set(m).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    friendStatus= "friend";
                                    btnSendFriendRequest.setText("Remove Friend");
                                    Map<String, Object> m = new HashMap<>();
                                    m.put("id", firebaseUser.getUid());
                                    m.put("chatId","");
                                    firebaseFirestore.collection("users").document(userId).collection("friends").document(firebaseUser.getUid()).set(m).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            friendStatus= "friend";
                                            btnSendFriendRequest.setText("Remove Friend");
                                            firebaseFirestore.collection("users").document(userId).collection("requestStatus").document(firebaseUser.getUid()).delete();
                                        }
                                    });
                                }
                            });
                        }
                    });

                }
                else{
                    Map<String,Object> m = new HashMap<>();
                    m.put("status","request_send");
                    firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("requestStatus").document(userId).set(m).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            friendStatus= "request_send";
                            btnSendFriendRequest.setText("Cancel Friend Request");
                            Map<String,Object> m = new HashMap<>();
                            m.put("status","waiting_approval");
                            firebaseFirestore.collection("users").document(userId).collection("requestStatus").document(firebaseUser.getUid()).set(m);
                        }
                    });
                }
            }
        });
    }


}
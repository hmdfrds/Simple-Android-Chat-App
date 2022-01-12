package com.example.simplechatapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.simplechatapplication.Adapter.MessageAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    RecyclerView rvMessage;
    Button btnSend;
    EditText edtMessage;
    TextView txtUsername;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    ArrayList<Map<String,Object>> messages = new ArrayList<>();
    String documentId = "";
    String receiverId = "";
    MessageAdapter messageAdapter;
    boolean firstCall = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        rvMessage = findViewById(R.id.rvMessage);
        btnSend = findViewById(R.id.btnSend);
        edtMessage = findViewById(R.id.edtMessage);
        txtUsername = findViewById(R.id.txtUsername);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                receiverId= null;
            } else {
                receiverId= extras.getString("receiverId");
                documentId = extras.getString("documentId");
            }
        } else {
            receiverId= (String) savedInstanceState.getSerializable("receiverId");
            documentId= (String) savedInstanceState.getSerializable("documentId");
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        rvMessage = findViewById(R.id.rvMessage);
        if(!documentId.isEmpty()) {
            firebaseFirestore.collection("chats").document(documentId).collection("message").orderBy("createdAt", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
                if(firstCall){
                    for(DocumentSnapshot dc :value){
                        if(messages.contains(dc.getData())){
                        }else{
                                messages.add(dc.getData());


                        }
                    }
                    firstCall = false;
                }else {
                    for(DocumentSnapshot dc :value){
                        if(messages.contains(dc.getData())){
                        }else{
                            messages.add(0,dc.getData());
                        }

                    }
                }
                messageAdapter = new MessageAdapter(ChatActivity.this, messages, item -> {

                });
                rvMessage.setLayoutManager(new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, true));
                rvMessage.setAdapter(messageAdapter);
rvMessage.scrollToPosition(0);


            });
        }
        btnSend.setOnClickListener(v -> sendMessage());


    }

    public void sendMessage(){
        if(edtMessage.getText().toString().trim().isEmpty()){

        }{
            Map<String, Object> message = new HashMap<>();
            Timestamp timestamp =  Timestamp.now();
            message.put("createdAt",timestamp );
            message.put("message", edtMessage.getText().toString());
            message.put("senderId",firebaseUser.getUid());
            edtMessage.setText("");
            if(documentId.isEmpty()){
                firebaseFirestore.collection("chats").document().collection("message").add(message).addOnCompleteListener(task -> {
                    task.addOnSuccessListener(documentReference -> {
                        documentId = documentReference.getParent().getParent().getId();


                        firebaseFirestore.collection("users").document(firebaseUser.getUid()).update("chats", FieldValue.arrayUnion(documentReference.getParent().getParent().getId())).addOnCompleteListener(task1 -> {
                            task1.addOnSuccessListener(unused -> {
                                firebaseFirestore.collection("chats").document(documentId).collection("message").orderBy("createdAt",Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
                                    if(firstCall){
                                        for(DocumentSnapshot dc :value){
                                            if(messages.contains(dc.getData())){
                                            }else{
                                                messages.add(dc.getData());


                                            }
                                        }
                                        firstCall = false;
                                    }else {
                                        for(DocumentSnapshot dc :value){
                                            if(messages.contains(dc.getData())){
                                            }else{
                                                messages.add(0,dc.getData());
                                            }

                                        }
                                    }
                                    messageAdapter = new MessageAdapter(ChatActivity.this, messages, item -> {

                                    });
                                    rvMessage.setLayoutManager(new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, true));
                                    rvMessage.setAdapter(messageAdapter);
                                    rvMessage.scrollToPosition(0);

                                });
                            });
                            task1.addOnFailureListener(e -> {

                            });
                        });
                        firebaseFirestore.collection("users").document(receiverId).update("chats", FieldValue.arrayUnion(documentReference.getParent().getParent().getId())).addOnCompleteListener(task1 -> {
                            task1.addOnSuccessListener(unused -> {
                                firebaseFirestore.collection("users").document(receiverId).collection("friends").document(firebaseUser.getUid()).update("chatId",documentId);
                                firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("friends").document(receiverId).update("chatId",documentId);

                            });
                            task1.addOnFailureListener(e -> {

                            });
                        });

                    });
                    task.addOnFailureListener(e -> {

                    });
                });
            }else {
                firebaseFirestore.collection("chats").document(documentId).collection("message").add(message).addOnCompleteListener(task -> {


                });
            }

        }

    }
}
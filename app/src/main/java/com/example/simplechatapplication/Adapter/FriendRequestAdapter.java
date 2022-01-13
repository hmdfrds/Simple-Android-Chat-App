package com.example.simplechatapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.simplechatapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot item);
    }
    private List<DocumentSnapshot> mData;
    private LayoutInflater mInflater;
    private final OnItemClickListener listener;

    // data is passed into the constructor
    public FriendRequestAdapter(Context context, List<DocumentSnapshot> data, OnItemClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.listener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.friend_request_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mData.get(position), listener);
        String email = mData.get(position).getId();

        holder.txtEmail.setText(email);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        holder.btnDecline.setOnClickListener(v -> firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("requestStatus").document(email).delete().addOnSuccessListener(unused -> {
            firebaseFirestore.collection("users").document(email).collection("requestStatus").document(firebaseUser.getUid()).delete();

        }));
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> m = new HashMap<>();
                m.put("id", email);
                m.put("chatId","");
                firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("requestStatus").document(email).delete().addOnSuccessListener(unused -> firebaseFirestore.collection("users").document(firebaseUser.getUid()).collection("friends").document(email).set(m).addOnSuccessListener(unused12 -> {
                    Map<String, Object> m12 = new HashMap<>();
                    m12.put("id", firebaseUser.getUid());
                    m12.put("chatId","");
                    firebaseFirestore.collection("users").document(email).collection("friends").document(firebaseUser.getUid()).set(m12).addOnSuccessListener(unused1 -> {

                        firebaseFirestore.collection("users").document(email).collection("requestStatus").document(firebaseUser.getUid()).delete();
                    });
                }));
            }
        });

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView txtEmail;
        Button btnAccept, btnDecline;

        ViewHolder(View itemView) {
            super(itemView);
            txtEmail = itemView.findViewById(R.id.txtEmailFriendRequest);
            btnAccept = itemView.findViewById(R.id.btnAcceptFriendRequest);
            btnDecline = itemView.findViewById(R.id.btnDeclineFriendRequest);

        }
        public void bind(final DocumentSnapshot item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }



}
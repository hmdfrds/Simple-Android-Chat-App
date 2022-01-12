package com.example.simplechatapplication.Adapter;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.simplechatapplication.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(String item);
    }
    private List<Map<String, Object>> mData;
    private LayoutInflater mInflater;
    private final OnItemClickListener listener;
    FirebaseUser firebaseUser;
    View view;

    // data is passed into the constructor
    public MessageAdapter(Context context, List<Map<String, Object>> data, OnItemClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.listener = listener;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    // inflates the row layout from xml when needed
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 1){
            view = mInflater.inflate(R.layout.my_message_row, parent, false);
            return new MyViewHolder(view);
        }
        else{
            view = mInflater.inflate(R.layout.your_message_row, parent, false);
            return new YourViewHolder(view);
        }


    }

    @Override
    public int getItemViewType(int position) {
        Map<String, Object> email = mData.get(position);
        if(email.get("senderId").toString().trim().equals(firebaseUser.getUid().trim())) {
            return 1;
        }
        return 2;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Map<String, Object> email = mData.get(position);
        Timestamp timestamp = (Timestamp) email.get("createdAt");
        Date date = timestamp.toDate();
        String newDate = new SimpleDateFormat("HH:mm").format(date);


        if(getItemViewType(position) == 1){
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            myViewHolder.txtMyMessage.setText(email.get("message").toString());
            myViewHolder.txtMyTime.setText(newDate);
        }
        else {
            YourViewHolder yourViewHolder = (YourViewHolder) holder;
            yourViewHolder.txtYourMessage.setText(email.get("message").toString());
            yourViewHolder.txtYourTime.setText(newDate);
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView txtMyMessage,txtMyTime;
        MyViewHolder(View itemView) {
            super(itemView);
            txtMyMessage = itemView.findViewById(R.id.txtMyMessageRow);
            txtMyTime = itemView.findViewById(R.id.txtMyTime);
        }
        public void bind(final String item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    // stores and recycles views as they are scrolled off screen
    public class YourViewHolder extends RecyclerView.ViewHolder  {
        TextView txtYourMessage,txtYourTime;
        YourViewHolder(View itemView) {
            super(itemView);
            txtYourTime = itemView.findViewById(R.id.txtYourTime);
            txtYourMessage = itemView.findViewById(R.id.txtYourMessageRow);
        }
        public void bind(final String item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }



}
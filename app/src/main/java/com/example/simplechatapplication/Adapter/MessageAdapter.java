package com.example.simplechatapplication.Adapter;



import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplechatapplication.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         view = mInflater.inflate(R.layout.message_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        Map<String, Object> email = mData.get(position);
        Timestamp timestamp = (Timestamp) email.get("createdAt");
        Date date = timestamp.toDate();
        String newDate = new SimpleDateFormat("HH:mm").format(date);
        holder.txtMessage.setText(email.get("message").toString());
        holder.txtTime.setText(newDate);


        if(email.get("senderId").toString().trim().equals(firebaseUser.getUid().toString().trim())){

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    750, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END;
            holder.lytSubMainMessage.setBackgroundColor(Color.parseColor("#0000FF"));
            holder.crdText.setLayoutParams(params);
            holder.txtMessage.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            holder.txtTime.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        }else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    750, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.START;
            holder.lytSubMainMessage.setBackgroundColor(Color.parseColor("#FF0000"));
            holder.crdText.setLayoutParams(params);
            holder.txtMessage.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            holder.txtTime.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView txtMessage,txtTime;
        CardView crdText;
        LinearLayout lytMainMessageRow,lytSubMainMessage;

        ViewHolder(View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessageRow);
            txtTime = itemView.findViewById(R.id.txtTime);
            crdText = itemView.findViewById(R.id.crdText);
            lytMainMessageRow = itemView.findViewById(R.id.lytMainMessageRow);
            lytSubMainMessage = itemView.findViewById(R.id.lytSubMainMessage);

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
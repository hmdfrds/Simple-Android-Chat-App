package com.example.simplechatapplication.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.simplechatapplication.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(String item);
    }
    private List<String> mData;
    private LayoutInflater mInflater;
    private final OnItemClickListener listener;

    // data is passed into the constructor
    public ChatAdapter(Context context, List<String> data, OnItemClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.listener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.friend_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mData.get(position), listener);
        String email = mData.get(position);

        holder.txtEmail.setText(email);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView txtEmail;



        ViewHolder(View itemView) {
            super(itemView);
            txtEmail = itemView.findViewById(R.id.txtEmailRow);




        }
        public void bind(final String item, final OnItemClickListener listener) {

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

    }



}
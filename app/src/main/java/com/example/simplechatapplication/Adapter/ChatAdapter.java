package com.example.simplechatapplication.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.simplechatapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Map<String,Object> item);
    }
    private ArrayList<Map<String,Object>> mData;
    private LayoutInflater mInflater;
    private final OnItemClickListener listener;
    private Context context;

    // data is passed into the constructor
    public ChatAdapter(Context context, ArrayList<Map<String,Object>> data, OnItemClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context=context;
        this.listener = listener;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.chat_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mData.get(position), listener);
        String name = mData.get(position).get("name").toString();
        String profileImg = mData.get(position).get("imageUrl").toString();
        Picasso.with(context).load(profileImg).into(holder.profileImgChat);
        holder.txtChatName.setText(name);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView txtChatName;
        ImageView profileImgChat;


        ViewHolder(View itemView) {
            super(itemView);
            txtChatName = itemView.findViewById(R.id.txtChatName);
            profileImgChat=itemView.findViewById(R.id.profileImgChat);


        }
        public void bind(final Map<String,Object> item, final OnItemClickListener listener) {

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }

    }



}
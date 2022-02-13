package com.example.simplechatapplication.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.simplechatapplication.R;

import java.util.List;
import java.util.Map;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Map<String,Object> item);
    }
    private List<Map<String,Object>> mData;
    private LayoutInflater mInflater;
    private final OnItemClickListener listener;

    // data is passed into the constructor
    public FriendAdapter(Context context, List<Map<String,Object>> data, OnItemClickListener listener) {
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
        String id = mData.get(position).get("id").toString();
        String email = mData.get(position).get("email").toString();
        String name = mData.get(position).get("name").toString();
        String profileImg = mData.get(position).get("imageUrl").toString();

        holder.txtID.setText(id);
        holder.txtName.setText(name);
        holder.txtEmail.setText(email);
        holder.profileImg.setImageURI(Uri.parse(profileImg));

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView txtName,txtEmail,txtID;
        ImageView profileImg;

        ViewHolder(View itemView) {
            super(itemView);
            txtID = itemView.findViewById(R.id.txtID);
            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            profileImg=itemView.findViewById(R.id.profileImg);



        }
        public void bind(final Map<String,Object> item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }



}
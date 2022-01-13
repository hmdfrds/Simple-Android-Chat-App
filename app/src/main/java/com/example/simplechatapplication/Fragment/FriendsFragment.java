package com.example.simplechatapplication.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.simplechatapplication.Adapter.FragmentAdapter;
import com.example.simplechatapplication.Adapter.FriendAdapter;
import com.example.simplechatapplication.Adapter.FriendFragmentAdapter;
import com.example.simplechatapplication.ChatActivity;
import com.example.simplechatapplication.R;
import com.example.simplechatapplication.SearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
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

public class FriendsFragment extends Fragment {


    TabLayout tabLayoutFriend;
    ViewPager2 viewPager2Friend;
    FriendFragmentAdapter friendFragmentAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        tabLayoutFriend = view.findViewById(R.id.tabLayoutFriends);
        viewPager2Friend = view.findViewById(R.id.viewPagerFriends);
        FragmentManager fragmentManager = getChildFragmentManager();


        friendFragmentAdapter = new FriendFragmentAdapter(fragmentManager, getLifecycle());
        viewPager2Friend.setAdapter(friendFragmentAdapter);

        tabLayoutFriend.addTab(tabLayoutFriend.newTab().setText("Friend List"));
        tabLayoutFriend.addTab(tabLayoutFriend.newTab().setText("Waiting For Approval"));

        tabLayoutFriend.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2Friend.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2Friend.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayoutFriend.selectTab(tabLayoutFriend.getTabAt(position));
            }
        });

        return view;
    }


}
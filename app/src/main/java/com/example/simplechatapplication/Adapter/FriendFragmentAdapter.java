package com.example.simplechatapplication.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import com.example.simplechatapplication.Fragment.FriendListFragment;
import com.example.simplechatapplication.Fragment.FriendRequestFragment;


public class FriendFragmentAdapter extends FragmentStateAdapter {
    public FriendFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public FriendFragmentAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public FriendFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new FriendRequestFragment();

        }
        return new FriendListFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

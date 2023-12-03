package com.example.myapplication.user_profile_page;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.user_profile_page.user_profile_fragments.AboutFragment;
import com.example.myapplication.user_profile_page.user_profile_fragments.UserProfileListingsFragment;
import com.example.myapplication.user_profile_page.user_profile_fragments.RatingsFragment;

public class ProfileViewPageAdapter extends FragmentStateAdapter {

    private final String profileUserId; // Add a field to hold the user ID

    public ProfileViewPageAdapter(@NonNull FragmentActivity fragmentActivity, String userId) {
        super(fragmentActivity);
        this.profileUserId = userId; // Initialize the user ID in the constructor
    }

    // takes position and changes the fragment for the tab layout
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;

        switch(position) {
            case 1:
                fragment = new RatingsFragment();
                break;
            case 2:
                fragment = new AboutFragment();
                break;

            default:
                fragment = new UserProfileListingsFragment();
                break;
        }

        // Set arguments for fragments that require the user ID
        if (fragment instanceof RatingsFragment || fragment instanceof UserProfileListingsFragment) {
            Bundle args = new Bundle();
            args.putString("uid", profileUserId); // Pass the user ID
            fragment.setArguments(args);
        }

        return fragment;
    }

    // total number of tabs
    @Override
    public int getItemCount() {
        return 3;
    }
}

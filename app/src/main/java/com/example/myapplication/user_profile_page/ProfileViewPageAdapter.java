package com.example.myapplication.user_profile_page;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.user_profile_page.user_profile_fragments.AboutFragment;
import com.example.myapplication.user_profile_page.user_profile_fragments.ListingsFragment;
import com.example.myapplication.user_profile_page.user_profile_fragments.RatingsFragment;

public class ProfileViewPageAdapter extends FragmentStateAdapter {
    public ProfileViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    // takes position and changes the fragment for the tab layout
    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch(position) {
            case 0:
                return new ListingsFragment();

            case 1:
                return new RatingsFragment();

            case 2:
                return new AboutFragment();

            default:
                return new ListingsFragment();
        }
    }

    // total number of tabs
    @Override
    public int getItemCount() {
        return 3;
    }
}

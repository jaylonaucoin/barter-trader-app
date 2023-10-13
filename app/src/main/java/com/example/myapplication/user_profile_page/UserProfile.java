package com.example.myapplication.user_profile_page;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.example.myapplication.user_profile_page.user_profile_fragments.RatingsFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class UserProfile extends AppCompatActivity {


    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ProfileViewPageAdapter ProfileViewPageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        RatingViewModel viewModel = new ViewModelProvider(this).get(RatingViewModel.class);
        RatingBar ratingBar = findViewById(R.id.ratingBar);

        // Observe the LiveData to update the RatingBar
        viewModel.getAverageRating().observe(this, ratingBar::setRating);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        ProfileViewPageAdapter = new ProfileViewPageAdapter(this);
        viewPager2.setAdapter(ProfileViewPageAdapter);

        // sets viewpage to their respective fragment using tab layout
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // registers tab layout changes when swiping
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Objects.requireNonNull(tabLayout.getTabAt(position)).select();
            }
        });
    }
}

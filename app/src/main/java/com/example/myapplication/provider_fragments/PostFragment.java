package com.example.myapplication.provider_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.PostGoods;
import com.example.myapplication.R;
public class PostFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.provider_post, container, false);

        setupButtonListeners(view);

        return view;
    }

    private void setupButtonListeners(View view) {
        // Associate each button with its category and set up the click listeners
        setupButtonListener(view, R.id.category1, "1");
        setupButtonListener(view, R.id.category2, "2");
        setupButtonListener(view, R.id.category3, "3");
        setupButtonListener(view, R.id.category4, "4");
        setupButtonListener(view, R.id.category5, "5");
        setupButtonListener(view, R.id.category6, "6");
    }

    private void setupButtonListener(View view, int buttonId, String category) {
        Button button = view.findViewById(buttonId);
        button.setOnClickListener(v -> navigateToPostActivity(category));
    }

    private void navigateToPostActivity(String category) {
        Intent intent = new Intent(getActivity(), PostGoods.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}

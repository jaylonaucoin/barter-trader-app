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

        // Retrieve the category array from strings.xml
        String[] categories = getResources().getStringArray(R.array.category);

        // Associate each button with its category and set up the click listeners
        setupButtonListeners(view, categories);

        return view;
    }

    private void setupButtonListeners(View view, String[] categories) {
        int[] buttonIds = {R.id.category1, R.id.category2, R.id.category3, R.id.category4, R.id.category5, R.id.category6, R.id.category7, R.id.category8};

        for (int i = 0; i < buttonIds.length; i++) {
            if (i < categories.length) {
                setupButtonListener(view, buttonIds[i], categories[i]);
            }
        }
    }

    private void setupButtonListener(View view, int buttonId, String category) {
        Button button = view.findViewById(buttonId);
        button.setText(category); // Set the text from the category array
        button.setOnClickListener(v -> navigateToPostActivity(category));
    }

    private void navigateToPostActivity(String category) {
        Intent intent = new Intent(getActivity(), PostGoods.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}

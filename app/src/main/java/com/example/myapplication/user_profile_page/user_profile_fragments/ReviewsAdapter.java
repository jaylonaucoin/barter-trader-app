package com.example.myapplication.user_profile_page.user_profile_fragments;

import com.example.myapplication.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private final List<Review> reviewsList;

    public ReviewsAdapter(List<Review> reviewsList) {
        this.reviewsList = reviewsList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewsList.get(position);
        holder.usernameTextView.setText(review.getUsername());
        holder.ratingBar.setRating(review.getRating());
        holder.feedbackTextView.setText(review.getFeedback());
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, feedbackTextView;
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            feedbackTextView = itemView.findViewById(R.id.feedbackTextView);
        }
    }
}

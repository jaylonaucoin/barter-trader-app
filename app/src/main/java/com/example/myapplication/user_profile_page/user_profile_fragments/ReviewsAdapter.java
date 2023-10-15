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

/**
 * Adapter for displaying a list of reviews in a RecyclerView.
 * Each review item consists of a username, rating, and feedback.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    // List containing the reviews to be displayed in the RecyclerView
    private final List<Review> reviewsList;

    /**
     * Constructor for ReviewsAdapter.
     *
     * @param reviewsList List of reviews to be displayed.
     */
    public ReviewsAdapter(List<Review> reviewsList) {
        this.reviewsList = reviewsList;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     *
     * @param parent   The ViewGroup into which the new view will be added.
     * @param viewType The view type of the new View.
     * @return A new ReviewViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the review item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    /**
     * Binds the data from the given review to the ViewHolder's views.
     *
     * @param holder   The ViewHolder to be updated with data.
     * @param position The position of the review in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewsList.get(position);
        holder.usernameTextView.setText(review.getUsername());
        holder.ratingBar.setRating(review.getRating());
        holder.feedbackTextView.setText(review.getFeedback());
    }

    /**
     * Returns the total number of reviews in the list.
     *
     * @return The total number of reviews.
     */
    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    /**
     * ViewHolder class that holds references to the UI components for each review item.
     */
    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, feedbackTextView;
        RatingBar ratingBar;

        /**
         * Constructor for ReviewViewHolder.
         *
         * @param itemView The review item view.
         */
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize UI components from the review item view
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            feedbackTextView = itemView.findViewById(R.id.feedbackTextView);
        }
    }
}

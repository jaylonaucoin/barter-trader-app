package com.example.myapplication.provider_fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatMessage> chatMessages;
    private OnChatListener onChatListener;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public void setChatMessages(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public interface OnChatListener {
        void onChatSelected(String partnerId);
    }

    public void setOnChatListener(OnChatListener onChatListener) {
        this.onChatListener = onChatListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        holder.textViewUsername.setText(chatMessage.getFullName());
        holder.textViewRecentMessage.setText(chatMessage.getText());

        holder.itemView.setOnClickListener(v -> {
            if (onChatListener != null) {
                onChatListener.onChatSelected(chatMessage.getReceiverId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        TextView textViewRecentMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            textViewRecentMessage = itemView.findViewById(R.id.textViewRecentMessage);
        }
    }
}

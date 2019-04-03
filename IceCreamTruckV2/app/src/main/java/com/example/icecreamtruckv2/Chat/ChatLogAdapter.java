package com.example.icecreamtruckv2.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.icecreamtruckv2.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ChatLogAdapter extends RecyclerView.Adapter<ChatLogAdapter.ChatViewHolder> {

    static final class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        TextView message;

        ChatViewHolder(View view) {
            super(view);

            icon = view.findViewById(R.id.item_icon);
            name = view.findViewById(R.id.item_username);
            message = view.findViewById(R.id.item_message);
        }
    }

    private List<ChatMessage> mData = new ArrayList<>();

    public void clearData() {
        mData.clear();
    }
    public void addData(ChatMessage data) {
        mData.add(data);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat, parent, false);
        return new ChatViewHolder(root);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatMessage data = mData.get(position);

        holder.message.setText(data.getMessage());
        holder.name.setText(data.getName());
        holder.icon.setImageResource(data.getIcon());
    }
}


package com.example.icecreamtruckv2.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icecreamtruckv2.R;
import com.example.icecreamtruckv2.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class ChatLogAdapter extends RecyclerView.Adapter<ChatLogAdapter.ChatViewHolder> {

    private List<ChatMessage> mData = new ArrayList<>();

    public ChatLogAdapter(List<ChatMessage> msg) {
        mData = msg;
    }
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
    public void onBindViewHolder(final ChatViewHolder holder, int position) {
        ChatMessage data = mData.get(position);

        holder.name.setText(data.getName());
        holder.icon.setImageResource(data.getIcon());

        if (data.getType().equals("GIF")) {
            holder.gif.setVisibility(View.VISIBLE);
            holder.message.setVisibility(View.GONE);
            holder.gif.setBackgroundResource(R.drawable.loading);
            data.setDrawable(holder);

        } else {
            if (!data.getMessage().equals("")) {
                holder.message.setVisibility(View.VISIBLE);
                holder.gif.setVisibility(View.GONE);
                holder.message.setText(data.getMessage());
            }
        }
    }

    public void removeData(int id) {
        mData.remove(id);
    }

    static final class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        TextView message;
        GifImageView gif;

        ChatViewHolder(View view) {
            super(view);

            icon = view.findViewById(R.id.item_icon);
            name = view.findViewById(R.id.item_username);
            message = view.findViewById(R.id.item_message);
            gif = view.findViewById(R.id.item_gif);
        }
    }
}


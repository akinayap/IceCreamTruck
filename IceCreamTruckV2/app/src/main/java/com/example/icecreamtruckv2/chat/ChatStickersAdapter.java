package com.example.icecreamtruckv2.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class ChatStickersAdapter extends RecyclerView.Adapter<ChatStickersAdapter.ChatStickersViewHolder> {

    private OnItemClickListener listener;
    public List<ChatSticker> mData = new ArrayList<>();

    public ChatStickersAdapter(List<ChatSticker> msg, OnItemClickListener listener) {
        mData = msg;
        this.listener = listener;
    }

    public void clearData() {
        mData.clear();
    }

    public void addData(ChatSticker data) {
        mData.add(data);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ChatStickersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticker, parent, false);
        return new ChatStickersViewHolder(root);
    }

    @Override
    public void onBindViewHolder(final ChatStickersViewHolder holder, int position) {
        final ChatSticker data = mData.get(position);
        data.setContext(holder.itemView.getContext());
        holder.icon.setBackgroundResource(R.drawable.loading);
        data.setDrawable(holder.icon);

        holder.bind(data, listener);
/*        holder.icon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e("HELLO " + data.getName(), "YESSSSSSSS");
                return false;
            }
        });*/

    }


    public interface OnItemClickListener {
        void onItemClick(ChatSticker item);
    }

    static final class ChatStickersViewHolder extends RecyclerView.ViewHolder {
        GifImageView icon;

        ChatStickersViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.sticker_icon);
        }

        public void bind(final ChatSticker item, final OnItemClickListener listener) {
/*            name.setText(item.name);
            Picasso.with(itemView.getContext()).load(item.imageUrl).into(image);*/
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}

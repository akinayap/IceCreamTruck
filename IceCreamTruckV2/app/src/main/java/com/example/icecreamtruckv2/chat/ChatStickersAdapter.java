package com.example.icecreamtruckv2.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.icecreamtruckv2.R;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class ChatStickersAdapter extends RecyclerView.Adapter<ChatStickersAdapter.ChatStickersViewHolder> {

    private OnItemClickListener listener;
    public List<ChatSticker> mData;

    public ChatStickersAdapter(List<ChatSticker> msg, OnItemClickListener listener) {
        mData = msg;
        this.listener = listener;
    }

    public void clearData() {
        mData.clear();
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
        holder.icon.setImageResource(R.drawable.loading);
        data.setDrawable(holder.icon);
        holder.bind(data, listener);
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
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}

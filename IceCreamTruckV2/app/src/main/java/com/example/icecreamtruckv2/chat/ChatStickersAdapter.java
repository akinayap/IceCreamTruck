package com.example.icecreamtruckv2.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.icecreamtruckv2.R;

import java.util.Collections;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class ChatStickersAdapter extends RecyclerView.Adapter<ChatStickersAdapter.ChatStickersViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {
    private OnItemClickListener clicklistener;
    private List<ChatSticker> mData;

    ChatStickersAdapter(List<ChatSticker> stickerList, OnItemClickListener listener) {
        mData = stickerList;
        clicklistener = listener;
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
        holder.bind(data, clicklistener);
    }

    public interface OnItemClickListener {
        void onItemClick(ChatSticker item);
    }

    final class ChatStickersViewHolder extends RecyclerView.ViewHolder {
        GifImageView icon;

        ChatStickersViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.sticker_icon);
        }

        void bind(final ChatSticker item, final OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }

    @Override
    public void onRowMoved(RecyclerView rv, int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mData, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mData, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(ChatStickersViewHolder myViewHolder) {
    }

    @Override
    public void onRowClear(ChatStickersViewHolder myViewHolder, RecyclerView rv) {
    }

}

package anw.ict.chat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import anw.ict.R;
import anw.ict.chat.callback.ItemMoveCallback;
import anw.ict.chat.objects.ChatSticker;
import pl.droidsonroids.gif.GifImageView;

public class ChatStickersAdapter extends RecyclerView.Adapter<ChatStickersAdapter.ChatStickersViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {
    private OnItemClickListener clickListener;
    private List<ChatSticker> mData;

    public ChatStickersAdapter(List<ChatSticker> stickerList, OnItemClickListener listener) {
        mData = stickerList;
        clickListener = listener;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    @NonNull
    @Override
    public ChatStickersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticker, parent, false);
        return new ChatStickersViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatStickersViewHolder holder, int position) {
        final ChatSticker data = mData.get(position);
        data.setContext(holder.itemView.getContext());
        holder.icon.setImageDrawable(data.drawable());
        holder.bind(data, clickListener);
    }

    public interface OnItemClickListener {
        void onItemClick(ChatSticker item);
    }

    public final class ChatStickersViewHolder extends RecyclerView.ViewHolder {
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

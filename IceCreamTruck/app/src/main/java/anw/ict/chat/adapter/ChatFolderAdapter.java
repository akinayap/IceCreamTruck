package anw.ict.chat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import anw.ict.R;
import anw.ict.chat.callback.FolderMoveCallback;
import anw.ict.chat.callback.ItemMoveCallback;
import anw.ict.chat.objects.ChatFolder;
import anw.ict.chat.objects.ChatSticker;

import static anw.ict.chat.fragments.FolderFrag.selectedFolder;
import static anw.ict.chat.fragments.FolderFrag.stickerAdapter;
import static anw.ict.chat.fragments.FolderFrag.stickerList;

public class ChatFolderAdapter extends RecyclerView.Adapter<ChatFolderAdapter.ChatFolderViewHolder> implements FolderMoveCallback.FolderTouchHelperContract {
    private List<ChatFolder> mData;
    private Context context;
    public ChatFolderAdapter(List<ChatFolder> folderList) {
        mData = folderList;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @NonNull
    @Override
    public ChatFolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
        return new ChatFolderViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatFolderViewHolder holder, int position) {

        holder.tv.setText(mData.get(position).getFolderName());

        holder.adapter = new ChatStickersAdapter(mData.get(position).getStickers(), v->{
            // Remove Sticker Here
            List<ChatSticker> stickers = mData.get(position).getStickers();
            ChatSticker toRemove = stickers.stream().filter(s -> s.getName().equals(v.getName())).findAny().orElse(null);
            holder.adapter.notifyItemRemoved(stickers.indexOf(toRemove));
            mData.get(position).getStickers().remove(toRemove);

            stickerList.add(v);
            stickerAdapter.notifyItemInserted(stickerList.size() - 1);
        });

        // Grid for folders
        GridLayoutManager glmV = new GridLayoutManager(context, 2, RecyclerView.HORIZONTAL, false);
        /* UI Components */
        holder.rv.setLayoutManager(glmV);
        holder.rv.setAdapter(holder.adapter);
        ItemTouchHelper.Callback callback = new ItemMoveCallback(holder.adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(holder.rv);

        holder.cl.setOnClickListener(v -> {
            String folderClicked = ((TextView)v.findViewById(R.id.folder_name)).getText().toString();
            ChatFolder prevFolder = (mData.stream().filter(f->f.getFolderName().equals(selectedFolder)).findAny().orElse(null));
            if(selectedFolder.equals(folderClicked)){
                selectedFolder = "";
            } else {
                ChatFolder newFolder = (mData.stream().filter(f->f.getFolderName().equals(folderClicked)).findAny().orElse(null));
                selectedFolder = folderClicked;
                notifyItemChanged(mData.indexOf(newFolder));
            }
            notifyItemChanged(mData.indexOf(prevFolder));
        });

        if(selectedFolder.equals(mData.get(position).getFolderName())) {
            Drawable buttonDrawable = holder.cl.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            //the color is a direct color int and not a color resource
            DrawableCompat.setTint(buttonDrawable, Color.WHITE);
            holder.cl.setBackground(buttonDrawable);

            holder.tv.setTextColor(Color.BLACK);
        }
        else {
            Drawable buttonDrawable = holder.cl.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            //the color is a direct color int and not a color resource
            DrawableCompat.setTintList(buttonDrawable, null);
            holder.tv.setTextColor(Color.WHITE);
        }
    }

    public class ChatFolderViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout cl;
        TextView tv;
        RecyclerView rv;
        ChatStickersAdapter adapter;
        ChatFolderViewHolder(@NonNull View itemView) {
            super(itemView);
            cl = itemView.findViewById(R.id.folder_bg);
            tv = itemView.findViewById(R.id.folder_name);
            rv = itemView.findViewById(R.id.rv_folder_view);
        }

        public ChatStickersAdapter getAdapter() {
            return adapter;
        }
    }

    @Override
    public void onRowMoved(RecyclerView recyclerView, int fromPosition, int toPosition) {
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
    public void onRowSelected(ChatFolderViewHolder myViewHolder) {

    }

    @Override
    public void onRowClear(ChatFolderViewHolder myViewHolder, RecyclerView recyclerView) {

    }
}

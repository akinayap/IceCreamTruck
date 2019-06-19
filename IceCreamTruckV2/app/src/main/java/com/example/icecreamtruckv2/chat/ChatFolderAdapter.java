package com.example.icecreamtruckv2.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;
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

import com.example.icecreamtruckv2.R;

import java.util.Collections;
import java.util.List;

import static com.example.icecreamtruckv2.chat.FolderFragment.folderList;
import static com.example.icecreamtruckv2.chat.FolderFragment.selectedFolder;
import static com.example.icecreamtruckv2.chat.FolderFragment.stickerAdapter;
import static com.example.icecreamtruckv2.chat.FolderFragment.stickerList;

class ChatFolderAdapter extends RecyclerView.Adapter<ChatFolderAdapter.ChatFolderViewHolder> implements FolderMoveCallback.FolderTouchHelperContract {
    private List<String> mData;
    private Context context;
    SharedPreferences sharedPreferences;
    String userid;
    ChatFolderAdapter(List<String> folderList) {
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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        userid = sharedPreferences.getString("userid", "");
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
        return new ChatFolderViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatFolderViewHolder holder, int position) {

        holder.tv.setText(mData.get(position));

        //List<ChatSticker> stickerList = new ArrayList<>();
        holder.adapter = new ChatStickersAdapter(folderList.get(position), v->{ // Remove Sticker Here
            folderList.get(position).removeIf(s->s.getName().equals(v.getName()));
            stickerList.add(v);
            holder.adapter.notifyItemRemoved(position);
            holder.rv.smoothScrollToPosition(getItemCount()-1);
            stickerAdapter.notifyItemInserted(stickerList.size() - 1);
        });

        // Grid for folders
        GridLayoutManager glmV = new GridLayoutManager(context, 2, RecyclerView.HORIZONTAL, false);
        /** UI Components **/
        holder.rv.setLayoutManager(glmV);
        holder.rv.setAdapter(holder.adapter);
        ItemTouchHelper.Callback callback = new ItemMoveCallback(holder.adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(holder.rv);

        if(selectedFolder.equals(mData.get(position)))
        {
            Drawable buttonDrawable = holder.cl.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            //the color is a direct color int and not a color resource
            DrawableCompat.setTint(buttonDrawable, Color.WHITE);
            holder.cl.setBackground(buttonDrawable);

            holder.tv.setTextColor(Color.BLACK);
        }
        else
        {
            if(selectedFolder.equals(""))
            Log.e("Not Selected folder", "NOT");
            Drawable buttonDrawable = holder.cl.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            //the color is a direct color int and not a color resource
            DrawableCompat.setTintList(buttonDrawable, null);
            holder.tv.setTextColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> {
            if(selectedFolder != "")
                Log.e("Prev folder", selectedFolder);

            String prevFolder = selectedFolder;
            if(selectedFolder.equals(mData.get(position))){
                selectedFolder = "";
            }
            else {
                selectedFolder = mData.get(position);
            }

            notifyDataSetChanged();
        });
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
                Collections.swap(folderList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mData, i, i - 1);
                Collections.swap(folderList, i, i - 1);
            }
        }
        //notifyDataSetChanged();
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(ChatFolderViewHolder myViewHolder) {

    }

    @Override
    public void onRowClear(ChatFolderViewHolder myViewHolder, RecyclerView recyclerView) {

    }
}

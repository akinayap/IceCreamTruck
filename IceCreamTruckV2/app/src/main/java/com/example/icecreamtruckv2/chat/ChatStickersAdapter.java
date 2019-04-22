package com.example.icecreamtruckv2.chat;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.Toast;

import com.example.icecreamtruckv2.R;
import com.example.icecreamtruckv2.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageButton;
import pl.droidsonroids.gif.GifImageView;

public class ChatStickersAdapter extends RecyclerView.Adapter<ChatStickersAdapter.ChatStickersViewHolder> {

    private List<ChatSticker> mData = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(ChatSticker item);
    }

    private final OnItemClickListener listener;

    public ChatStickersAdapter(OnItemClickListener listener) {
        this.listener = listener;
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
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
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
        data.setDrawable();
        holder.icon.setBackgroundResource(R.drawable.loading);

        final int TWO_MEGABYTE = 1024 * 1024  * 2;
        final String filename = "GIF" + data.getName() + ".txt";

        try {
            FileInputStream file = holder.itemView.getContext().openFileInput(filename);

            BufferedInputStream buf = new BufferedInputStream(file);
            byte[] bytes = new byte[TWO_MEGABYTE];
            buf.read(bytes, 0, bytes.length);
            buf.close();

            GifDrawable gifFromBytes = new GifDrawable( bytes );
            holder.icon.setBackground(gifFromBytes);
            Log.e("Success", "Download");
        } catch (Exception e) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference stickerSR = storage.getReference(Constants.STICKERS_DB).child(data.getName());
            stickerSR.getBytes(TWO_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    GifDrawable gifFromBytes = null;
                    try {
                        FileOutputStream outputStream;

                        outputStream = holder.itemView.getContext().openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(bytes);
                        outputStream.close();

                        Log.e("Success", "convert1 " + filename);
                        gifFromBytes = new GifDrawable(bytes);
                        holder.icon.setBackground(gifFromBytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }

        holder.bind(data, listener);
/*        holder.icon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e("HELLO " + data.getName(), "YESSSSSSSS");
                return false;
            }
        });*/

    }
}

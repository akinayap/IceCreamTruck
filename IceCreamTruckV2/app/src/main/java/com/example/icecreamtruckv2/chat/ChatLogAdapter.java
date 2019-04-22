package com.example.icecreamtruckv2.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import androidx.recyclerview.widget.RecyclerView;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class ChatLogAdapter extends RecyclerView.Adapter<ChatLogAdapter.ChatViewHolder> {

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
    public void onBindViewHolder(final ChatViewHolder holder, int position) {
        ChatMessage data = mData.get(position);

        holder.name.setText(data.getName());
        holder.icon.setImageResource(data.getIcon());

        if(data.getType().equals("GIF"))
        {
            holder.gif.setVisibility(View.VISIBLE);
            holder.message.setVisibility(View.GONE);
            holder.gif.setBackgroundResource(R.drawable.loading);

            final int TWO_MEGABYTE = 1024 * 1024  * 2;
            final String filename = "GIF" + data.getMessage() + ".txt";

            try {
                FileInputStream file = holder.itemView.getContext().openFileInput(filename);

                BufferedInputStream buf = new BufferedInputStream(file);
                byte[] bytes = new byte[TWO_MEGABYTE];
                buf.read(bytes, 0, bytes.length);
                buf.close();

                GifDrawable gifFromBytes = new GifDrawable( bytes );
                holder.gif.setBackground(gifFromBytes);
                Log.e("Success", "Download");
            } catch (Exception e) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference stickerSR = storage.getReference(Constants.STICKERS_DB).child(data.getMessage());
                stickerSR.getBytes(TWO_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        GifDrawable gifFromBytes = null;
                        try {
                            FileOutputStream outputStream;

                            outputStream = holder.itemView.getContext().openFileOutput(filename, Context.MODE_PRIVATE);
                            outputStream.write(bytes);
                            outputStream.close();

                            Log.e("Success", "convert " + filename);
                            gifFromBytes = new GifDrawable(bytes);
                            holder.gif.setBackground(gifFromBytes);
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
        }
        else
        {
            if(!data.getMessage().equals(""))
            {
                holder.message.setVisibility(View.VISIBLE);
                holder.gif.setVisibility(View.GONE);
                holder.message.setText(data.getMessage());
            }
        }
    }
}


package com.example.icecreamtruckv2.chat;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.icecreamtruckv2.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class ChatMessage {

    private int mID;
    private int mIcon;
    private String name;
    private String message;
    private String type;
    private String timestamp;
    private GifDrawable mDrawable;
    private int TWO_MEGABYTE = 1024 * 1024  * 2;

    private static int counter = 0;
    public ChatMessage() {
        mID = counter++;
        // empty constructor
    }

    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String t) {
        timestamp = t;
    }

    public int getIcon() {
        return mIcon;
    }
    public void setIcon(int icon) {
        mIcon = icon;
    }

    public String getName() {
        return name;
    }
    public void setName(String n) {
        name = n;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String m) {
        message = m;
    }

    public String getType() {
        return type;
    }
    public void setType(String t) {
        type = t;
    }
    public int getID() {
        return(mID);
    }

    public void setDrawable(ChatLogAdapter.ChatViewHolder holder, GifImageView gif) {
        byte[] bytes = new byte[TWO_MEGABYTE];
        final String filename = "GIF" + getMessage() + ".txt";

        try {
            FileInputStream file = holder.itemView.getContext().openFileInput(filename);

            BufferedInputStream buf = new BufferedInputStream(file);
            buf.read(bytes, 0, bytes.length);
            buf.close();

            GifDrawable gifFromBytes = new GifDrawable(bytes);
            gif.setBackground(gifFromBytes);
            Log.e("Success", "DownloadCM");
        } catch (Exception e) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference stickerSR = storage.getReference(Constants.STICKERS_DB).child(getMessage());
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
                        gif.setBackground(gifFromBytes);
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
}

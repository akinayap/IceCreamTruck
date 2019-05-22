package com.example.icecreamtruckv2.chat;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
    private String username, message, type, timestamp;
    private Context context;
    private int IMAGE_GIF = 1024 * 1024 * 12;

    public ChatMessage() {}

    public void setContext(Context c) {
        context = c;
    }

    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String t) {
        timestamp = t;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String n) {
        username = n;
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

    public void setDrawable(final ChatLogAdapter.ChatViewHolder holder, final GifImageView gif) {
        byte[] bytes = new byte[IMAGE_GIF];
        final String filename = "GIF" + getMessage() + ".txt";

        try {
            GifDrawable gifFromBytes;
            FileInputStream file = holder.itemView.getContext().openFileInput(filename);

            BufferedInputStream buf = new BufferedInputStream(file);
            buf.read(bytes, 0, bytes.length);
            buf.close();

            try {
                gifFromBytes = new GifDrawable(bytes);
                gifFromBytes.setLoopCount(0);
                gif.setImageDrawable(gifFromBytes);
            } catch (Exception eer) {
                Drawable image = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                gif.setImageDrawable(image);
            }
            Log.e("Success", "DownloadCM");
        } catch (Exception e) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference stickerSR = storage.getReference(Constants.STICKERS_DB).child(getMessage());
            stickerSR.getBytes(IMAGE_GIF).addOnSuccessListener(bytes1 -> {
                GifDrawable gifFromBytes;
                try {
                    FileOutputStream outputStream;

                    outputStream = holder.itemView.getContext().openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(bytes1);
                    outputStream.close();

                    Log.e("Success", "convert " + filename);
                    try {
                        gifFromBytes = new GifDrawable(bytes);
                        gifFromBytes.setLoopCount(0);
                        gif.setImageDrawable(gifFromBytes);
                    } catch (Exception eer) {
                        Drawable image = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                        gif.setImageDrawable(image);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }).addOnFailureListener(exception -> {
                // Handle any errors
            });
        }
    }
}

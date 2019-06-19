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

import static com.example.icecreamtruckv2.chat.ChatFrag.IMAGE_GIF;

class ChatMessage {
    public String username, message, type, timestamp;
    private Context context;

    ChatMessage() {}

    void setContext(Context c) {
        context = c;
    }

    String getTimestamp() {
        return timestamp;
    }
    void setTimestamp(String t) {
        timestamp = t;
    }

    String getUsername() {
        return username;
    }
    void setUsername(String n) {
        username = n;
    }

    String getMessage() {
        return message;
    }
    void setMessage(String m) {
        message = m;
    }

    String getType() {
        return type;
    }
    void setType(String t) {
        type = t;
    }

    void setDrawable(final ChatLogAdapter.ChatViewHolder holder, final GifImageView gif) {
        byte[] bytes = new byte[IMAGE_GIF];
        final String filename = getMessage();

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
        } catch (Exception e) {
            Log.e("Error", "Failed to set drawable");
        }
    }
}

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

public class ChatSticker{
    private Context context;
    private String name, timestamp, folder;
    private boolean folderActivity = false;

    ChatSticker() {}
    ChatSticker(String f, Context c, String filename) {
        folder = f;
        context = c;
        name = filename;
    }

    public void setDrawable(final GifImageView gif) {
        byte[] bytes = new byte[IMAGE_GIF];
        final String filename = name;

        try {
            GifDrawable gifFromBytes;
            FileInputStream file = context.openFileInput(filename);

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
        }
    }
    void setContext(Context c)
    {
        context = c;
    }

    public String getTimestamp() {
        return timestamp;
    }
    void setTimestamp(String t) {
        timestamp = t;
    }

    String getName() {
        return name;
    }
    void setName(String n) {
        name = n;
    }

    public String getFolder() {
        return folder;
    }
    void setFolder(String name) {
        folder = name;
    }

    void isInFolder(){
        folderActivity = true;
    }
    boolean inFolder(){
        return folderActivity;
    }
}

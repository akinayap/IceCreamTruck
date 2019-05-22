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

public class ChatSticker {
    private int IMAGE_GIF = 1024 * 1024 * 12;
    private Context context;
    private String name, timestamp;

    public ChatSticker() {}

    public void setContext(Context c)
    {
        context = c;
    }

    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String t) {
        timestamp = t;
    }

    public String getName() {
        return name;
    }
    public void setName(String n) {
        name = n;
    }

    public void setDrawable(final GifImageView gif) {
        final String filename = "GIF" + name + ".txt";
        try {
            GifDrawable gifFromBytes;
            FileInputStream file = context.openFileInput(filename);

            BufferedInputStream buf = new BufferedInputStream(file);
            byte[] bytes = new byte[IMAGE_GIF];
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
            Log.e("Success", "DownloadCS");
        } catch (Exception e) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference stickerSR = storage.getReference(Constants.STICKERS_DB).child(name);
            stickerSR.getBytes(IMAGE_GIF).addOnSuccessListener(bytes -> {
                GifDrawable gifFromBytes;
                try {
                    FileOutputStream outputStream;

                    outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(bytes);
                    outputStream.close();

                    Log.e("Success", "convert2 " + filename);
                    try {
                        gifFromBytes = new GifDrawable( bytes );
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

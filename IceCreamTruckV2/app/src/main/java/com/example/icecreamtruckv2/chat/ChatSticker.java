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

public class ChatSticker {
    private int TWO_MEGABYTE = 1024 * 1024  * 2;
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
            FileInputStream file = context.openFileInput(filename);

            BufferedInputStream buf = new BufferedInputStream(file);
            byte[] bytes = new byte[TWO_MEGABYTE];
            buf.read(bytes, 0, bytes.length);
            buf.close();

            GifDrawable gifFromBytes = new GifDrawable( bytes );
            gif.setImageDrawable(gifFromBytes);
            Log.e("Success", "DownloadCS");
        } catch (Exception e) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference stickerSR = storage.getReference(Constants.STICKERS_DB).child(name);
            stickerSR.getBytes(TWO_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    GifDrawable gifFromBytes = null;
                    try {
                        FileOutputStream outputStream;

                        outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(bytes);
                        outputStream.close();

                        Log.e("Success", "convert2 " + filename);
                        gifFromBytes = new GifDrawable( bytes );
                        gif.setImageDrawable(gifFromBytes);
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

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
    private String mName;
    private GifDrawable mDrawable;
    private int TWO_MEGABYTE = 1024 * 1024  * 2;
    private Context mContext;

    public ChatSticker() {
        // empty constructor
    }

    public void setContext(Context context)
    {
        mContext = context;
    }

    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }

    public GifDrawable getDrawable() {
        return mDrawable;
    }
    public void setDrawable() {
        final String filename = "GIF" + mName + ".txt";
        try {
            FileInputStream file = mContext.openFileInput(filename);

            BufferedInputStream buf = new BufferedInputStream(file);
            byte[] bytes = new byte[TWO_MEGABYTE];
            buf.read(bytes, 0, bytes.length);
            buf.close();

            GifDrawable gifFromBytes = new GifDrawable( bytes );
            mDrawable = gifFromBytes;
            Log.e("Success", "DownloadCS");
        } catch (Exception e) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference stickerSR = storage.getReference(Constants.STICKERS_DB).child(mName);
            stickerSR.getBytes(TWO_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    GifDrawable gifFromBytes = null;
                    try {
                        FileOutputStream outputStream;

                        outputStream = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(bytes);
                        outputStream.close();

                        Log.e("Success", "convert2 " + filename);
                        gifFromBytes = new GifDrawable( bytes );
                        mDrawable = gifFromBytes;
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

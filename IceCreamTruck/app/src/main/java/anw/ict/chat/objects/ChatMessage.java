package anw.ict.chat.objects;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import anw.ict.R;
import anw.ict.SplashActivity;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static anw.ict.utils.Constants.IMAGE_GIF;
import static anw.ict.utils.Constants.IMAGE_PHOTO;
import static anw.ict.utils.Constants.PHOTOS;
import static anw.ict.utils.Constants.STICKERS;

public class ChatMessage {
    public String username, message, type, timestamp;
    public ChatMessage reply = null;
    public boolean startBlink = false;
    private Context context;

    public ChatMessage(){}

    public ChatMessage(Context c, String msg, String t, String time, String userRole){
        context = c;
        message = msg;
        type = t;
        timestamp = time;
        username = userRole;
    }

    public void setContext(Context c) {
        context = c;
    }


    public String getTimestamp() {
        return timestamp;
    }
    public String getUsername() {
        return username;
    }
    public String getMessage() {
        return message;
    }
    public String getType() {
        return type;
    }

    public void drawable(GifImageView giv){
        byte[] bytes = new byte[IMAGE_GIF];

        try {
            GifDrawable gifFromBytes;
            FileInputStream file = context.openFileInput(getMessage());

            BufferedInputStream buf = new BufferedInputStream(file);
            buf.read(bytes, 0, bytes.length);
            buf.close();

            try {
                gifFromBytes = new GifDrawable(bytes);
                gifFromBytes.setLoopCount(0);
                giv.setImageDrawable(gifFromBytes);
            } catch (Exception eer) {
                giv.setImageDrawable(new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(bytes, 0, bytes.length)));
            }
        } catch (Exception e) {
            giv.setImageResource(R.drawable.ic_load);
            FirebaseStorage.getInstance().getReference(STICKERS).child(getMessage()).getBytes(IMAGE_GIF).addOnSuccessListener(bytes1 -> {
                try {
                    GifDrawable gifFromBytes;
                    FileOutputStream outputStream = context.openFileOutput(getMessage(), Context.MODE_PRIVATE);
                    outputStream.write(bytes1);
                    outputStream.close();

                    try {
                        gifFromBytes = new GifDrawable(bytes);
                        gifFromBytes.setLoopCount(0);
                        giv.setImageDrawable(gifFromBytes);
                    } catch (Exception eer) {
                        giv.setImageDrawable(new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(bytes1, 0, bytes1.length)));
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }).addOnFailureListener(exception -> Log.e("Error", "Failed to get GIF"));
        }
    }
    public void photo(GifImageView giv){
        byte[] bytes = new byte[IMAGE_PHOTO];

        try {
            FileInputStream file = context.openFileInput(getMessage());

            BufferedInputStream buf = new BufferedInputStream(file);
            buf.read(bytes, 0, bytes.length);
            buf.close();
            giv.setImageDrawable(new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(bytes, 0, bytes.length)));

        } catch (Exception e) {
            giv.setImageResource(R.drawable.ic_load);
            FirebaseStorage.getInstance().getReference(PHOTOS).child(getMessage()).getBytes(IMAGE_PHOTO).addOnSuccessListener(bytes1 -> {
                try {
                    FileOutputStream outputStream = context.openFileOutput(getMessage(), Context.MODE_PRIVATE);
                    outputStream.write(bytes1);
                    outputStream.close();
                    giv.setImageDrawable(new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(bytes1, 0, bytes1.length)));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }).addOnFailureListener(exception -> Log.e("Error", "Failed to get image"));
        }
    }
}

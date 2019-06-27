package anw.ict.chat.objects;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import anw.ict.R;
import pl.droidsonroids.gif.GifDrawable;

import static anw.ict.utils.Constants.IMAGE_GIF;

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

    public Drawable drawable(){
        byte[] bytes = new byte[IMAGE_GIF];
        final String filename = getMessage();

        try {
            GifDrawable gifFromBytes;
            FileInputStream file = context.openFileInput(filename);

            BufferedInputStream buf = new BufferedInputStream(file);
            buf.read(bytes, 0, bytes.length);
            buf.close();

            try {
                gifFromBytes = new GifDrawable(bytes);
                gifFromBytes.setLoopCount(0);
                return gifFromBytes;
            } catch (Exception eer) {
                return new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        } catch (Exception e) {
            return context.getDrawable(R.drawable.ic_load);
        }
    }
}

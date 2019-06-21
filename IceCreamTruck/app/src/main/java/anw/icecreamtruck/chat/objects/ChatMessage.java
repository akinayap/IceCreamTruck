package anw.icecreamtruck.chat.objects;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import pl.droidsonroids.gif.GifDrawable;

import static anw.icecreamtruck.utils.Constants.IMAGE_GIF;

public class ChatMessage {
    public String username, message, type, timestamp;
    private Context context;

    ChatMessage() {}

    public void setContext(Context c) {
        context = c;
    }

    public String getTimestamp() {
        return timestamp;
    }
    void setTimestamp(String t) {
        timestamp = t;
    }

    public String getUsername() {
        return username;
    }
    void setUsername(String n) {
        username = n;
    }

    public String getMessage() {
        return message;
    }
    void setMessage(String m) {
        message = m;
    }

    public String getType() {
        return type;
    }
    void setType(String t) {
        type = t;
    }

    public Drawable getDrawable(){
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
                Drawable image = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                return image;
            }
        } catch (Exception e) {
            Log.e("Error", "Failed to set drawable" + e.getMessage());
        }
        return null;
    }
}

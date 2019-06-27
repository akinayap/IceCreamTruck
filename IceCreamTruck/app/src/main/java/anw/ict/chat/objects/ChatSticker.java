package anw.ict.chat.objects;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import anw.ict.R;
import pl.droidsonroids.gif.GifDrawable;

import static anw.ict.utils.Constants.IMAGE_GIF;

public class ChatSticker {
    private Context context;
    private String name, timestamp, folder;

    ChatSticker() {}
    public ChatSticker(String f, Context c, String filename) {
        folder = f;
        context = c;
        name = filename;
    }

    public void setContext(Context c)
    {
        context = c;
    }

    public String getTimestamp() {
        return timestamp;
    }
    void setTimestamp(String t) {
        timestamp = t;
    }

    public String getName() {
        return name;
    }
    void setName(String n) {
        name = n;
    }

    public String getFolder() {
        return folder;
    }
    public void setFolder(String name) {
        folder = name;
    }

    public Drawable drawable(){
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
                return gifFromBytes;
            } catch (Exception eer) {
                return new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        } catch (Exception e) {
            return context.getDrawable(R.drawable.ic_login_load);
        }
    }
}

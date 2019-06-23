package anw.icecreamtruck.chat.objects;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import anw.icecreamtruck.R;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static anw.icecreamtruck.utils.Constants.IMAGE_GIF;

public class ChatSticker {
    private Context context;
    private String name, timestamp, folder;
    private boolean folderActivity = false;

    ChatSticker() {}
    public ChatSticker(String f, Context c, String filename) {
        folder = f;
        context = c;
        name = filename;
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
                Log.e("Error", "Image is not GIF" + eer.getMessage());
                Drawable image = new BitmapDrawable(context.getResources(), BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                return image;
            }
        } catch (Exception e) {
            Log.e("Error", "Failed to set drawable" + e.getMessage());
            Drawable image = context.getDrawable(R.drawable.ic_loading);
            return image;
        }
    }
}

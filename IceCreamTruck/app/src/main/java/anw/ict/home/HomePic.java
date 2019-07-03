package anw.ict.home;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import anw.ict.R;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static anw.ict.utils.Constants.IMAGE_PHOTO;
import static anw.ict.utils.Constants.MEMORIES;

public class HomePic {
    private String name;
    private Context context;

    public HomePic(Context c, String filename) {
        name = filename;
        context = c;
    }
    public void drawable(GifImageView giv){
        byte[] bytes = new byte[IMAGE_PHOTO];

        try {
            GifDrawable gifFromBytes;
            FileInputStream file = context.openFileInput(name);

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
            FirebaseStorage.getInstance().getReference(MEMORIES).child(name).getBytes(IMAGE_PHOTO).addOnSuccessListener(bytes1 -> {
                try {
                    GifDrawable gifFromBytes;
                    FileOutputStream outputStream = context.openFileOutput(name, Context.MODE_PRIVATE);
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
}

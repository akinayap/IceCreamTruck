package anw.ict.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import anw.ict.R;
import anw.ict.chat.objects.ChatSticker;
import pl.droidsonroids.gif.GifImageView;

import static anw.ict.utils.Constants.IMAGE_PHOTO;
import static anw.ict.utils.Constants.MEMORIES;
import static anw.ict.utils.Constants.UPLOAD_MEMORIES;

// 1 4 9 16 25

public class HomeFrag extends Fragment {

    private ValueEventListener imageListener;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        return inflater.inflate(R.layout.frag_home, parent, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FirebaseDatabase.getInstance().getReference(MEMORIES).removeEventListener(imageListener);

    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        imageListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long imgCount = dataSnapshot.getChildrenCount();

                LinearLayout wholeImage = view.findViewById(R.id.img_slideshow);
                wholeImage.removeAllViews();
                int row = (int) Math.round(Math.sqrt((double)imgCount) + 0.4);
                int objW = wholeImage.getMeasuredWidth() / row;
                int objH = wholeImage.getMeasuredHeight() / row;


                Log.e("row", String.valueOf(row));
                Log.e("objH", String.valueOf(objH));
                int i = 0;
                LinearLayout ll = null;
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    Log.e("Child", child.getKey());
                    if(i == 0){
                        ll = new LinearLayout(getContext());
                    }

                    GifImageView iv = new GifImageView(getContext());
                    iv.setImageResource(R.drawable.ic_load);
                    HomePic pic = new HomePic(getContext(), child.getKey());
                    pic.drawable(iv);
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Objects.requireNonNull(ll).addView(iv, objW, objH);

                    ++i;
                    if(i == row){
                        i = 0;
                        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        ll.setOrientation(LinearLayout.HORIZONTAL);
                        wholeImage.addView(ll);
                    }
                }
                if(i != 0){
                    ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    ll.setOrientation(LinearLayout.HORIZONTAL);
                    wholeImage.addView(ll);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };

        // Download All Pictures
        db.getReference(MEMORIES).addValueEventListener(imageListener);

        ImageButton add_btn = view.findViewById(R.id.add_btn);
        add_btn.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, UPLOAD_MEMORIES);
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == UPLOAD_MEMORIES){
                Log.e("Activity intent", "UPLOAD");
                if (data.getClipData() != null) {
                    Log.e("dataclip", "UPLOAD");
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; ++i) {
                        Uri file = data.getClipData().getItemAt(i).getUri();
                        uploadImage(file);
                    }
                } else if (data.getData() != null) {
                    Log.e("data", "UPLOAD");
                    Uri file = data.getData();
                    uploadImage(file);
                }
            }
        }
    }

    private void uploadImage(Uri file) {
        long fileSize = 0;
        try {
            AssetFileDescriptor afd = Objects.requireNonNull(getActivity()).getContentResolver().openAssetFileDescriptor(file, "r");
            assert afd != null;
            fileSize = afd.getLength();
            afd.close();
        } catch (Exception e) {
            Log.e("Invalid", "File not valid");
        }

        if (fileSize > IMAGE_PHOTO || fileSize <= 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("File too large");
            builder.setMessage("Image size must be less than 10MB");

            // Set up the buttons
            builder.setPositiveButton("Optimize Image", (dialog, which) -> {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ezgif.com")));
                dialog.dismiss();
            });
            builder.setNegativeButton("OK", (dialog, which) -> dialog.cancel());

            builder.show();
        } else {
            String name = String.valueOf(new Date().getTime());

            UploadTask ut = FirebaseStorage.getInstance().getReference(MEMORIES).child(name).putFile(file);
            ut.addOnFailureListener(exception -> Toast.makeText(getContext(), "Photo not uploaded. :(", Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getContext(), "Photo uploaded!", Toast.LENGTH_SHORT).show();
                        FirebaseDatabase.getInstance().getReference(MEMORIES).child(name).setValue(name);
                    });
        }
    }
}

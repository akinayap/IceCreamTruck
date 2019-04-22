package com.example.icecreamtruckv2.chat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.icecreamtruckv2.utils.Constants;
import com.example.icecreamtruckv2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicMarkableReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageButton;

public class ChatFrag extends Fragment {

    /** Database instance **/
    private DatabaseReference root, notif, stickerDR;
    private StorageReference stickerSR;

    /** UI Components **/
    private RecyclerView chat, sticker;
    private EditText mChatInput;
    private ChatLogAdapter mAdapter;
    private ChatStickersAdapter stickerAdapter;

    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private FirebaseInstanceId fid;
    private FirebaseStorage storage;

    private SharedPreferences sharedPreferences;
    private String userRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userRole = sharedPreferences.getString("userRole", "null");
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        fid = FirebaseInstanceId.getInstance();
        storage = FirebaseStorage.getInstance();

        return inflater.inflate(R.layout.chat_frag, parent, false);
    }


    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        chat = view.findViewById(R.id.list_of_messages);
        mAdapter = new ChatLogAdapter();
        chat.setAdapter(mAdapter);
        chat.setLayoutManager(new LinearLayoutManager(getContext()));

        try {
            root = db.getReference(Constants.CHAT_DB);
            root.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    ChatMessage data = dataSnapshot.getValue(ChatMessage.class);
                    mAdapter.addData(data);
                    chat.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
        }


        sticker = view.findViewById(R.id.rv_stickers);
        stickerAdapter = new ChatStickersAdapter(new ChatStickersAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(ChatSticker item) {
                Log.e("GIF", "SENT");
                mChatInput = view.findViewById(R.id.input);
                ChatMessage data = new ChatMessage();
                data.setMessage(item.getName());
                data.setType("GIF");
                data.setId("0");

                if (userRole.equals("ahgirl")) {
                    data.setIcon(R.drawable.girl);
                    data.setName("Ah Girl");
                    //fortestinguse
                    //notif = db.getReference(Constants.CHAT_DB + "girl");

                    notif = db.getReference(Constants.CHAT_DB + "boy");
                    FirebaseMessaging.getInstance().subscribeToTopic("pushGirlNotifications");
                } else {
                    data.setIcon(R.drawable.boy);
                    data.setName("Ah Boy");
                    notif = db.getReference(Constants.CHAT_DB + "girl");
                    FirebaseMessaging.getInstance().subscribeToTopic("pushBoyNotifications");
                }

                notif.child(String.valueOf(new Date().getTime())).setValue(data);
                root.child(String.valueOf(new Date().getTime())).setValue(data);
                mChatInput.setText("");
            }
        });
        sticker.setAdapter(stickerAdapter);
        sticker.setLayoutManager(new GridLayoutManager(getContext(), 3, RecyclerView.HORIZONTAL, false));

        try {
            stickerDR = db.getReference(Constants.STICKERS_DB);
            stickerDR.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    ChatSticker data = dataSnapshot.getValue(ChatSticker.class);
                    stickerAdapter.addData(data);
                    stickerAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
        }

        FloatingActionButton fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                mChatInput = view.findViewById(R.id.input);
                ChatMessage data = new ChatMessage();
                data.setMessage(mChatInput.getText().toString());
                data.setType("MSG");
                data.setId("0");

                if (userRole.equals("ahgirl")) {
                    data.setIcon(R.drawable.girl);
                    data.setName("Ah Girl");
                    //fortestinguse
                    //notif = db.getReference(Constants.CHAT_DB + "girl");

                    notif = db.getReference(Constants.CHAT_DB + "boy");
                    FirebaseMessaging.getInstance().subscribeToTopic("pushGirlNotifications");
                } else {
                    data.setIcon(R.drawable.boy);
                    data.setName("Ah Boy");
                    notif = db.getReference(Constants.CHAT_DB + "girl");
                    FirebaseMessaging.getInstance().subscribeToTopic("pushBoyNotifications");
                }

                notif.child(String.valueOf(new Date().getTime())).setValue(data);
                root.child(String.valueOf(new Date().getTime())).setValue(data);
                mChatInput.setText("");
            }
        });

        FloatingActionButton imgUpload = view.findViewById(R.id.img);

        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });

        FloatingActionButton openKeyboard = view.findViewById(R.id.gif_btn);

        openKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View kb = view.findViewById(R.id.sticker_keyboard);
                if(kb.getVisibility() == View.VISIBLE)

                    kb.setVisibility(View.GONE);
                else
                    kb.setVisibility(View.VISIBLE);

                chat.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {

                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for(int i = 0; i < count; ++i)
                    {
                        Uri file = data.getClipData().getItemAt(i).getUri();
                        uploadSticker(file);
                    }
                }
                else if(data.getData() != null)
                {
                    Uri file = data.getData();
                    uploadSticker(file);
                }
            }
    }

    private void uploadSticker(Uri file)
    {
/*
        try {
            java.net.URI juri = new java.net.URI(file.toString());
            String extension =  MimeTypeMap.getFileExtensionFromUrl(file.toString());
            Log.e("URI", extension);
            // if file wrong format
            if(!extension.equals(".gif"))
            {
                Log.e("Error", "format not supported");
                Toast.makeText(getContext(), "Image format not supported. :(", Toast.LENGTH_SHORT);
                return;
            }
            // if file is too large

            final int TWO_MEGABYTE = 1024 * 1024  * 2;
            if(juri.getPath().length() > TWO_MEGABYTE)
            {
                Log.e("Error", "gif too large");
                Toast.makeText(getContext(), "Image too large. :(", Toast.LENGTH_SHORT);
                return;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
*/



        stickerSR = storage.getReference(Constants.STICKERS_DB);
        final ChatSticker imgName = new ChatSticker();
        imgName.setId("0");
        imgName.setName(String.valueOf(new Date().getTime()));

        StorageReference storageRef = stickerSR.child(imgName.getName());
        UploadTask ut = storageRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        ut.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getContext(), "Image not uploaded. :(", Toast.LENGTH_SHORT);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "GIF added!", Toast.LENGTH_SHORT);
                stickerDR = db.getReference(Constants.STICKERS_DB);
                stickerDR.child(imgName.getName()).setValue(imgName);
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }
}

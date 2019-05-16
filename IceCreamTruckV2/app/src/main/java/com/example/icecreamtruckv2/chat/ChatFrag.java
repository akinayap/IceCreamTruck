package com.example.icecreamtruckv2.chat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icecreamtruckv2.R;
import com.example.icecreamtruckv2.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifDrawable;

public class ChatFrag extends Fragment {

    /** Database instance **/
    private DatabaseReference root, notif, stickerDR;
    private StorageReference stickerSR;

    /** UI Components **/
    private RecyclerView chat, sticker;
    private EditText mChatInput;
    private static ChatLogAdapter mAdapter;
    private static ChatStickersAdapter stickerAdapter;

    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private FirebaseInstanceId fid;
    private FirebaseStorage storage;

    private SharedPreferences sharedPreferences;
    public static String userRole;

    public static List<ChatSticker> stickers = new ArrayList<>();
    public static List<ChatMessage> messages = new ArrayList<>();
    private ChildEventListener chatListener, stickerListener;

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
        mAdapter = new ChatLogAdapter(messages);
        chat.setAdapter(mAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        chat.setLayoutManager(llm);
        chatListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatMessage data = dataSnapshot.getValue(ChatMessage.class);
                messages.add(data);
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
        };

        try {
            root = db.getReference(Constants.CHAT_DB);
            root.limitToLast(100).addChildEventListener(chatListener);
        } catch (Exception e) {
        }

        sticker = view.findViewById(R.id.rv_stickers);
        stickerAdapter = new ChatStickersAdapter(stickers, new ChatStickersAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(ChatSticker item) {
                Log.e("GIF", "SENT");
                mChatInput = view.findViewById(R.id.input);
                ChatMessage data = new ChatMessage();
                data.setMessage(item.getName());
                data.setType("GIF");
                data.setTimestamp(new SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.US).format(new Date().getTime()));

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
        sticker.setLayoutManager(new GridLayoutManager(getContext(), 2, RecyclerView.HORIZONTAL, false));
        stickerListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("Child Added", "HERE");
                ChatSticker data = dataSnapshot.getValue(ChatSticker.class);
                data.setContext(getContext());
                stickers.add(data);
                stickerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                stickerAdapter.notifyItemRemoved(dataSnapshot.getValue(ChatMessage.class).getID());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        try {
            stickerDR = db.getReference(Constants.STICKERS_DB);
            stickerDR.addChildEventListener(stickerListener);
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
                data.setTimestamp(new SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.US).format(new Date().getTime()));

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
                if(mAdapter.getItemCount() > 0)
                    chat.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        root.removeEventListener(chatListener);
        stickerDR.removeEventListener(stickerListener);
        messages.clear();
        stickers.clear();
        mAdapter.clearData();
        stickerAdapter.clearData();
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
        stickerSR = storage.getReference(Constants.STICKERS_DB);
        final ChatSticker imgName = new ChatSticker();
        imgName.setName(String.valueOf(new Date().getTime()));

        StorageReference storageRef = stickerSR.child(imgName.getName());
        UploadTask ut = storageRef.putFile(file);

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
            }
        });
    }
}

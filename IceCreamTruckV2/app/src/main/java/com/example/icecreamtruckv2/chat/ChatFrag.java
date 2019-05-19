package com.example.icecreamtruckv2.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.google.android.material.textfield.TextInputLayout;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifDrawable;

public class ChatFrag extends Fragment {
    private SharedPreferences sharedPreferences;
    public static String userRole;

    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private FirebaseInstanceId fid;
    private FirebaseStorage storage;

    /** Database instance **/
    private DatabaseReference chatListRoot, stickerListRoot, notifRoot;
    private StorageReference stickerObjectsRoot;

    /** UI Components **/
    private RecyclerView chatRV, stickerRV;
    private EditText chatInput;
    private ChatLogAdapter chatAdapter;
    private ChatStickersAdapter stickerAdapter;

    /** Array Lists **/
    private List<ChatSticker> stickerList = new ArrayList<>();
    private List<ChatMessage> chatList = new ArrayList<>();

    /** Listeners**/
    private ChildEventListener chatListener, stickerListener;
    private ChatStickersAdapter.OnItemClickListener stickerClick;
    private View.OnClickListener sendClick, uploadClick, sKeyboardClick;

    private boolean justUploaded = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userRole = sharedPreferences.getString("userRole", "null");
        Log.e("WHOAMI", userRole);
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
        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FirebaseMessaging.getInstance().subscribeToTopic(userRole.equals("ahgirl") ? "pushGirlNotifications" : "pushBoyNotifications");
        initListeners(view);
        initRV(view);
        loadItems(view);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        chatListRoot.removeEventListener(chatListener);
        stickerListRoot.removeEventListener(stickerListener);
        chatList.clear();
        stickerList.clear();
        chatAdapter.clearData();
        stickerAdapter.clearData();
    }


    private void initListeners(View view) {
        chatInput = view.findViewById(R.id.input);

        chatListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatMessage data = dataSnapshot.getValue(ChatMessage.class);
                chatList.add(data);
                chatRV.scrollToPosition(chatAdapter.getItemCount() - 1);
                //chatRV.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatMessage data = dataSnapshot.getValue(ChatMessage.class);
                chatList.removeIf(c -> (data.getTimestamp().equals(c.getTimestamp())));
                chatList.add(data);
                chatRV.scrollToPosition(chatAdapter.getItemCount() - 1);
                //chatRV.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                ChatMessage data = dataSnapshot.getValue(ChatMessage.class);
                chatList.removeIf(c -> (data.getTimestamp().equals(c.getTimestamp())));
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        stickerListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatSticker data = dataSnapshot.getValue(ChatSticker.class);
                data.setContext(getContext());
                stickerList.add(data);
                if(justUploaded)
                {
                    stickerRV.smoothScrollToPosition(stickerAdapter.getItemCount() - 1);
                    justUploaded = false;
                }
                stickerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                ChatSticker data = dataSnapshot.getValue(ChatSticker.class);
                stickerList.removeIf(s -> (data.getTimestamp().equals(s.getTimestamp())));
                stickerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        stickerClick = item -> {
            ChatMessage data = new ChatMessage();
            data.setMessage(item.getName());
            data.setType("GIF");
            data.setTimestamp(new SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.US).format(new Date().getTime()));
            data.setUsername(userRole);
            notifRoot = db.getReference(Constants.CHAT_DB + (userRole.equals("ahgirl") ? "boy" : "girl"));
            chatListRoot.child(String.valueOf(new Date().getTime())).setValue(data);
            notifRoot.child(String.valueOf(new Date().getTime())).setValue(data);
        };
        sendClick = send -> {
            if (!chatInput.getText().toString().trim().equals("")) {
                ChatMessage data = new ChatMessage();
                data.setMessage(chatInput.getText().toString().trim());
                data.setType("MSG");
                data.setTimestamp(new SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.US).format(new Date().getTime()));
                data.setUsername(userRole);
                notifRoot = db.getReference(Constants.CHAT_DB + (userRole.equals("ahgirl") ? "boy" : "girl"));
                chatListRoot.child(String.valueOf(new Date().getTime())).setValue(data);
                notifRoot.child(String.valueOf(new Date().getTime())).setValue(data);
                chatInput.setText("");
            }
        };
        uploadClick = send -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        };
        sKeyboardClick = send -> {
            View kb = view.findViewById(R.id.sticker_keyboard);
            if(kb.getVisibility() == View.VISIBLE)
                kb.setVisibility(View.GONE);
            else
                kb.setVisibility(View.VISIBLE);

            if(chatAdapter.getItemCount() > 0) {
                chatRV.scrollToPosition(chatAdapter.getItemCount() - 1);
                chatAdapter.notifyDataSetChanged();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isAcceptingText()) {
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }
            }
        };
        TextInputLayout tv = view.findViewById(R.id.textInputLayout);
        tv.setOnClickListener(v -> {
            View kb = view.findViewById(R.id.sticker_keyboard);
            kb.setVisibility(View.GONE);});
        chatInput.setOnClickListener(v -> {
            View kb = view.findViewById(R.id.sticker_keyboard);
            kb.setVisibility(View.GONE);
        });
    }
    private void initRV(View view) {
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        chatRV =  view.findViewById(R.id.list_of_messages);
        chatAdapter = new ChatLogAdapter(chatList);
        chatRV.setAdapter(chatAdapter);
        chatRV.setLayoutManager(llm);

        GridLayoutManager glm = new GridLayoutManager(getContext(), 2, RecyclerView.HORIZONTAL, false);
        stickerRV =  view.findViewById(R.id.rv_stickers);
        stickerAdapter = new ChatStickersAdapter(stickerList, stickerClick);
        stickerRV.setAdapter(stickerAdapter);
        stickerRV.setLayoutManager(glm);
    }
    private void loadItems(View view) {
        // Load chat messages
        try {
            chatListRoot = db.getReference(Constants.CHAT_DB);
            chatListRoot.limitToLast(100).addChildEventListener(chatListener);
        } catch (Exception e) {}

        // Load stickers
        try {
            stickerListRoot = db.getReference(Constants.STICKERS_DB);
            stickerListRoot.addChildEventListener(stickerListener);
        } catch (Exception e) {}

        // Load buttons with listeners
        FloatingActionButton fab =  view.findViewById(R.id.fab);
        fab.setOnClickListener(sendClick);
        FloatingActionButton imgUpload =  view.findViewById(R.id.img);
        imgUpload.setOnClickListener(uploadClick);
        FloatingActionButton openKeyboard =  view.findViewById(R.id.gif_btn);
        openKeyboard.setOnClickListener(sKeyboardClick);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {

                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for(int i = 0; i < count; ++i) {
                        Uri file = data.getClipData().getItemAt(i).getUri();
                        uploadSticker(file);
                    }
                }
                else if(data.getData() != null) {
                    Uri file = data.getData();
                    uploadSticker(file);
                }
            }
    }
    private void uploadSticker(Uri file) {
        stickerObjectsRoot = storage.getReference(Constants.STICKERS_DB);
        final ChatSticker imgName = new ChatSticker();
        imgName.setName(String.valueOf(new Date().getTime()));
        imgName.setTimestamp(String.valueOf(new Date().getTime()));

        StorageReference storageRef = stickerObjectsRoot.child(imgName.getName());
        UploadTask ut = storageRef.putFile(file);

        ut.addOnFailureListener(exception -> Toast.makeText(getContext(), "Image not uploaded. :(", Toast.LENGTH_SHORT))
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(getContext(), "GIF added!", Toast.LENGTH_SHORT);
                    stickerListRoot = db.getReference(Constants.STICKERS_DB);
                    stickerListRoot.child(imgName.getName()).setValue(imgName);
                    justUploaded = true;
                    stickerRV.smoothScrollToPosition(stickerAdapter.getItemCount() - 1);
                });
    }


}

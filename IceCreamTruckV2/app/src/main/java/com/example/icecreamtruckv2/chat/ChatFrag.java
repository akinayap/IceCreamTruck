package com.example.icecreamtruckv2.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.icecreamtruckv2.R;
import com.example.icecreamtruckv2.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChatFrag extends Fragment {
    static int IMAGE_GIF = 1024 * 1024;

    static TabLayout tabLayout;
    static String userRole, userid;
    static FirebaseDatabase database = FirebaseDatabase.getInstance();

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference stickerRoot, chatRoot, notifRoot;
    private StorageReference stickerStorage;

    private List<ChatMessage> chatList = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private ChildEventListener stickerListener, chatListener;

    private EditText chatInput;
    private RecyclerView chatRV;
    private ChatLogAdapter chatAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Log.e("Function", "ChatFrag.onCreateView");
        RemoveAllNotification();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userRole = sharedPreferences.getString("userRole", "null");
        userid = sharedPreferences.getString("userid", "null");
        FirebaseMessaging.getInstance().subscribeToTopic(userRole.equals("ahgirl") ? "pushGirlNotifications" : "pushBoyNotifications");
        return inflater.inflate(R.layout.chat_frag, parent, false);
    }
    private void RemoveAllNotification() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("msg", "");
        editor.apply();
        NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        Log.e("Function", "ChatFrag.onViewCreated");
        initListeners();
        loadStickers();
        loadChat(view);
        initTabs(view);
        loadUI(view);
    }

    private void initListeners(){
        Log.e("Function", "ChatFrag.initListeners");
        // Download sticker everytime there is a new one.
        stickerListener = new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                downloadSticker(dataSnapshot.getKey());
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };

        // Get chat log details.
        chatListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatMessage data = dataSnapshot.getValue(ChatMessage.class);
                chatList.add(data);
                chatAdapter.notifyItemInserted(chatList.size()-1);
                chatRV.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("Message", "Edited");
                ChatMessage data = dataSnapshot.getValue(ChatMessage.class);
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Message", "Deleted");
                ChatMessage data = dataSnapshot.getValue(ChatMessage.class);
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
    }
    private void downloadSticker(String filename) {
        try {
            FileInputStream file = getContext().openFileInput(filename);
        } catch (Exception e) {
            stickerStorage = storage.getReference(Constants.STICKERS_DB).child(filename);
            stickerStorage.getBytes(IMAGE_GIF).addOnSuccessListener(bytes -> {
                try {
                    FileOutputStream outputStream = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(bytes);
                    outputStream.close();
                    Log.e("Downloaded", filename);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }).addOnFailureListener(exception -> {
                Log.e("Error", "Failed to get sticker");
            });
        }
    }
    private void loadStickers() {
        Log.e("Function", "ChatFrag.loadStickers");
        stickerRoot = database.getReference(Constants.STICKERS_DB);
        stickerRoot.addChildEventListener(stickerListener);
    }

    private void loadChat(View view) {
        Log.e("Function", "ChatFrag.loadChat");
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        chatRV = view.findViewById(R.id.list_of_messages);
        chatAdapter = new ChatLogAdapter(chatList);
        chatRV.setAdapter(chatAdapter);
        chatRV.setLayoutManager(llm);

        try {
            chatRoot = database.getReference(Constants.CHAT_DB);
            chatRoot.limitToLast(20).addChildEventListener(chatListener);
        } catch (Exception e) {
            Log.e("Error", "Failed to load chat");
        }

    }
    private void initTabs(View view) {
        Log.e("Function", "ChatFrag.initTabs");
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        ChatViewPagerAdapter pagerAdapter = new ChatViewPagerAdapter(getActivity().getSupportFragmentManager(), getContext());
        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

    }
    private void loadUI(View view) {
        Log.e("Function", "ChatFrag.loadUI");
        chatInput = view.findViewById(R.id.input);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener( send -> {
            if (!chatInput.getText().toString().trim().equals("")) {
                ChatMessage data = new ChatMessage();
                data.setContext(getContext());
                data.setMessage(chatInput.getText().toString().trim());
                data.setType("MSG");
                data.setTimestamp(new SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.US).format(new Date().getTime()));
                data.setUsername(userRole);
                notifRoot = database.getReference(userRole.equals("ahgirl") ? Constants.CHAT_BOY : Constants.CHAT_GIRL);
                chatRoot.child(String.valueOf(new Date().getTime())).setValue(data);
                notifRoot.child(String.valueOf(new Date().getTime())).setValue(data);
                chatInput.setText("");
            }
        });
        FloatingActionButton imgUpload = view.findViewById(R.id.img);
        imgUpload.setOnClickListener(send -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        });
        FloatingActionButton openKeyboard = view.findViewById(R.id.gif_btn);
        openKeyboard.setOnClickListener( send -> {
            View kb = view.findViewById(R.id.sticker_keyboard);
            if (kb.getVisibility() == View.VISIBLE)
                kb.setVisibility(View.GONE);
            else {
                kb.setVisibility(View.VISIBLE);
            }

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isAcceptingText()) {
                imm.hideSoftInputFromWindow(Objects.requireNonNull(getActivity().getCurrentFocus()).getWindowToken(), 0);
            }
            if (chatAdapter.getItemCount() > 0) {
                chatRV.scrollToPosition(chatAdapter.getItemCount() - 1);
                //chatAdapter.notifyDataSetChanged();
            }
        });
        FloatingActionButton folderFrag = view.findViewById(R.id.add_folder);
        folderFrag.setOnClickListener(open->{
            Fragment frag = new FolderFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.frag, frag ); // give your fragment container id in first parameter
            transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
            transaction.commit();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("Function", "ChatFrag.onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {

                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; ++i) {
                        Uri file = data.getClipData().getItemAt(i).getUri();
                        uploadSticker(file);
                    }
                } else if (data.getData() != null) {
                    Uri file = data.getData();
                    uploadSticker(file);
                }
            }
    }
    private void uploadSticker(Uri file) {
        Log.e("Function", "ChatFrag.uploadSticker");
        long fileSize = 0;
        try {
            AssetFileDescriptor afd = getActivity().getContentResolver().openAssetFileDescriptor(file, "r");
            assert afd != null;
            fileSize = afd.getLength();
            afd.close();
        } catch (Exception e) {
            Log.e("Invalid", "File not valid");
        }

        if (fileSize > IMAGE_GIF || fileSize <= 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("File too large");
            builder.setMessage("Image size must be less than 1MB");

            // Set up the buttons
            builder.setPositiveButton("Optimize Image", (dialog, which) -> {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ezgif.com")));
                dialog.dismiss();
            });
            builder.setNegativeButton("OK", (dialog, which) -> dialog.cancel());

            builder.show();
        } else {

            stickerStorage = storage.getReference(Constants.STICKERS_DB);
            final ChatSticker imgName = new ChatSticker();
            imgName.setName(String.valueOf(new Date().getTime()));
            imgName.setTimestamp(String.valueOf(new Date().getTime()));

            StorageReference storageRef = stickerStorage.child(imgName.getName());
            UploadTask ut = storageRef.putFile(file);
            ut.addOnFailureListener(exception -> Toast.makeText(getContext(), "Image not uploaded. :(", Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getContext(), "GIF added!", Toast.LENGTH_SHORT).show();
                        stickerRoot = database.getReference(Constants.STICKERS_DB);
                        stickerRoot.child(imgName.getName()).setValue(imgName);
                    });
        }
    }

    @Override
    public void onDestroyView(){
        Log.e("Function", "ChatFrag.onDestroyView");
        super.onDestroyView();
        chatRoot.removeEventListener(chatListener);
        stickerRoot.removeEventListener(stickerListener);
    }
}

package anw.ict.chat.fragments;

import android.annotation.SuppressLint;
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
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import anw.ict.R;
import anw.ict.SplashActivity;
import anw.ict.chat.adapter.ChatLogAdapter;
import anw.ict.chat.adapter.ChatViewPagerAdapter;
import anw.ict.chat.objects.ChatMessage;
import anw.ict.chat.objects.ChatNotification;

import static anw.ict.utils.Constants.CHAT_LOG;
import static anw.ict.utils.Constants.IMAGE_GIF;
import static anw.ict.utils.Constants.NOTIFICATIONS;
import static anw.ict.utils.Constants.STICKERS;

public class ChatFrag extends Fragment{
    private String userRole, userId;

    private List<ChatMessage> chatList = new ArrayList<>();
    private ChildEventListener chatListener;

    private EditText chatInput;
    private RecyclerView chatRV;
    private ChatLogAdapter chatAdapter;
    private ChatViewPagerAdapter pagerAdapter;
    private FirebaseDatabase db;
    private FirebaseStorage st;

    public static TabLayout tabLayout;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        // Init user details
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userRole = sharedPreferences.getString("userRole", "null");
        userId = sharedPreferences.getString("userId", "null");

        db = FirebaseDatabase.getInstance();
        st = FirebaseStorage.getInstance();

        // Init chat
        chatListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatMessage data = dataSnapshot.getValue(ChatMessage.class);
                if(data != null){
                    data.setContext(getContext());
                    chatList.add(data);
                    if(chatAdapter != null){
                        chatAdapter.notifyItemInserted(chatList.size()-1);
                        chatRV.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                    }
                }
                clearNotifications();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("Message", "Edited");
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Message", "Deleted");
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        try {
            db.getReference(CHAT_LOG).limitToLast(50).addChildEventListener(chatListener);
        } catch (Exception e) {
            Log.e("Error", "Failed to load chat");
        }

        return inflater.inflate(R.layout.frag_chat, parent, false);
    }

    private void clearNotifications() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("msg", "");
        editor.apply();

        NotificationManager mNotificationManager = (NotificationManager) Objects.requireNonNull(getContext()).getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e("ChatFrag", "onViewCreated");

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        chatRV = view.findViewById(R.id.list_of_messages);
        chatAdapter = new ChatLogAdapter(chatList);
        chatRV.setAdapter(chatAdapter);
        chatRV.setLayoutManager(llm);

        ImageButton stickerKeyboardBtn = view.findViewById(R.id.sticker_btn);
        ImageButton addStickerBtn = view.findViewById(R.id.add_sticker_btn);
        ImageButton sendBtn = view.findViewById(R.id.send_btn);
        ImageButton folderBtn = view.findViewById(R.id.folder_btn);
        ImageButton cameraBtn = view.findViewById(R.id.camera_btn);

        chatInput = view.findViewById(R.id.input);

        chatInput.setOnTouchListener((v, event) -> {
            View keyboard = view.findViewById(R.id.sticker_keyboard);
            if(keyboard.getVisibility() == View.VISIBLE)
                keyboard.setVisibility(View.GONE);
            return false;
        });

        stickerKeyboardBtn.setOnClickListener(v -> {
            View keyboard = view.findViewById(R.id.sticker_keyboard);
            if(keyboard.getVisibility() == View.VISIBLE)
                keyboard.setVisibility(View.GONE);
            else
                keyboard.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm.isAcceptingText())
                imm.hideSoftInputFromWindow(Objects.requireNonNull(getActivity().getCurrentFocus()).getWindowToken(), 0);

            if(!chatList.isEmpty())
                chatRV.smoothScrollToPosition(chatList.size() - 1);
        });
        addStickerBtn.setOnClickListener(v->{
            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        });
        sendBtn.setOnClickListener(v->{
            String msg = chatInput.getText().toString().trim();
            Long time = new Date().getTime();
            String timestamp = new SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.US).format(time);
            if (!msg.equals("")) {
                ChatMessage data = new ChatMessage(getContext(), msg,"MSG", timestamp, userRole);
                db.getReference(CHAT_LOG).child(String.valueOf(time)).setValue(data);
                chatInput.setText("");
                sendNotification(String.valueOf(time), data);
            }
        });
        folderBtn.setOnClickListener(v-> Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frag, new FolderFrag()).addToBackStack(null).commit());
        cameraBtn.setOnClickListener(v->{
            /*Show Camera REMOVE THE LOGOUT*/
            FirebaseAuth.getInstance().signOut();

            Intent i = new Intent(getContext(), SplashActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
        initTabs(view);
    }

    private void sendNotification(String time, ChatMessage data) {
        ChatNotification notif = new ChatNotification(data);
        db.getReference(NOTIFICATIONS + "/" + userId + "/" + time).setValue(notif);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.e("ChatFrag", "Destroy View");
        db.getReference(CHAT_LOG).removeEventListener(chatListener);
        pagerAdapter.removeListener();
    }

    private void initTabs(View view) {
        Log.e("Function", "ChatFrag.initTabs");
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        pagerAdapter = new ChatViewPagerAdapter(getChildFragmentManager(), getContext());
        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
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
            AssetFileDescriptor afd = Objects.requireNonNull(getActivity()).getContentResolver().openAssetFileDescriptor(file, "r");
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
            String name = String.valueOf(new Date().getTime());

            UploadTask ut = st.getReference(STICKERS).child(name).putFile(file);
            ut.addOnFailureListener(exception -> Toast.makeText(getContext(), "Image not uploaded. :(", Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getContext(), "GIF added!", Toast.LENGTH_SHORT).show();
                        db.getReference(STICKERS).child(name).setValue(name);
                    });
        }
    }
}
package anw.ict.chat.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import anw.ict.R;
import anw.ict.chat.adapter.ChatLogAdapter;
import anw.ict.chat.adapter.ChatViewPagerAdapter;
import anw.ict.chat.callback.MessageSwipeCallback;
import anw.ict.chat.objects.ChatMessage;
import anw.ict.chat.objects.ChatNotification;
import pl.droidsonroids.gif.GifImageView;

import static android.view.View.GONE;
import static anw.ict.utils.Constants.CAM_PERMISSION;
import static anw.ict.utils.Constants.CHAT_LOG;
import static anw.ict.utils.Constants.IMAGE_GIF;
import static anw.ict.utils.Constants.IMAGE_PHOTO;
import static anw.ict.utils.Constants.MSG;
import static anw.ict.utils.Constants.NOTIFICATIONS;
import static anw.ict.utils.Constants.PHOTOS;
import static anw.ict.utils.Constants.PIC;
import static anw.ict.utils.Constants.STICKERS;
import static anw.ict.utils.Constants.TAKE_PIC_AND_SEND;
import static anw.ict.utils.Constants.UPLOAD_IMAGE_AND_SEND;
import static anw.ict.utils.Constants.UPLOAD_STICKERS;

public class ChatFrag extends Fragment{
    private String userRole, userId;

    private static List<ChatMessage> chatList = new ArrayList<>();
    private ChildEventListener chatListener;

    private EditText chatInput;
    private ChatLogAdapter chatAdapter;
    private static ConstraintLayout replyBox;
    private static ConstraintLayout imagePreview;
    private ChatViewPagerAdapter pagerAdapter;
    private FirebaseDatabase db;
    private FirebaseStorage st;

    public static RecyclerView chatRV;
    public static TabLayout tabLayout;
    public static int reply = -1;

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
                    if(dataSnapshot.child("reply").getValue() != null)
                    {
                        ChatMessage reply = dataSnapshot.child("reply").getValue(ChatMessage.class);
                        if(reply != null){
                            reply.setContext(getContext());
                            data.reply = reply;
                        }
                    }
                    data.setContext(getContext());
                    chatList.add(data);
                    if(chatAdapter != null){
                        chatAdapter.notifyItemInserted(chatList.size()-1);
                        chatRV.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                    }
                }
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
        clearNotifications();
        replyBox = view.findViewById(R.id.reply_view);
        imagePreview = view.findViewById(R.id.img_preview_layout);
        if(imagePreview.getVisibility() == View.VISIBLE)
            imagePreview.setVisibility(GONE);

        if(reply < 0) {
            replyBox.setVisibility(GONE);
        }
        else{
            showReply(null);
        }

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        chatRV = view.findViewById(R.id.list_of_messages);
        chatAdapter = new ChatLogAdapter(chatList);
        chatRV.setAdapter(chatAdapter);
        chatRV.setLayoutManager(llm);
        MessageSwipeCallback swipeCallback = new MessageSwipeCallback(chatAdapter);

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeCallback);
        itemTouchhelper.attachToRecyclerView(chatRV);

        try {
            chatList.clear();
            chatAdapter.notifyDataSetChanged();
            db.getReference(CHAT_LOG).removeEventListener(chatListener);
            db.getReference(CHAT_LOG).limitToLast(50).addChildEventListener(chatListener);
        } catch (Exception e) {
            Log.e("Error", "Failed to load chat");
        }

        ImageButton stickerKeyboardBtn = view.findViewById(R.id.sticker_btn);
        ImageButton addStickerBtn = view.findViewById(R.id.add_sticker_btn);
        ImageButton sendBtn = view.findViewById(R.id.send_btn);
        ImageButton folderBtn = view.findViewById(R.id.folder_btn);
        ImageButton cameraBtn = view.findViewById(R.id.camera_btn);

        chatInput = view.findViewById(R.id.input);
        chatInput.setOnTouchListener((v, event) -> {
            View keyboard = view.findViewById(R.id.sticker_keyboard);
            if(keyboard.getVisibility() == View.VISIBLE)
                keyboard.setVisibility(GONE);
            return false;
        });

        stickerKeyboardBtn.setOnClickListener(v -> {
            View keyboard = view.findViewById(R.id.sticker_keyboard);
            if(keyboard.getVisibility() == View.VISIBLE)
                keyboard.setVisibility(GONE);
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
            startActivityForResult(photoPickerIntent, UPLOAD_STICKERS);
        });
        sendBtn.setOnClickListener(v->{
            String msg = chatInput.getText().toString().trim();
            Long time = new Date().getTime();
            //String timestamp = new SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.US).format(time);
            if (!msg.equals("")) {
                ChatMessage data = new ChatMessage(getContext(), msg,MSG, String.valueOf(time), userRole);
                showReply(data);
                reply = -1;
                showReply(null);
                db.getReference(CHAT_LOG).child(String.valueOf(time)).setValue(data);
                chatInput.setText("");
                sendNotification(String.valueOf(time), data);
            }
        });
        folderBtn.setOnClickListener(v-> Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frag, new FolderFrag()).addToBackStack(null).commit());
        cameraBtn.setOnClickListener(v->{
            String[] choices = {"Take a photo", "Open Gallery"};

                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                    mBuilder.setTitle("Choose an item");
                    mBuilder.setSingleChoiceItems(choices, -1, (dialogInterface, i) -> {
                        if(i == 0){
                            // Take Photo
                            if (Objects.requireNonNull(getContext()).checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAM_PERMISSION);
                            }
                            else {
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, TAKE_PIC_AND_SEND);
                            }
                        }
                        else{
                            // Upload Image
                            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, UPLOAD_IMAGE_AND_SEND);
                        }
                        dialogInterface.dismiss();
                    }).setIcon(null);

                    AlertDialog mDialog = mBuilder.create();
                    mDialog.show();
        });
        initTabs(view);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAM_PERMISSION)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getContext(), "Camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, TAKE_PIC_AND_SEND);
            }
            else
            {
                Toast.makeText(getContext(), "Camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void showReply(ChatMessage data) {
        if(reply < 0) {
            replyBox.setVisibility(GONE);
            return;
        }
        replyBox.setVisibility(View.VISIBLE);
        ChatMessage msg = chatList.get(reply);
        if(data != null)
            data.reply = msg;

        ((ImageView)replyBox.findViewById(R.id.sender_img)).setImageResource(msg.username.equals("ahgirl") ? R.drawable.ic_girl : R.drawable.ic_boy);
        if(msg.getType().equals(MSG)){
            replyBox.findViewById(R.id.gif_to_reply).setVisibility(View.GONE);
            TextView tv = replyBox.findViewById(R.id.msg_to_reply);
            tv.setVisibility(View.VISIBLE);

            tv.setText(msg.getMessage());
        }
        else{
            replyBox.findViewById(R.id.msg_to_reply).setVisibility(View.GONE);
            GifImageView gif = replyBox.findViewById(R.id.gif_to_reply);
            gif.setVisibility(View.VISIBLE);

            msg.drawable(gif);
        }

        replyBox.findViewById(R.id.cancel_reply).setOnClickListener(v -> {reply = -1; replyBox.setVisibility(GONE);});
    }

    public static void showImage(Drawable img){
        imagePreview.setVisibility(View.VISIBLE);
        GifImageView giv = imagePreview.findViewById(R.id.img_preview);
        giv.setImageDrawable(img);

        imagePreview.findViewById(R.id.end_img_preview).setOnClickListener(v -> imagePreview.setVisibility(GONE));
    }

    private void sendNotification(String time, ChatMessage data) {
        ChatNotification notif = new ChatNotification(data);
        db.getReference(NOTIFICATIONS + "/" + userId + "/" + time).setValue(notif);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        db.getReference(CHAT_LOG).removeEventListener(chatListener);
        pagerAdapter.removeListener();
    }
    private void initTabs(View view) {
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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == UPLOAD_STICKERS){
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
            else if (requestCode == UPLOAD_IMAGE_AND_SEND){
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
            else if (requestCode == TAKE_PIC_AND_SEND){
                Log.e("Activity intent", "UPLOAD");
                if (data.getExtras().get("data") != null) {
                    Log.e("data", "UPLOAD");
                    uploadPhoto((Bitmap)data.getExtras().get("data"));
                    //uploadImage(file);
                }
            }
        }
    }

    private void uploadSticker(Uri file) {
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
            builder.setMessage("GIF size must be less than 1MB");

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

            UploadTask ut = st.getReference(PHOTOS).child(name).putFile(file);
            ut.addOnFailureListener(exception -> Toast.makeText(getContext(), "Photo not uploaded. :(", Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getContext(), "Photo sent!", Toast.LENGTH_SHORT).show();

                        ChatMessage data = new ChatMessage(getContext(), name, PIC, name, userRole);
                        showReply(data);
                        reply = -1;
                        showReply(null);
                        db.getReference(CHAT_LOG).child(name).setValue(data);
                        chatInput.setText("");
                        sendNotification(name, data);
                    });
        }
    }

    private void uploadPhoto(Bitmap bitmap) {
        String name = String.valueOf(new Date().getTime());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();

        UploadTask ut = st.getReference(PHOTOS).child(name).putBytes(bytes);
        ut.addOnFailureListener(exception -> Toast.makeText(getContext(), "Photo not uploaded. :(", Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(getContext(), "Photo sent!", Toast.LENGTH_SHORT).show();

                    ChatMessage data = new ChatMessage(getContext(), name, PIC, name, userRole);
                    showReply(data);
                    reply = -1;
                    showReply(null);
                    db.getReference(CHAT_LOG).child(name).setValue(data);
                    chatInput.setText("");
                    sendNotification(name, data);
                });
    }
}
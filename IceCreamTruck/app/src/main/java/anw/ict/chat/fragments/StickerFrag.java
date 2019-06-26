package anw.ict.chat.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import anw.ict.R;
import anw.ict.chat.adapter.ChatStickersAdapter;
import anw.ict.chat.objects.ChatMessage;
import anw.ict.chat.objects.ChatNotification;
import anw.ict.chat.objects.ChatSticker;

import static anw.ict.chat.fragments.ChatFrag.chatList;
import static anw.ict.chat.fragments.ChatFrag.reply;
import static anw.ict.chat.fragments.ChatFrag.showReply;
import static anw.ict.utils.Constants.CHAT_LOG;
import static anw.ict.utils.Constants.NOTIFICATIONS;
import static anw.ict.utils.Constants.STICKERS_FOLDER;

public class StickerFrag extends Fragment {
    private String userId, userRole;

    private List<ChatSticker> stickerList = new ArrayList<>();
    private String folderName;

    private ChatStickersAdapter stickerAdapter;

    private FirebaseDatabase db;
    private ValueEventListener stickerListener;

    public StickerFrag(String name) {
        folderName = name;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("StickerFrag", "Created " + folderName);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = sharedPreferences.getString("userId", "");
        userRole = sharedPreferences.getString("userRole", "");

        View rootView = inflater.inflate(R.layout.frag_sticker, container, false);
        stickerListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stickerAdapter.notifyItemRangeRemoved(0, stickerList.size());
                stickerList.clear();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    ChatSticker sticker = new ChatSticker(folderName, getContext(), child.getKey());
                    sticker.setFolder(folderName);
                    stickerList.add(sticker);
                }
                stickerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        db = FirebaseDatabase.getInstance();
        db.getReference("users/" + userId + "/" + STICKERS_FOLDER + "/" + folderName).addValueEventListener(stickerListener);

        ChatStickersAdapter.OnItemClickListener stickerClick = item -> {
            Long time = new Date().getTime();
            //String timestamp = new SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.US).format(time);
            ChatMessage data = new ChatMessage(getContext(), item.getName(), "GIF", String.valueOf(time), userRole);

            if(reply > -1){
                data.reply = chatList.get(reply);
                reply = -1;
                showReply();
            }
            db.getReference(CHAT_LOG).child(String.valueOf(time)).setValue(data);
            sendNotification(String.valueOf(time), data);
        };

        GridLayoutManager glm = new GridLayoutManager(getContext(), 2, RecyclerView.HORIZONTAL, false);
        /* UI Components */
        RecyclerView stickerRV = rootView.findViewById(R.id.rv_recycler_view);
        stickerAdapter = new ChatStickersAdapter(stickerList, stickerClick);
        stickerRV.setAdapter(stickerAdapter);
        stickerRV.setLayoutManager(glm);

        return rootView;
    }

    private void sendNotification(String time, ChatMessage data) {
        ChatNotification notif = new ChatNotification(data);
        db.getReference(NOTIFICATIONS + "/" + userId + "/" + time).setValue(notif);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("StickerFrag", "Destroyed " + folderName);
        stickerAdapter.notifyItemRangeRemoved(0, stickerList.size());
        stickerList.clear();
        db.getReference("users/" + userId + "/" + STICKERS_FOLDER + "/" + folderName).removeEventListener(stickerListener);
    }
}

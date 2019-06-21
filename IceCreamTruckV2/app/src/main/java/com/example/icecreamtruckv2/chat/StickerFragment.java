package com.example.icecreamtruckv2.chat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icecreamtruckv2.R;
import com.example.icecreamtruckv2.utils.Constants;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.icecreamtruckv2.chat.ChatFrag.database;
import static com.example.icecreamtruckv2.chat.ChatFrag.userRole;

public class StickerFragment extends Fragment {
    private List<ChatSticker> stickerList;
    private String folderName;

    /** Database instance **/
    private DatabaseReference chatRoot, notifRoot;

    private ChatStickersAdapter stickerAdapter;
    private RecyclerView stickerRV;

    private DatabaseReference stickerRoot;
    private ChildEventListener stickerListener;

    public StickerFragment() {
        // Required empty public constructor
    }

    StickerFragment(String fn) {
        folderName = fn;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListener();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private void initListener() {
        stickerListener = new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatSticker sticker = new ChatSticker(folderName, getContext(), dataSnapshot.getKey());
                sticker.setFolder(folderName);
                stickerList.add(sticker);
                stickerAdapter.notifyItemInserted(stickerList.size() - 1);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userid = sharedPreferences.getString("userid", "");
        if(stickerList == null)
            stickerList = new ArrayList<>();
        else
            stickerList.clear();

        stickerRoot = database.getReference("users/" + userid + "/" + Constants.STICKERS_FOLDER_DB + "/" + folderName);
        stickerRoot.addChildEventListener(stickerListener);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.frag_sticker, container, false);

        /** Listeners**/
        ChatStickersAdapter.OnItemClickListener stickerClick = item -> {

            ChatMessage data = new ChatMessage();
            data.setContext(getContext());
            data.setMessage(item.getName());
            data.setType("GIF");
            data.setTimestamp(new SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.US).format(new Date().getTime()));
            data.setUsername(userRole);
            notifRoot = database.getReference(userRole.equals("ahgirl") ? Constants.CHAT_BOY : Constants.CHAT_GIRL);
            chatRoot = database.getReference(Constants.CHAT_DB);
            chatRoot.child(String.valueOf(new Date().getTime())).setValue(data);
            notifRoot.child(String.valueOf(new Date().getTime())).setValue(data);
        };

        GridLayoutManager glm = new GridLayoutManager(getContext(), 2, RecyclerView.HORIZONTAL, false);
        /** UI Components **/
        stickerRV = rootView.findViewById(R.id.rv_recycler_view);
        stickerAdapter = new ChatStickersAdapter(stickerList, stickerClick);
        stickerRV.setAdapter(stickerAdapter);
        stickerRV.setLayoutManager(glm);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stickerRoot.removeEventListener(stickerListener);
    }
}
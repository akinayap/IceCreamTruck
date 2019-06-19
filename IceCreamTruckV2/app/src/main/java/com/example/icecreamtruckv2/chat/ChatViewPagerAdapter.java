package com.example.icecreamtruckv2.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.icecreamtruckv2.R;
import com.example.icecreamtruckv2.utils.Constants;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

import static com.example.icecreamtruckv2.chat.ChatFrag.database;
import static com.example.icecreamtruckv2.chat.ChatFrag.tabLayout;

public class ChatViewPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private List<String> folderNames = new ArrayList<>();
    private List<ChatSticker> folderIcons = new ArrayList<>();

    private ChildEventListener folderListener;

    ChatViewPagerAdapter(FragmentManager fm, Context c) {
        super(fm);
        context = c;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userid = sharedPreferences.getString("userid", "");

        initListener();
        DatabaseReference folderRoot = database.getReference("users/" + userid + "/" + Constants.STICKERS_FOLDER_DB);
        folderRoot.addChildEventListener(folderListener);
    }

    private void initListener(){
        folderListener = new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                folderNames.add(dataSnapshot.getKey());

                String filename = dataSnapshot.getChildren().iterator().next().getKey();
                ChatSticker cs = new ChatSticker(dataSnapshot.getKey(), context, filename);
                folderIcons.add(cs);
                notifyDataSetChanged();
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
    public int getCount() {
        return folderNames.size();
    }

    @Override
    public Fragment getItem(int position) {
        return new StickerFragment(folderNames.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return folderNames.get(position);
    }

    private View getTabView(int position) {
        View tabs = LayoutInflater.from(context).inflate(R.layout.tab, null);
        GifImageView giv = tabs.findViewById(R.id.gif_title);
        giv.setImageResource(R.drawable.loading);
        folderIcons.get(position).setDrawable(giv);
        return tabs;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(getTabView(i));
        }
    }

}
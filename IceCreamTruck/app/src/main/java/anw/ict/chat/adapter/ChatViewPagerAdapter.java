package anw.ict.chat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import anw.ict.R;
import anw.ict.chat.fragments.StickerFrag;
import anw.ict.chat.objects.ChatSticker;
import pl.droidsonroids.gif.GifImageView;

import static anw.ict.chat.fragments.ChatFrag.tabLayout;
import static anw.ict.utils.Constants.STICKERS_FOLDER;

public class ChatViewPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private List<String> folderNames = new ArrayList<>();
    private List<ChatSticker> folderIcons = new ArrayList<>();
    private String userId;

    private ValueEventListener folderListener;

    public ChatViewPagerAdapter(FragmentManager fm, Context c) {
        super(fm);
        Log.e("ChatViewPagerAdapter", "Initialized");
        context = c;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        userId = sharedPreferences.getString("userId", "");

        folderListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    folderNames.add(child.getKey());

                    String filename = child.getChildren().iterator().next().getKey();
                    ChatSticker cs = new ChatSticker(dataSnapshot.getKey(), context, filename);
                    folderIcons.add(cs);
                }
                notifyDataSetChanged();
                FirebaseDatabase.getInstance().getReference("users/" + userId + "/" + STICKERS_FOLDER).removeEventListener(folderListener);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        FirebaseDatabase.getInstance().getReference("users/" + userId + "/" + STICKERS_FOLDER).addValueEventListener(folderListener);
    }

    @Override
    public Fragment getItem(int position) {
        return new StickerFrag(folderNames.get(position));
    }

    @Override
    public int getCount() {
        return folderNames.size();
    }
    private View getTabView(int position) {
        @SuppressLint("InflateParams") View tabs = LayoutInflater.from(context).inflate(R.layout.tab, null);
        GifImageView giv = tabs.findViewById(R.id.gif_title);
        TextView tv = tabs.findViewById(R.id.folder_title);
        giv.setImageResource(R.drawable.ic_login_load);
        giv.setImageDrawable(folderIcons.get(position).drawable());
        tv.setText(folderNames.get(position));
        return tabs;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            Objects.requireNonNull(tab).setCustomView(getTabView(i));
        }
    }

    public void removeListener() {
        FirebaseDatabase.getInstance().getReference("users/" + userId + "/" + STICKERS_FOLDER).removeEventListener(folderListener);
    }
}

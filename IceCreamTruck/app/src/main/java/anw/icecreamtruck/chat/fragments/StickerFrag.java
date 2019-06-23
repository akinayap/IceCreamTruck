package anw.icecreamtruck.chat.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import anw.icecreamtruck.R;
import anw.icecreamtruck.utils.FragmentLifecycle;

public class StickerFrag extends Fragment implements FragmentLifecycle {
    private String folderName;
    private String userId;

    public StickerFrag(String name) {
        folderName = name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("StickerFrag", "Created " + folderName);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String userId = sharedPreferences.getString("userId", "");
        View rootView = inflater.inflate(R.layout.frag_sticker, container, false);
        return rootView;
    }

    @Override
    public void onPauseFragment() {
        Log.e("StickerFrag", "Paused");
    }

    @Override
    public void onResumeFragment() {
        Log.e("StickerFrag", "Resumed");

    }
}

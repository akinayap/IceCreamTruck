package com.example.icecreamtruckv2.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.icecreamtruckv2.R;
import com.example.icecreamtruckv2.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static com.example.icecreamtruckv2.chat.ChatFrag.userRole;

public class ChatLogAdapter extends RecyclerView.Adapter<ChatLogAdapter.ChatViewHolder> {
    private List<ChatMessage> mData;
    public ChatLogAdapter(List<ChatMessage> msg) {
        mData = msg;
    }
    public void clearData() {
        mData.clear();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat, parent, false);
        return new ChatViewHolder(root);
    }

    @Override
    public void onBindViewHolder(final ChatViewHolder holder, int position) {
        ChatMessage data = mData.get(position);
        int micon = userRole.equals("ahgirl") ? R.drawable.girl : R.drawable.boy;
        int oicon = userRole.equals("ahgirl") ? R.drawable.boy : R.drawable.girl;
        if(data.getUsername().equals("ahgirl") && userRole.equals("ahgirl") ||
        data.getUsername().equals("ahboy") && userRole.equals("ahboy"))
        {
            holder.mcard.setVisibility(View.VISIBLE);
            holder.mgif.setVisibility(View.GONE);
            holder.mmessage.setVisibility(View.GONE);
            holder.ogif.setVisibility(View.GONE);
            holder.omessage.setVisibility(View.GONE);
            holder.ocard.setVisibility(View.GONE);
            holder.micon.setImageResource(micon);
            holder.mtime.setText(data.getTimestamp());

            if (data.getType().equals("GIF")) {
                holder.mgif.setVisibility(View.VISIBLE);
                holder.mmessage.setVisibility(View.GONE);
                holder.mgif.setImageResource(R.drawable.loading);
                data.setDrawable(holder, holder.mgif);
            } else {
                if (!data.getMessage().equals("")) {
                    holder.mmessage.setVisibility(View.VISIBLE);
                    holder.mgif.setVisibility(View.GONE);
                    holder.mmessage.setText(data.getMessage());
                }
            }
        } else {
            holder.ocard.setVisibility(View.VISIBLE);
            holder.mgif.setVisibility(View.GONE);
            holder.mmessage.setVisibility(View.GONE);
            holder.ogif.setVisibility(View.GONE);
            holder.omessage.setVisibility(View.GONE);
            holder.mcard.setVisibility(View.GONE);
            holder.oicon.setImageResource(oicon);
            holder.otime.setText(data.getTimestamp());

            if (data.getType().equals("GIF")) {
                holder.ogif.setVisibility(View.VISIBLE);
                holder.omessage.setVisibility(View.GONE);
                holder.ogif.setImageResource(R.drawable.loading);
                data.setDrawable(holder, holder.ogif);

            } else {
                if (!data.getMessage().equals("")) {
                    holder.omessage.setVisibility(View.VISIBLE);
                    holder.ogif.setVisibility(View.GONE);
                    holder.omessage.setText(data.getMessage());
                }
            }
        }
    }

    static final class ChatViewHolder extends RecyclerView.ViewHolder {
        CardView mcard;
        ImageView micon;
        TextView mmessage;
        TextView mtime;
        GifImageView mgif;

        CardView ocard;
        ImageView oicon;
        TextView omessage;
        TextView otime;
        GifImageView ogif;

        ChatViewHolder(View view) {
            super(view);

            ocard = view.findViewById(R.id.other_view);
            oicon = view.findViewById(R.id.other_icon);
            omessage = view.findViewById(R.id.other_message);
            ogif = view.findViewById(R.id.other_gif);
            otime = view.findViewById(R.id.other_time);

            mcard = view.findViewById(R.id.my_view);
            micon = view.findViewById(R.id.my_icon);
            mmessage = view.findViewById(R.id.my_message);
            mgif = view.findViewById(R.id.my_gif);
            mtime = view.findViewById(R.id.my_time);
        }
    }
}
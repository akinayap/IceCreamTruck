package com.example.icecreamtruckv2.chat;

import android.content.Context;
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

public class ChatLogAdapter extends RecyclerView.Adapter<ChatLogAdapter.ChatViewHolder> {

    private List<ChatMessage> mData = new ArrayList<>();

    public ChatLogAdapter(List<ChatMessage> msg) {
        mData = msg;
    }
    public void clearData() {
        mData.clear();
    }

    public void addData(ChatMessage data) {
        mData.add(data);
    }

    // total number of rows
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
        if(data.getName().equals("Ah Girl") && ChatFrag.userRole.equals("ahgirl") ||
        data.getName().equals("Ah Boy") && ChatFrag.userRole.equals("ahboy"))
        {
            holder.ocard.setVisibility(View.GONE);
            holder.mcard.setVisibility(View.VISIBLE);

            holder.mname.setText(data.getName());
            holder.micon.setImageResource(data.getIcon());

            if (data.getType().equals("GIF")) {
                holder.mgif.setVisibility(View.VISIBLE);
                holder.mmessage.setVisibility(View.GONE);
                holder.mgif.setBackgroundResource(R.drawable.loading);
                data.setDrawable(holder, holder.mgif);

            } else {
                if (!data.getMessage().equals("")) {
                    holder.mmessage.setVisibility(View.VISIBLE);
                    holder.mgif.setVisibility(View.GONE);
                    holder.mmessage.setText(data.getMessage());
                }
            }
        }
        else
        {
            holder.mcard.setVisibility(View.GONE);
            holder.ocard.setVisibility(View.VISIBLE);

            holder.oname.setText(data.getName());
            holder.oicon.setImageResource(data.getIcon());

            if (data.getType().equals("GIF")) {
                holder.ogif.setVisibility(View.VISIBLE);
                holder.omessage.setVisibility(View.GONE);
                holder.ogif.setBackgroundResource(R.drawable.loading);
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

    public void removeData(int id) {
        mData.remove(id);
    }

    static final class ChatViewHolder extends RecyclerView.ViewHolder {
        CardView mcard;
        ImageView micon;
        TextView mname;
        TextView mmessage;
        GifImageView mgif;

        CardView ocard;
        ImageView oicon;
        TextView oname;
        TextView omessage;
        GifImageView ogif;

        ChatViewHolder(View view) {
            super(view);

            ocard = view.findViewById(R.id.other_view);
            oicon = view.findViewById(R.id.other_icon);
            oname = view.findViewById(R.id.other_username);
            omessage = view.findViewById(R.id.other_message);
            ogif = view.findViewById(R.id.other_gif);

            mcard = view.findViewById(R.id.my_view);
            micon = view.findViewById(R.id.my_icon);
            mname = view.findViewById(R.id.my_username);
            mmessage = view.findViewById(R.id.my_message);
            mgif = view.findViewById(R.id.my_gif);
        }
    }
}


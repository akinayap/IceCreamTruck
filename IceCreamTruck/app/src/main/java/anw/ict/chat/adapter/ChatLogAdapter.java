package anw.ict.chat.adapter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import anw.ict.R;
import anw.ict.chat.objects.ChatMessage;
import pl.droidsonroids.gif.GifImageView;

public class ChatLogAdapter extends RecyclerView.Adapter<ChatLogAdapter.ChatViewHolder> {
    private String userRole;
    private List<ChatMessage> mData;
    public ChatLogAdapter(List<ChatMessage> msg) {
        mData = msg;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
        userRole = sharedPreferences.getString("userRole", "null");

        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat, parent, false);
        return new ChatViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatViewHolder holder, int position) {
        ChatMessage data = mData.get(position);
        if(data.getUsername().equals(userRole)){
            holder.oCard.setVisibility(View.GONE);
            holder.mCard.setVisibility(View.VISIBLE);
            holder.mTime.setText(data.getTimestamp());

            if (data.getType().equals("GIF")) {
                holder.mGif.setVisibility(View.VISIBLE);
                holder.mMessage.setVisibility(View.GONE);
                holder.mGif.setImageDrawable(data.drawable());
            } else if (!data.getMessage().equals("")) {
                holder.mMessage.setVisibility(View.VISIBLE);
                holder.mGif.setVisibility(View.GONE);
                holder.mMessage.setText(data.getMessage());
            }
        } else {
            holder.mCard.setVisibility(View.GONE);
            holder.oCard.setVisibility(View.VISIBLE);
            holder.oTime.setText(data.getTimestamp());

            if (data.getType().equals("GIF")) {
                holder.oGif.setVisibility(View.VISIBLE);
                holder.oMessage.setVisibility(View.GONE);
                holder.oGif.setImageDrawable(data.drawable());
            } else if (!data.getMessage().equals("")) {
                holder.oMessage.setVisibility(View.VISIBLE);
                holder.oGif.setVisibility(View.GONE);
                holder.oMessage.setText(data.getMessage());
            }
        }
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        CardView mCard;
        ImageView mIcon;
        TextView mMessage;
        TextView mTime;
        GifImageView mGif;

        CardView oCard;
        ImageView oIcon;
        TextView oMessage;
        TextView oTime;
        GifImageView oGif;

        ChatViewHolder(View view) {
            super(view);
            oCard = view.findViewById(R.id.other_view);
            oMessage = view.findViewById(R.id.other_message);
            oGif = view.findViewById(R.id.other_gif);
            oTime = view.findViewById(R.id.other_time);

            mCard = view.findViewById(R.id.my_view);
            mMessage = view.findViewById(R.id.my_message);
            mGif = view.findViewById(R.id.my_gif);
            mTime = view.findViewById(R.id.my_time);

            oIcon = view.findViewById(R.id.other_icon);
            mIcon = view.findViewById(R.id.my_icon);
            int mImg, oImg;
            if(userRole.equals("ahgirl")) {
                mImg = R.drawable.ic_girl;
                oImg = R.drawable.ic_boy;
            }
            else {
                mImg = R.drawable.ic_boy;
                oImg = R.drawable.ic_girl;
            }
            oIcon.setImageResource(oImg);
            mIcon.setImageResource(mImg);
        }
    }
}
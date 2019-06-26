package anw.ict.chat.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import anw.ict.R;
import anw.ict.chat.objects.ChatMessage;
import pl.droidsonroids.gif.GifImageView;

import static anw.ict.chat.fragments.ChatFrag.chatList;
import static anw.ict.chat.fragments.ChatFrag.chatRV;

public class ChatLogAdapter extends RecyclerView.Adapter<ChatLogAdapter.ChatViewHolder> {
    private String userRole;
    private List<ChatMessage> mData;
    public ChatLogAdapter(List<ChatMessage> msg) {
        mData = msg;
    }
    private Context c;

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
        userRole = sharedPreferences.getString("userRole", "null");
        c = parent.getContext();
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat, parent, false);
        return new ChatViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatViewHolder holder, int position) {
        ChatMessage data = mData.get(position);
        String date = new SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.US).format(Long.parseLong(data.getTimestamp()));
        //String date = data.getTimestamp();
        if(data.getUsername().equals(userRole)){
            holder.oCard.setVisibility(View.GONE);
            holder.mCard.setVisibility(View.VISIBLE);
            holder.mTime.setText(date);

            if (data.getType().equals("GIF")) {
                holder.mGif.setVisibility(View.VISIBLE);
                holder.mMessage.setVisibility(View.GONE);
                holder.mGif.setImageDrawable(data.drawable());
            } else if (!data.getMessage().equals("")) {
                holder.mMessage.setVisibility(View.VISIBLE);
                holder.mGif.setVisibility(View.GONE);
                holder.mMessage.setText(data.getMessage());
            }
            if(data.reply != null){
                holder.oReply.setVisibility(View.GONE);
                holder.mReply.setVisibility(View.VISIBLE);
                ImageView userIcon = (ImageView) holder.mReply.getViewById(R.id.my_sender_img);
                TextView message = (TextView) holder.mReply.getViewById(R.id.my_msg_to_reply);
                GifImageView gifMessage = (GifImageView) holder.mReply.getViewById(R.id.my_gif_to_reply);

                userIcon.setImageResource(data.reply.username.equals("ahgirl") ? R.drawable.ic_girl : R.drawable.ic_boy);
                if(data.reply.getType().equals("GIF")){
                    message.setVisibility(View.GONE);
                    gifMessage.setVisibility(View.VISIBLE);
                    gifMessage.setImageDrawable(data.reply.drawable());
                } else if (!data.reply.getMessage().equals("")){
                    gifMessage.setVisibility(View.GONE);
                    message.setVisibility(View.VISIBLE);
                    message.setText(data.reply.getMessage());
                }
                holder.mReply.setOnClickListener(v -> chatList.stream().filter(m -> m.getTimestamp().equals(data.reply.getTimestamp())).findAny().ifPresent(cm -> chatRV.smoothScrollToPosition(chatList.indexOf(cm))));
            } else {
                holder.mReply.setVisibility(View.GONE);
                holder.oReply.setVisibility(View.GONE);
            }
        } else {
            holder.mCard.setVisibility(View.GONE);
            holder.oCard.setVisibility(View.VISIBLE);
            holder.oTime.setText(date);

            if (data.getType().equals("GIF")) {
                holder.oGif.setVisibility(View.VISIBLE);
                holder.oMessage.setVisibility(View.GONE);
                holder.oGif.setImageDrawable(data.drawable());
            } else if (!data.getMessage().equals("")) {
                holder.oMessage.setVisibility(View.VISIBLE);
                holder.oGif.setVisibility(View.GONE);
                holder.oMessage.setText(data.getMessage());
            }

            if(data.reply != null){
                holder.oReply.setVisibility(View.VISIBLE);
                holder.mReply.setVisibility(View.GONE);
                ImageView userIcon = (ImageView) holder.oReply.getViewById(R.id.other_sender_img);
                TextView message = (TextView) holder.oReply.getViewById(R.id.other_msg_to_reply);
                GifImageView gifMessage = (GifImageView) holder.oReply.getViewById(R.id.other_gif_to_reply);

                userIcon.setImageResource(data.reply.username.equals("ahgirl") ? R.drawable.ic_girl : R.drawable.ic_boy);
                if(data.reply.getType().equals("GIF")){
                    message.setVisibility(View.GONE);
                    gifMessage.setVisibility(View.VISIBLE);
                    gifMessage.setImageDrawable(data.reply.drawable());
                } else if (!data.reply.getMessage().equals("")){
                    gifMessage.setVisibility(View.GONE);
                    message.setVisibility(View.VISIBLE);
                    message.setText(data.reply.getMessage());
                }
            } else {
                holder.mReply.setVisibility(View.GONE);
                holder.oReply.setVisibility(View.GONE);
            }
        }
    }

    public Context getContext() {
        return c;
    }

    public ChatMessage getItem(int position) {
        return mData.get(position);
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        CardView mCard;
        ImageView mIcon;
        TextView mMessage;
        TextView mTime;
        GifImageView mGif;
        ConstraintLayout mReply;

        CardView oCard;
        ImageView oIcon;
        TextView oMessage;
        TextView oTime;
        GifImageView oGif;
        ConstraintLayout oReply;

        ChatViewHolder(View view) {
            super(view);
            oCard = view.findViewById(R.id.other_view);
            oMessage = view.findViewById(R.id.other_message);
            oGif = view.findViewById(R.id.other_gif);
            oTime = view.findViewById(R.id.other_time);
            oReply = view.findViewById(R.id.other_reply);

            mCard = view.findViewById(R.id.my_view);
            mMessage = view.findViewById(R.id.my_message);
            mGif = view.findViewById(R.id.my_gif);
            mTime = view.findViewById(R.id.my_time);
            mReply = view.findViewById(R.id.my_reply);

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

        public CardView getMCard() {
            return mCard;
        }
        public CardView getOCard() {
            return oCard;
        }
    }
}
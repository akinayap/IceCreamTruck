package anw.ict.chat.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import static anw.ict.chat.fragments.ChatFrag.chatRV;
import static anw.ict.utils.Constants.GIF;
import static anw.ict.utils.Constants.PIC;

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

            if(data.startBlink){
                blink(holder.mCard);
                data.startBlink = false;
            }

            if (data.getType().equals(GIF) || data.getType().equals(PIC)) {
                holder.mGif.setVisibility(View.VISIBLE);
                holder.mMessage.setVisibility(View.GONE);
                if(data.getType().equals(PIC))
                    data.photo(holder.mGif);
                else
                    data.drawable(holder.mGif);
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
                if(data.reply.getType().equals(GIF) || data.reply.getType().equals(PIC)){
                    message.setVisibility(View.GONE);
                    gifMessage.setVisibility(View.VISIBLE);
                    if(data.reply.getType().equals(PIC))
                        data.reply.photo(gifMessage);
                    else
                        data.reply.drawable(gifMessage);
                } else if (!data.reply.getMessage().equals("")){
                    gifMessage.setVisibility(View.GONE);
                    message.setVisibility(View.VISIBLE);
                    message.setText(data.reply.getMessage());
                }
                holder.mReply.setOnClickListener(v -> {
                    ChatMessage cm = mData.stream().filter(m -> m.getTimestamp().equals(data.reply.getTimestamp())).findAny().orElse(null);
                    if(mData.indexOf(cm) < 0) {
                        Toast.makeText(getContext(), "Message was deleted.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        chatRV.smoothScrollToPosition(mData.indexOf(cm));
                        if(cm != null){
                            cm.startBlink = true;
                            notifyItemChanged(mData.indexOf(cm));
                        }
                    }
                });
            } else {
                holder.mReply.setVisibility(View.GONE);
                holder.oReply.setVisibility(View.GONE);
            }
        } else {
            holder.mCard.setVisibility(View.GONE);
            holder.oCard.setVisibility(View.VISIBLE);
            holder.oTime.setText(date);

            if(data.startBlink){
                blink(holder.oCard);
                data.startBlink = false;
            }

            if (data.getType().equals(GIF) || data.getType().equals(PIC)) {
                holder.oGif.setVisibility(View.VISIBLE);
                holder.oMessage.setVisibility(View.GONE);
                if(data.getType().equals(PIC))
                    data.photo(holder.oGif);
                else
                    data.drawable(holder.oGif);
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
                if(data.reply.getType().equals(GIF) || data.reply.getType().equals(PIC)){
                    message.setVisibility(View.GONE);
                    gifMessage.setVisibility(View.VISIBLE);
                    if(data.reply.getType().equals(PIC))
                        data.reply.photo(gifMessage);
                    else
                       data.reply.drawable(gifMessage);
                } else if (!data.reply.getMessage().equals("")){
                    gifMessage.setVisibility(View.GONE);
                    message.setVisibility(View.VISIBLE);
                    message.setText(data.reply.getMessage());
                }
                holder.oReply.setOnClickListener(v -> {
                    ChatMessage cm = mData.stream().filter(m -> m.getTimestamp().equals(data.reply.getTimestamp())).findAny().orElse(null);
                    if(mData.indexOf(cm) < 0) {
                        Toast.makeText(getContext(), "Message was deleted.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        chatRV.smoothScrollToPosition(mData.indexOf(cm));
                        if(cm != null){
                            cm.startBlink = true;
                            notifyItemChanged(mData.indexOf(cm));
                        }
                    }
                });
            } else {
                holder.mReply.setVisibility(View.GONE);
                holder.oReply.setVisibility(View.GONE);
            }

        }
    }

    private void blink(View view) {
        Drawable buttonDrawable = view.getBackground();
        ObjectAnimator anim = ObjectAnimator.ofArgb(view, "backgroundColor", 0, R.color.colorPrimaryDark, R.color.colorPrimaryDark, R.color.colorPrimaryDark, 0);
        //ObjectAnimator anim = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.5f, 0.5f, 0.5f, 0.5f, 1.0f);
        anim.setDuration(1000);
        anim.start();
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setBackground(buttonDrawable);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.setBackground(buttonDrawable);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public Context getContext() {
        return c;
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
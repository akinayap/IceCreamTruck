package anw.ict.chat.callback;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import anw.ict.R;
import anw.ict.chat.adapter.ChatLogAdapter;
import anw.ict.chat.fragments.ChatFrag;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;
import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

public class MessageSwipeCallback extends ItemTouchHelper.Callback {
    private boolean swipeBack = false;
    private static final float buttonWidth = 100;
    private ChatLogAdapter mAdapter;

    public MessageSwipeCallback(ChatLogAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ACTION_STATE_SWIPE) {
            float iconSize = buttonWidth - 20;

            View itemView = viewHolder.itemView;
            CardView card = ((ChatLogAdapter.ChatViewHolder) viewHolder).getOCard();
            if (((ChatLogAdapter.ChatViewHolder) viewHolder).getMCard().getVisibility() == View.VISIBLE)
                card = ((ChatLogAdapter.ChatViewHolder) viewHolder).getMCard();

            float diff = itemView.getBottom() - itemView.getTop();
            float offset = (diff - iconSize) / 2;
            if (dX > buttonWidth) {
                dX = buttonWidth;
                Drawable iconLeft = ContextCompat.getDrawable(mAdapter.getContext(), R.drawable.ic_reply);
                Objects.requireNonNull(iconLeft).setBounds(card.getLeft(), (int) (itemView.getTop() + offset), (int) (card.getLeft() + iconSize), (int) (itemView.getBottom() - offset));
                iconLeft.draw(c);
            }

/*            if (dX < -buttonWidth) {
                dX = -buttonWidth;
                Drawable iconRight = ContextCompat.getDrawable(mAdapter.getContext(), R.drawable.ic_delete);
                Objects.requireNonNull(iconRight).setBounds((int) (card.getRight() - iconSize), (int) (itemView.getTop() + offset), card.getRight(), (int) (itemView.getBottom() - offset));
                iconRight.draw(c);
            }*/
            setTouchListener(recyclerView, viewHolder, dX);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  float dX) {

        recyclerView.setOnTouchListener((v, event) -> {
            swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
            if (swipeBack) {

                if(viewHolder.getAdapterPosition() < 0)
                    return false;

                if (dX >= buttonWidth) {
                    ChatFrag.reply = viewHolder.getAdapterPosition();
                    ChatFrag.showReply(null);
                }
/*                if (dX <= -buttonWidth) {
                    FirebaseDatabase.getInstance().getReference(CHAT_LOG + "/" + mAdapter.getItem(viewHolder.getAdapterPosition()).getTimestamp()).removeValue();
                }*/
            }
            return false;
        });
    }
}
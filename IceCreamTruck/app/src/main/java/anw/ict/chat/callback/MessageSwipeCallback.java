package anw.ict.chat.callback;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import anw.ict.R;
import anw.ict.chat.adapter.ChatLogAdapter;
import anw.ict.chat.fragments.ChatFrag;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;
import static anw.ict.utils.Constants.CHAT_LOG;

enum ButtonsState {
    GONE,
    LEFT_VISIBLE,
    RIGHT_VISIBLE
}

public class MessageSwipeCallback extends ItemTouchHelper.Callback {
    private boolean swipeBack = false;
    private ButtonsState buttonShowedState = ButtonsState.GONE;
    private Drawable buttonInstance = null;
    private RecyclerView.ViewHolder currentItemViewHolder = null;
    private MessageSwipeActions buttonsActions;
    private static final float buttonWidth = 100;
    private ChatLogAdapter mAdapter;

    public MessageSwipeCallback(ChatLogAdapter adapter) {
        buttonsActions = new MessageSwipeActions();
        mAdapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
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
            swipeBack = buttonShowedState != ButtonsState.GONE;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ACTION_STATE_SWIPE) {
            if (buttonShowedState != ButtonsState.GONE) {
                if (buttonShowedState == ButtonsState.LEFT_VISIBLE) dX = Math.max(dX, buttonWidth);
                if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) dX = Math.min(dX, -buttonWidth);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
            else {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        if (buttonShowedState == ButtonsState.GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        currentItemViewHolder = viewHolder;
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener(final Canvas c,
                                  final RecyclerView recyclerView,
                                  final RecyclerView.ViewHolder viewHolder,
                                  final float dX, final float dY,
                                  final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener((v, event) -> {
            Log.e("MessageSwipeCallback", "setTouchListener");
            swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
            if (swipeBack) {
                if (dX < -buttonWidth) buttonShowedState = ButtonsState.RIGHT_VISIBLE;
                else if (dX > buttonWidth) buttonShowedState  = ButtonsState.LEFT_VISIBLE;

                if (buttonShowedState != ButtonsState.GONE) {
                    setTouchDownListener(c, recyclerView, viewHolder, dY, actionState, isCurrentlyActive);
                    setItemsClickable(recyclerView, false);
                }
            }
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchDownListener(final Canvas c,
                                      final RecyclerView recyclerView,
                                      final RecyclerView.ViewHolder viewHolder,
                                      final float dY,
                                      final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener((v, event) -> {
            Log.e("MessageSwipeCallback", "setTouchDownListener");
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.e("MessageSwipeCallback", "ACTION_DOWN");
                setTouchUpListener(c, recyclerView, viewHolder, dY, actionState, isCurrentlyActive);
            }
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchUpListener(final Canvas c,
                                    final RecyclerView recyclerView,
                                    final RecyclerView.ViewHolder viewHolder,
                                    final float dY,
                                    final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener((v, event) -> {
            Log.e("MessageSwipeCallback", "setTouchUpListener");
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.e("MessageSwipeCallback", "ACTION_UP");
                MessageSwipeCallback.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                recyclerView.setOnTouchListener((v1, event1) -> false);
                setItemsClickable(recyclerView, true);
                swipeBack = false;

                if (buttonsActions != null && buttonInstance != null && buttonInstance.getBounds().contains((int)event.getX(), (int)event.getY())) {
                    Log.e("MessageSwipeCallback", "checkevents");
                    if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
                        Log.e("MessageSwipeCallback", "leftClick");
                        buttonsActions.onLeftClicked(viewHolder.getAdapterPosition());
                    }
                    else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                        Log.e("MessageSwipeCallback", "rightClick");
                        buttonsActions.onRightClicked(viewHolder.getAdapterPosition());
                    }
                }
                buttonShowedState = ButtonsState.GONE;
                currentItemViewHolder = null;
            }
            return false;
        });
    }

    private void setItemsClickable(RecyclerView recyclerView,
                                   boolean isClickable) {
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }


    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
        float iconSize = buttonWidth - 20;

        View itemView = viewHolder.itemView;
        CardView card = ((ChatLogAdapter.ChatViewHolder)viewHolder).getOCard();
        if(((ChatLogAdapter.ChatViewHolder)viewHolder).getMCard().getVisibility() == View.VISIBLE)
            card = ((ChatLogAdapter.ChatViewHolder)viewHolder).getMCard();

        float diff = itemView.getBottom() - itemView.getTop();
        float offset = (diff - iconSize)/2;

        if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
            Drawable iconLeft = ContextCompat.getDrawable(mAdapter.getContext(), R.drawable.ic_reply);
            Objects.requireNonNull(iconLeft).setBounds(card.getLeft(), (int)(itemView.getTop() + offset), (int)(card.getLeft() + iconSize), (int)(itemView.getBottom() - offset));
            iconLeft.draw(c);
            buttonInstance = iconLeft;
        }
        else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
            Drawable iconRight = ContextCompat.getDrawable(mAdapter.getContext(), R.drawable.ic_delete);
            Objects.requireNonNull(iconRight).setBounds((int)(card.getRight() - iconSize), (int)(itemView.getTop() + offset), card.getRight(), (int)(itemView.getBottom() - offset));
            iconRight.draw(c);
            buttonInstance = iconRight;
        }
        else
        {
            buttonInstance = null;
        }
    }

    public void onDraw(Canvas c) {
        if (currentItemViewHolder != null) {
            drawButtons(c, currentItemViewHolder);
        }
    }

    private class MessageSwipeActions {
        void onLeftClicked(int position) {
            Log.e("Left Tapped", String.valueOf(position));
            ChatFrag.reply = position;
            ChatFrag.showReply();
        }
        void onRightClicked(int position) {
            Log.e("Right Tapped", String.valueOf(position));
            FirebaseDatabase.getInstance().getReference(CHAT_LOG + "/" + mAdapter.getItem(position).getTimestamp()).removeValue();
        }

    }
}

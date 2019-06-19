package com.example.icecreamtruckv2.chat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class FolderMoveCallback extends ItemTouchHelper.Callback {

    private final FolderTouchHelperContract mAdapter;

    public FolderMoveCallback(FolderTouchHelperContract adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {}

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        mAdapter.onRowMoved(recyclerView, viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
                                  int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof ChatFolderAdapter.ChatFolderViewHolder) {
                ChatFolderAdapter.ChatFolderViewHolder myViewHolder = (ChatFolderAdapter.ChatFolderViewHolder) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }
        }

        super.onSelectedChanged(viewHolder, actionState);
    }
    @Override
    public void clearView(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof ChatFolderAdapter.ChatFolderViewHolder) {
            ChatFolderAdapter.ChatFolderViewHolder myViewHolder=
                    (ChatFolderAdapter.ChatFolderViewHolder) viewHolder;
            mAdapter.onRowClear(myViewHolder, recyclerView);
        }
    }

    public interface FolderTouchHelperContract {

        void onRowMoved(RecyclerView recyclerView, int fromPosition, int toPosition);
        void onRowSelected(ChatFolderAdapter.ChatFolderViewHolder myViewHolder);
        void onRowClear(ChatFolderAdapter.ChatFolderViewHolder myViewHolder, RecyclerView recyclerView);

    }

}
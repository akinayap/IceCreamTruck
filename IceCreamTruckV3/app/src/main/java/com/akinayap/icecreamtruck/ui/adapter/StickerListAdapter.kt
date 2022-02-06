package com.akinayap.icecreamtruck.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.akinayap.icecreamtruck.databinding.ItemStickerBinding

class StickerListAdapter(private val context: Context) :
    BaseAdapter<Long, StickerListAdapter.StickerListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerListViewHolder {
        return StickerListViewHolder(
            ItemStickerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    inner class StickerListViewHolder(
        private val viewBinding: ItemStickerBinding
    ) : ViewHolderWrapper<Long>(viewBinding.root) {
        /**
         * Sets data information into view binding UI
         * @param itemData item to be shown
         * @param position item position in list of recyclerview
         */
        override fun setItemData(itemData: Long, position: Int) {

        }
    }

}
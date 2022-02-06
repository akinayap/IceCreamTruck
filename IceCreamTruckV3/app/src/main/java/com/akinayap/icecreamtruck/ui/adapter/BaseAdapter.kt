package com.akinayap.icecreamtruck.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Wrapper class to add setItemData function for RecyclerView.ViewHolder
 *
 * @param ItemType Type of item in RecyclerView list
 * @param itemView Root of viewBinding
 */
abstract class ViewHolderWrapper<ItemType>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    /**
     * Sets data information into view binding UI
     * @param itemData item to be shown
     * @param position item position in list of recyclerview
     */
    abstract fun setItemData(itemData:ItemType, position: Int)
}

/**
 * Base Adapter for Recyclerview to avoid boiler plate code
 *
 * @param ItemType Type of item in the recyclerview list
 * @param ViewHolder Inherits ViewHolderWrapper
 */
abstract class BaseAdapter<ItemType, ViewHolder: ViewHolderWrapper<ItemType>> : RecyclerView.Adapter<ViewHolder>() {
    private var itemList = ArrayList<ItemType>()

    override fun getItemCount(): Int {
        return itemList.size
    }

    open fun addToList(itemNameList: ArrayList<ItemType>){
        this.itemList.addAll(itemNameList)
        this.notifyDataSetChanged()
    }

    open fun setItemList(itemNameList: ArrayList<ItemType>) {
        this.itemList = itemNameList
        this.notifyDataSetChanged()
    }

    fun getItemList() : ArrayList<ItemType>{
        return itemList
    }

    override fun onBindViewHolder(holderItem: ViewHolder, position: Int) {
        holderItem.setItemData(itemList[position], position)
    }
}
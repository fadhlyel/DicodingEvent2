package com.example.dicodingevent.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.dicodingevent.data.local.entity.Event

class FavoriteDiffCallback(
    private val oldList: List<Event>,
    private val newList: List<Event>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
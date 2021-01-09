package com.pankaj.scrollwithheader

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class Adapter(val data: ArrayList<CustomData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val HEADER = 1
    val ITEM = 2
    override fun getItemViewType(position: Int): Int {
        return takeIf { data[position].type == "head" }?.HEADER ?: ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == HEADER) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.header_layout, parent, false)
            return HeaderViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
            return ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.onBind(data[position])
        } else if (holder is ItemViewHolder)
            holder.onBind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
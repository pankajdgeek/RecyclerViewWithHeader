package com.pankaj.scrollwithheader

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun onBind(customData: CustomData) {
        val txt = view.findViewById<TextView>(R.id.txt_item)
        txt.setText(customData.value)
    }
}
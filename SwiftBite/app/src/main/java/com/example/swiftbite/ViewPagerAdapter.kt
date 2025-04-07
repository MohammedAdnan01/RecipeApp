package com.example.swiftbite

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ViewPagerAdapter(private val context: Context, private val slideLayouts: List<Int>) :
    RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {

    class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val view = LayoutInflater.from(context).inflate(viewType, parent, false)
        return PagerViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int = slideLayouts[position]

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {}

    override fun getItemCount(): Int = slideLayouts.size
}

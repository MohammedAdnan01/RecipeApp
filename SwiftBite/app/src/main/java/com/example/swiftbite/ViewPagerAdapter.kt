package com.example.swiftbite



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ViewPagerAdapter(private val layouts: List<Int>) : RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {
    class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return PagerViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int = layouts[position]

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {}

    override fun getItemCount(): Int = layouts.size
}

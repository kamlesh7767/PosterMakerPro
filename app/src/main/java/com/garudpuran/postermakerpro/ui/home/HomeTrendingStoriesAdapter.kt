package com.garudpuran.postermakerpro.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.ui.commonui.models.HomeCategoryModel
import de.hdodenhof.circleimageview.CircleImageView

class HomeTrendingStoriesAdapter(private val mListener:HomeTrendingStoriesAdapterListener):RecyclerView.Adapter<HomeTrendingStoriesAdapter.ItemViewHolder>() {
    private val dataset = ArrayList<HomeCategoryModel>()

    class ItemViewHolder(view:View):RecyclerView.ViewHolder(view) {

        val icon:CircleImageView = view.findViewById(R.id.trending_item_civ)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.trending_rc_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        Glide
            .with(holder.itemView.context)
            .load("https://picsum.photos/200/300")
            .centerCrop()
            .into(holder.icon);
        holder.itemView.setOnClickListener {
            mListener.onHomeTrendingStoriesClicked(item)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(courses: List<HomeCategoryModel>) {
        dataset.clear()
        dataset.addAll(courses)
        notifyDataSetChanged()
    }

    interface HomeTrendingStoriesAdapterListener{
        fun onHomeTrendingStoriesClicked(item: HomeCategoryModel)
    }

}
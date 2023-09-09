package com.garudpuran.postermakerpro.ui.home

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.models.SubCategoryItem
import com.garudpuran.postermakerpro.ui.commonui.models.HomeCategoryModel

class HomeTodayOrUpcomingAdapter(private val language:String):RecyclerView.Adapter<HomeTodayOrUpcomingAdapter.ItemViewHolder>() {
    private val dataset = ArrayList<SubCategoryItem>()

    class ItemViewHolder(view:View):RecyclerView.ViewHolder(view) {

        val icon:ImageView = view.findViewById(R.id.today_item_iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.todays_post_rc_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        Log.d("SUBSET2",item.toString())
        Glide
            .with(holder.itemView.context)
            .load(item.image_url)
            .centerCrop()
            .into(holder.icon)
       /* holder.itemView.setOnClickListener {
            mListener.onHomeTodayOrUpcomingClicked(item)
        }*/
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(courses: List<SubCategoryItem>) {
        dataset.clear()
        dataset.addAll(courses)
        notifyDataSetChanged()
    }

    interface HomeTodayOrUpcomingAdapterListener{
        fun onHomeTodayOrUpcomingClicked(item: HomeCategoryModel)
    }

}
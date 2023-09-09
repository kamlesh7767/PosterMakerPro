package com.garudpuran.postermakerpro.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.models.TrendingStoriesItemModel
import de.hdodenhof.circleimageview.CircleImageView

class HomeTrendingStoriesAdapter(private val mListener:HomeTrendingStoriesAdapterListener,private val language:String):RecyclerView.Adapter<HomeTrendingStoriesAdapter.ItemViewHolder>() {
    private val dataset = ArrayList<TrendingStoriesItemModel>()

    class ItemViewHolder(view:View):RecyclerView.ViewHolder(view) {

        val icon:CircleImageView = view.findViewById(R.id.trending_item_civ)
        val titleTv:TextView = view.findViewById(R.id.trending_item_title_tv)
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

        when(language){
            "en"->    holder.titleTv.text = item.title_eng
            "mr"->    holder.titleTv.text = item.title_mar
            "hi"->    holder.titleTv.text = item.title_hin
        }


        Glide
            .with(holder.itemView.context)
            .load(item.image_url)
            .centerCrop()
            .into(holder.icon);
        holder.itemView.setOnClickListener {
            mListener.onHomeTrendingStoriesClicked(item)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(courses: List<TrendingStoriesItemModel>) {
        dataset.clear()
        dataset.addAll(courses)
        notifyDataSetChanged()
    }

    interface HomeTrendingStoriesAdapterListener{
        fun onHomeTrendingStoriesClicked(item: TrendingStoriesItemModel)
    }

}
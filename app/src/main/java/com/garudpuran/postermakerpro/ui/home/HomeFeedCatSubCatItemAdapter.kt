package com.garudpuran.postermakerpro.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.models.SubCategoryItem

class HomeFeedCatSubCatItemAdapter(private val dataset: List<SubCategoryItem>,private val mListener:CatSubCatItemAdapterListener,private val language:String) :RecyclerView.Adapter<HomeFeedCatSubCatItemAdapter.ItemViewHolder>() {


    class ItemViewHolder(view:View):RecyclerView.ViewHolder(view) {

        val icon:ImageView = view.findViewById(R.id.today_item_iv)
        val titleTv:TextView = view.findViewById(R.id.today_item_title_tv)
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

        when(language){
            "en"->     holder.titleTv.text = item.title_eng
            "mr"->    holder.titleTv.text = item.title_mar
            "hi"->     holder.titleTv.text = item.title_hin
        }


        Glide
            .with(holder.itemView.context)
            .load(item.image_url)
            .centerCrop()
            .into(holder.icon)
         holder.itemView.setOnClickListener {
             mListener.onCatSubCatItemAdapterClicked(item)
         }
    }



    interface CatSubCatItemAdapterListener{
        fun onCatSubCatItemAdapterClicked(item: SubCategoryItem)
    }

}
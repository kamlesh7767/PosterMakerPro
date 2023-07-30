package com.garudpuran.postermakerpro.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.ui.commonui.HomeResources

class HomeFeedRcAdapter(private val mListener:HomeFeedClickListener):RecyclerView.Adapter<HomeFeedRcAdapter.ItemViewHolder>() {
    private val FEED_ITEM_VIEW_TYPE = 100
    private val FEED_RC_ITEM_VIEW_TYPE = 200

    class ItemViewHolder(view: View):RecyclerView.ViewHolder(view) {
val rcView = view.findViewById<RecyclerView>(R.id.feed_interim_rc_view)
val itemUserName = view.findViewById<TextView>(R.id.feed_item_user_name_tv)
val itemPostImage = view.findViewById<ImageView>(R.id.feed_item_post_pic)
val itemDesp = view.findViewById<TextView>(R.id.feed_item_user_desp_tv)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
       return if(viewType == FEED_ITEM_VIEW_TYPE){
           ItemViewHolder(LayoutInflater.from(parent.context)
               .inflate(R.layout.feed_rc_item, parent, false))
        }else{
           ItemViewHolder(LayoutInflater.from(parent.context)
               .inflate(R.layout.feed_category_item, parent, false))
        }

    }

    override fun getItemCount(): Int {
return 50
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
if(holder.itemViewType == FEED_ITEM_VIEW_TYPE){
    holder.itemPostImage.setOnClickListener {
        mListener.onHomeFeedImageClicked()
    }

    Glide
        .with(holder.itemView.context)
        .load("https://picsum.photos/seed/200/300")
        .centerCrop()
        .into(holder.itemPostImage)

}else{
    val adapter = HomeTodayOrUpcomingAdapter()
    adapter.setData(HomeResources.homeCategories())
    holder.rcView.adapter = adapter
}




    }

    override fun getItemViewType(position: Int): Int {
        return if(position<=40){
            if(position!= 0 && position%5 == 0){
                FEED_RC_ITEM_VIEW_TYPE
            }else{
                FEED_ITEM_VIEW_TYPE
            }
        }else{
            FEED_ITEM_VIEW_TYPE
        }

    }

    interface HomeFeedClickListener{
        fun onHomeFeedImageClicked()
    }

}
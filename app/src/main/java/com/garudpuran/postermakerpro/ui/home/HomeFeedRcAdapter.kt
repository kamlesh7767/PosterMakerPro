package com.garudpuran.postermakerpro.ui.home

import android.animation.Animator
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.ddd.androidutils.DoubleClick
import com.ddd.androidutils.DoubleClickListener
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.models.FeedItem
import com.garudpuran.postermakerpro.ui.commonui.HomeResources
import com.garudpuran.postermakerpro.ui.commonui.models.HomeCategoryModel
import com.garudpuran.postermakerpro.utils.Utils

class HomeFeedRcAdapter(private val mListener:HomeFeedClickListener):RecyclerView.Adapter<HomeFeedRcAdapter.ItemViewHolder>() {
    private val FEED_ITEM_VIEW_TYPE = 100
    private val FEED_RC_ITEM_VIEW_TYPE = 200
    private var dataset = mutableListOf<FeedItem>()

    class ItemViewHolder(view: View):RecyclerView.ViewHolder(view) {
val rcView = view.findViewById<RecyclerView>(R.id.feed_interim_rc_view)
val itemUserName = view.findViewById<TextView>(R.id.feed_item_user_name_tv)
val itemPostImage = view.findViewById<ImageView>(R.id.feed_item_post_pic)
val itemLikeIv = view.findViewById<ImageView>(R.id.feed_item_like_Iv)
val itemDesp = view.findViewById<TextView>(R.id.feed_item_user_desp_tv)
val itemLikeTv = view.findViewById<TextView>(R.id.feed_item_like_tv)
val likeAnv = view.findViewById<LottieAnimationView>(R.id.feed_like_anv)

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
return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
if(holder.itemViewType == FEED_ITEM_VIEW_TYPE){
    val item = dataset[position]
    val doubleClick = DoubleClick(object : DoubleClickListener {
        override fun onSingleClickEvent(view: View?) {

        }

        override fun onDoubleClickEvent(view: View?) {
            holder.likeAnv.visibility = View.VISIBLE
            holder.likeAnv.playAnimation()
            item.likes=+1
holder.itemLikeTv.text = "${item.likes} Likes"
            holder.itemLikeIv.setImageResource(R.drawable.filled_heart)
    mListener.onHomeFeedImageLiked(item)

        }
    })

    holder.itemPostImage.setOnClickListener(doubleClick)

    holder.likeAnv.addAnimatorListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator) {


        }

        override fun onAnimationEnd(p0: Animator) {
            holder.likeAnv.visibility = View.GONE

        }

        override fun onAnimationCancel(p0: Animator) {
            holder.likeAnv.visibility = View.GONE

        }

        override fun onAnimationRepeat(p0: Animator) {
        }
    })

    Glide
        .with(holder.itemView.context)
        .load(item.image_url)
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

    @SuppressLint("NotifyDataSetChanged")
    fun setData(dat: List<FeedItem>) {
        dataset.clear()
        dataset.addAll(dat)
        notifyDataSetChanged()
    }

    interface HomeFeedClickListener{
        fun onHomeFeedImageClicked()
        fun onHomeFeedImageLiked(item: FeedItem)
        fun onHomeFeedImageUnLiked()
    }

}
package com.garudpuran.postermakerpro.ui.home

import android.animation.Animator
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
import com.google.android.gms.ads.AdView


class HomeFeedRcAdapter(private val mListener: HomeFeedClickListener,private var dataset : List<FeedItem>,private var likedPosts:MutableList<String>?,private val language:String) :
    RecyclerView.Adapter<HomeFeedRcAdapter.ItemViewHolder>() {
    private val FEED_ITEM_VIEW_TYPE = 100
    private val FEED_AD_VIEW_TYPE = 200


    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemPostImage = view.findViewById<ImageView>(R.id.feed_item_post_pic)
        val itemUserImg = view.findViewById<ImageView>(R.id.feed_item_user_profile_pic)
        val itemOutlinedLike = view.findViewById<ImageView>(R.id.feed_item_to_like_iv)
        val itemFilledLike = view.findViewById<ImageView>(R.id.feed_item_to_unlike_iv)
        val itemDesp = view.findViewById<TextView>(R.id.feed_item_user_desp_tv)
        val itemTitle = view.findViewById<TextView>(R.id.feed_item_user_name_tv)
        val itemLikeTv = view.findViewById<TextView>(R.id.feed_item_like_tv)
        val checkoutbutton = view.findViewById<TextView>(R.id.feed_checkout_button)
        val likeAnv = view.findViewById<LottieAnimationView>(R.id.feed_like_anv)
        val adView = view.findViewById<AdView>(R.id.feed_banner_ad_v)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_rc_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            if (likedPosts==null){
                holder.itemFilledLike.visibility = View.GONE
                holder.itemOutlinedLike.visibility = View.GONE
                holder.itemLikeTv.visibility = View.GONE
            }


            val item = dataset[position]
            val doubleClick = DoubleClick(object : DoubleClickListener {
                override fun onSingleClickEvent(view: View?) {

                }

                override fun onDoubleClickEvent(view: View?) {
                    if(likedPosts!=null){
                        if(!likedPosts!!.contains(item.image_url)){
                            initLikeProcess(
                                holder.likeAnv,
                                holder.itemLikeTv,
                                item
                            )

                            holder.itemFilledLike.visibility = View.VISIBLE
                            holder.itemOutlinedLike.visibility = View.GONE
                            likedPosts!!.add(item.image_url)
                        }
                    }




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

            holder.itemOutlinedLike.setOnClickListener {

                if(likedPosts!= null){
                    if(!likedPosts!!.contains(item.image_url)){
                        initLikeProcess(
                            holder.likeAnv,
                            holder.itemLikeTv,
                            item
                        )
                        holder.itemFilledLike.visibility = View.VISIBLE
                        holder.itemOutlinedLike.visibility = View.GONE
                        likedPosts!!.add(item.image_url)
                    }
                }


            }
            holder.itemFilledLike.setOnClickListener {
                // unlike the post

                (item.likes - 1).also {
                    item.likes = it
                    holder.itemLikeTv!!.text = "${it} Likes"
                }

                holder.itemFilledLike.visibility = View.GONE
                holder.itemOutlinedLike.visibility = View.VISIBLE
                mListener.onHomeFeedImageUnLiked(item)
                likedPosts!!.remove(item.image_url)
            }


            Glide
                .with(holder.itemView.context)
                .load(item.image_url)
                .centerCrop()
                .into(holder.itemPostImage)


            when(language){
                "en"->    holder.itemDesp.text = item.title_eng
                "mr"->    holder.itemDesp.text = item.title_mar
                "hi"->    holder.itemDesp.text = item.title_hin
            }

            if(!item.createdByAdmin){
                holder.itemTitle.text = item.userProfile?.name
                Glide
                    .with(holder.itemView.context)
                    .load(item.userProfile?.profile_image_url)
                    .centerCrop()
                    .into(holder.itemUserImg)
            }


            holder.itemLikeTv!!.text = "${item.likes} Likes"

            if (likedPosts!!.contains(item.image_url)) {
                // post is liked.
                holder.itemOutlinedLike.visibility = View.GONE
                holder.itemFilledLike.visibility = View.VISIBLE
            } else {
                holder.itemOutlinedLike.visibility = View.VISIBLE
                holder.itemFilledLike.visibility = View.GONE
            }
            if (item.type==2){
            holder.checkoutbutton.visibility = View.GONE
            }

            holder.checkoutbutton.setOnClickListener {
                mListener.onHomeFeedCheckOutBtnClicked(item)
            }




    }

    private fun initLikeProcess(
        likeAnv: LottieAnimationView?,
        itemLikeTv: TextView?,
        item: FeedItem
    ) {
        likeAnv!!.visibility = View.VISIBLE
        likeAnv.playAnimation()

        (item.likes + 1).also {
            item.likes = it
            itemLikeTv!!.text = "${it} Likes"
        }

        mListener.onHomeFeedImageLiked(item)
    }


    interface HomeFeedClickListener {
        fun onHomeFeedImageClicked()
        fun onHomeFeedCheckOutBtnClicked(item: FeedItem)
        fun onHomeFeedImageLiked(item: FeedItem)
        fun onHomeFeedImageUnLiked(item: FeedItem)
    }

}
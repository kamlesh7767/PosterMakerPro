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
import com.garudpuran.postermakerpro.models.CategoryItem
import com.garudpuran.postermakerpro.models.FeedItem
import com.garudpuran.postermakerpro.models.SubCategoryItem


class HomeFeedRcAdapter(private val mListener: HomeFeedClickListener,private var dataset : List<FeedItem>,private val datasetSecond:List<Pair<CategoryItem,List<SubCategoryItem>>>,private var likedPosts:MutableList<String>,private val mListener2: HomeFeedCatSubCatItemAdapter.CatSubCatItemAdapterListener) :
    RecyclerView.Adapter<HomeFeedRcAdapter.ItemViewHolder>() {
    private val FEED_ITEM_VIEW_TYPE = 100
    private val FEED_RC_ITEM_VIEW_TYPE = 200
        // private var dataset = mutableListOf<FeedItem>()
  //  private var likedPosts = mutableListOf<String>()
  //  private var datasetSecond = mutableListOf<Pair<CategoryItem,List<SubCategoryItem>>>()
    private val combinedList: List<Any>

    init {
        combinedList = generateCombinedList()

    }

    private fun generateCombinedList(): List<Any> {
        val combined = mutableListOf<Any>()
    combined.addAll(dataset)
    combined.addAll(datasetSecond)
        return combined.shuffled()
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rcView = view.findViewById<RecyclerView>(R.id.feed_interim_rc_view)
        val itemUserName = view.findViewById<TextView>(R.id.feed_item_user_name_tv)
        val itemPostImage = view.findViewById<ImageView>(R.id.feed_item_post_pic)
        val itemOutlinedLike = view.findViewById<ImageView>(R.id.feed_item_to_like_iv)
        val itemFilledLike = view.findViewById<ImageView>(R.id.feed_item_to_unlike_iv)
        val itemDesp = view.findViewById<TextView>(R.id.feed_item_user_desp_tv)
        val itemLikeTv = view.findViewById<TextView>(R.id.feed_item_like_tv)
        val checkoutbutton = view.findViewById<TextView>(R.id.feed_checkout_button)
        val likeAnv = view.findViewById<LottieAnimationView>(R.id.feed_like_anv)
        val catSubCatItemTitleTv = view.findViewById<TextView>(R.id.feed_catsubCat_item_title_tv)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return if (viewType == FEED_ITEM_VIEW_TYPE) {
            ItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.feed_rc_item, parent, false)
            )
        } else {
            ItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.feed_category_item, parent, false)
            )
        }

    }

    override fun getItemCount(): Int {
        return generateCombinedList().size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (holder.itemViewType == FEED_ITEM_VIEW_TYPE) {
            val item = combinedList[position] as FeedItem
            val doubleClick = DoubleClick(object : DoubleClickListener {
                override fun onSingleClickEvent(view: View?) {

                }

                override fun onDoubleClickEvent(view: View?) {
                    if(!likedPosts.contains(item.image_url)){
                        initLikeProcess(
                            holder.likeAnv,
                            holder.itemLikeTv,
                            item
                        )

                        holder.itemFilledLike.visibility = View.VISIBLE
                        holder.itemOutlinedLike.visibility = View.GONE
                        likedPosts.add(item.image_url)
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
                // like the posr
               if(!likedPosts.contains(item.image_url)){
                   initLikeProcess(
                       holder.likeAnv,
                       holder.itemLikeTv,
                       item
                   )
                   holder.itemFilledLike.visibility = View.VISIBLE
                   holder.itemOutlinedLike.visibility = View.GONE
                   likedPosts.add(item.image_url)
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
                likedPosts.remove(item.image_url)
            }


            Glide
                .with(holder.itemView.context)
                .load(item.image_url)
                .centerCrop()
                .into(holder.itemPostImage)

            holder.itemDesp.text = item.title_eng
            holder.itemLikeTv!!.text = "${item.likes} Likes"

            if (likedPosts.contains(item.image_url)) {
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

        } else {
            val item2 = combinedList[position] as Pair<CategoryItem,List<SubCategoryItem>>
            holder.catSubCatItemTitleTv.text = item2.first.title_eng
            val adapter = HomeFeedCatSubCatItemAdapter(item2.second,mListener2)
            holder.rcView.adapter = adapter
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



    override fun getItemViewType(position: Int): Int {
        val item = combinedList[position]

        return if (item is Pair<*,*>) {
            FEED_RC_ITEM_VIEW_TYPE
        } else {
            FEED_ITEM_VIEW_TYPE
        }

    }


    interface HomeFeedClickListener {
        fun onHomeFeedImageClicked()
        fun onHomeFeedCheckOutBtnClicked(item: FeedItem)
        fun onHomeFeedImageLiked(item: FeedItem)
        fun onHomeFeedImageUnLiked(item: FeedItem)
    }

}
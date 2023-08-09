package com.garudpuran.postermakerpro.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.models.CategoryItem
import com.garudpuran.postermakerpro.models.SubCategoryItem
import com.garudpuran.postermakerpro.ui.home.HomeFeedCatSubCatItemAdapter


class CategoriesAdapter(private val datasetSecond:List<Pair<CategoryItem,List<SubCategoryItem>>>,private val mListener: HomeFeedCatSubCatItemAdapter.CatSubCatItemAdapterListener) :
    RecyclerView.Adapter<CategoriesAdapter.ItemViewHolder>() {

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

        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_category_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return datasetSecond.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val item2 = datasetSecond[position]
            holder.catSubCatItemTitleTv.text = item2.first.title_eng
            val adapter = HomeFeedCatSubCatItemAdapter(item2.second,mListener)
            holder.rcView.adapter = adapter



    }



}
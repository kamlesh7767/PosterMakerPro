package com.garudpuran.postermakerpro.ui.subcat


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.models.PostItem


class AllSubCategoryPostsAdapter(private val mListener:AllSubCategoryPostsAdapterListener):RecyclerView.Adapter<AllSubCategoryPostsAdapter.ItemViewHolder>() {
    private var dataset = ArrayList<PostItem>()
    class ItemViewHolder(view : View):RecyclerView.ViewHolder(view) {
        val titleTv = view.findViewById<TextView>(R.id.today_item_title_tv)
        val titleIv = view.findViewById<ImageView>(R.id.today_item_iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter = LayoutInflater.from(parent.context).inflate(R.layout.todays_post_rc_item,parent,false)
        return ItemViewHolder(adapter)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.titleTv.text = item.title_eng
        Glide.with(holder.itemView.context).load(item.image_url).into(holder.titleIv)
        holder.itemView.setOnClickListener {
            mListener.onAllSubCategoryPostsAdapterListItemClicked(item)
        }


    }
    @SuppressLint("NotifyDataSetChanged")
    fun setData(courses: List<PostItem>) {
        dataset.clear()
        dataset.addAll(courses)
        notifyDataSetChanged()
    }

    interface AllSubCategoryPostsAdapterListener{
        fun onAllSubCategoryPostsAdapterListItemClicked(item:PostItem)
    }
}
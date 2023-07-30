package com.garudpuran.postermakerpro.ui.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.ui.commonui.models.HomeCategoryModel
import de.hdodenhof.circleimageview.CircleImageView

class ProfileItemsAdapter:RecyclerView.Adapter<ProfileItemsAdapter.ItemViewHolder>() {

    private val dataset = ArrayList<HomeCategoryModel>()
    class ItemViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val itemUserName = view.findViewById<TextView>(R.id.profile_item_user_name_tv)
        val itemUserImage = view.findViewById<CircleImageView>(R.id.profile_item_user_profile_pic)
        val itemDesp = view.findViewById<TextView>(R.id.profile_item_user_desp_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_profile_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
       return 10
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(courses: List<HomeCategoryModel>) {
        dataset.clear()
        dataset.addAll(courses)
        notifyDataSetChanged()
    }
}
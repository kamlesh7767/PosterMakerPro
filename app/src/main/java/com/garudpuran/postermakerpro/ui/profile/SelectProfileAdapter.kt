package com.garudpuran.postermakerpro.ui.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.models.UserProfessionalProfileModel
import com.garudpuran.postermakerpro.ui.commonui.models.HomeCategoryModel
import de.hdodenhof.circleimageview.CircleImageView

class SelectProfileAdapter(private val mListener:SelectProfileAdapterListener):RecyclerView.Adapter<SelectProfileAdapter.ItemViewHolder>() {

    private val dataset = ArrayList<UserProfessionalProfileModel>()
    class ItemViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val itemUserName = view.findViewById<TextView>(R.id.profile_item_user_name_tv)
        val itemUserImage = view.findViewById<CircleImageView>(R.id.profile_item_user_profile_pic)
        val itemDesp = view.findViewById<TextView>(R.id.profile_item_user_desp_tv)
        val deleteBtn = view.findViewById<ImageView>(R.id.delete_pro_profile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_profile_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.itemUserName.text = item.name
        holder.itemDesp.text = item.mobile_number
        Glide
            .with(holder.itemView.context)
            .load(item.logo_image_url)
            .centerCrop()
            .into(holder.itemUserImage)
        holder.deleteBtn.visibility = View.GONE
        holder.itemView.setOnClickListener {
            mListener.onSelectProfileAdapterItemClicked(item)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(courses: List<UserProfessionalProfileModel>) {
        dataset.clear()
        dataset.addAll(courses)
        notifyDataSetChanged()
    }

    interface SelectProfileAdapterListener{
        fun onSelectProfileAdapterItemClicked(item :UserProfessionalProfileModel)
    }
}
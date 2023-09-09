package com.garudpuran.postermakerpro.ui.editing.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import com.garudpuran.postermakerpro.ui.commonui.models.EditFragOptionsModel
import de.hdodenhof.circleimageview.CircleImageView

class StoryFrameRcAdapter(private val data: UserPersonalProfileModel) :
    RecyclerView.Adapter<StoryFrameRcAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userPic = view.findViewById<CircleImageView>(R.id.story_user_profile_pic)
        val userName = view.findViewById<TextView>(R.id.story_user_name_tv)
        val userDes = view.findViewById<TextView>(R.id.story_user_desp_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.story_post_frame_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.userDes.text = data.mobile_number
        holder.userName.text = data.name
        Glide.with(holder.itemView.context).load(data.profile_image_url).into(holder.userPic)

    }


}
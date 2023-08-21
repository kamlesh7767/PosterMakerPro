package com.garudpuran.postermakerpro.ui.editing.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.models.UserPersonalProfileModel
import de.hdodenhof.circleimageview.CircleImageView


class ViewPagerAdapter(private val data: UserPersonalProfileModel, private val layouts: List<Int>) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    private lateinit var userPic: CircleImageView
    private lateinit var userName: TextView
    private lateinit var userDes: TextView
    private lateinit var userAddress: TextView

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.frame_root_con_l)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.story_post_frame_item, parent, false) as ConstraintLayout
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = LayoutInflater.from(holder.itemView.context).inflate(layouts[position], null)

        userPic = view.findViewById<CircleImageView>(R.id.user_profile_pic_iv)
        userName = view.findViewById<TextView>(R.id.user_name_tv)
        userDes = view.findViewById<TextView>(R.id.user_mobile_tv)
        userAddress = view.findViewById<TextView>(R.id.user_address_tv)

        userDes.text = data.mobile_number
        userDes.visibility = View.GONE
        userName.text = data.name
        userAddress.text = data.address
        Glide.with(holder.itemView.context).load(data.profile_image_url).into(userPic)

        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        view.layoutParams = layoutParams

        holder.constraintLayout.removeAllViews()
        holder.constraintLayout.addView(view)
    }

    override fun getItemCount(): Int {
        return layouts.size
    }




}


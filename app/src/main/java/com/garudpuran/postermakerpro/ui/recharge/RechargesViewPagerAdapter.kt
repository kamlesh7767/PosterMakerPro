package com.garudpuran.postermakerpro.ui.recharge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.models.RechargeItem

class RechargesViewPagerAdapter(private val offers: List<RechargeItem>,private val language:String,private val mlistener:RechargesViewPagerAdapterListener) :
    RecyclerView.Adapter<RechargesViewPagerAdapter.OfferViewHolder>() {

    inner class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.recharge_item_iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recharge_offer, parent, false)
        return OfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        val item = offers[position]

        when(language){
            "en"->      Glide.with(holder.itemView.context).load(item.image_eng).into(holder.imageView)
            "mr"->     Glide.with(holder.itemView.context).load(item.image_mar).into(holder.imageView)
            "hi"->      Glide.with(holder.itemView.context).load(item.image_hin).into(holder.imageView)
        }

        holder.itemView.setOnClickListener {
            mlistener.rechargesViewPagerAdapterItemClicked(item)
        }


    }

    override fun getItemCount(): Int = offers.size

    interface RechargesViewPagerAdapterListener{

        fun rechargesViewPagerAdapterItemClicked(item: RechargeItem)
    }
}

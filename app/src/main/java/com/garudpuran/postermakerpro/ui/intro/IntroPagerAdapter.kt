package com.garudpuran.postermakerpro.ui.intro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.ui.commonui.HomeResources

class IntroPagerAdapter() : RecyclerView.Adapter<IntroPagerAdapter.OfferViewHolder>() {

    private val pages = HomeResources.introPages()

    inner class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.recharge_item_iv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recharge_offer, parent, false)
        return OfferViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        val item = pages[position]
        holder.imageView.setImageResource(item)
    }

    override fun getItemCount(): Int = pages.size

}

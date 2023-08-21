package com.garudpuran.postermakerpro.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.models.SearchModel

class SearchItemsRCAdapter(private val mListener:SearchItemClickListener,private val language:String):RecyclerView.Adapter<SearchItemsRCAdapter.ItemViewHolder>() {
    private val dataset = ArrayList<SearchModel>()


    class ItemViewHolder(view:View):RecyclerView.ViewHolder(view) {
val titleTv:TextView = view.findViewById(R.id.search_item_title_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
       return  dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
       val item = dataset[position]

        when(language){
            "en"->   holder.titleTv.text = item.title_eng
            "mr"->    holder.titleTv.text = item.title_mar
            "hi"->   holder.titleTv.text = item.title_hin
        }
        holder.itemView.setOnClickListener {
            mListener.onSearchItemClicked(item)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(adItems: List<SearchModel>) {
        this.dataset.clear()
        this.dataset.addAll(adItems)
        notifyDataSetChanged()

    }

    interface SearchItemClickListener{
        fun  onSearchItemClicked(item:SearchModel)
    }
}
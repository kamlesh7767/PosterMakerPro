package com.garudpuran.postermakerpro.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.ui.commonui.models.HomeCategoryModel

class HomeCategoryAdapter(private val mListener:HomeCategoryGridListener):RecyclerView.Adapter<HomeCategoryAdapter.ItemViewHolder>() {
    private val dataset = ArrayList<HomeCategoryModel>()

    class ItemViewHolder(view:View):RecyclerView.ViewHolder(view) {

val icon:ImageView = view.findViewById(R.id.cat_iv)
val catTitle:TextView = view.findViewById(R.id.cat_title_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_grid_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
      return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
       val item = dataset[position]
        holder.catTitle.text = holder.itemView.context.getText(item.title)
        holder.icon.setImageDrawable( AppCompatResources.getDrawable(holder.itemView.context,item.icon_res))
        holder.itemView.setOnClickListener {
            mListener.onHomeCatClicked(item)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(courses: List<HomeCategoryModel>) {
        dataset.clear()
        dataset.addAll(courses)
        notifyDataSetChanged()
    }

  interface HomeCategoryGridListener{
      fun onHomeCatClicked(item: HomeCategoryModel)
  }

}
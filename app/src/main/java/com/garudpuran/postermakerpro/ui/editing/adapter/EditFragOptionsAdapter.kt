package com.garudpuran.postermakerpro.ui.editing.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.ui.commonui.HomeResources
import com.garudpuran.postermakerpro.ui.commonui.models.EditFragOptionsModel
import com.garudpuran.postermakerpro.ui.editing.EditPostActivity

class EditFragOptionsAdapter(val activity: EditPostActivity, private val mListener: EditOptionsListener) :
    RecyclerView.Adapter<EditFragOptionsAdapter.ItemViewHolder>() {
    private val dataset = listOf<EditFragOptionsModel>(
        EditFragOptionsModel(0, "Frames"),
        EditFragOptionsModel(1, "Profile Photo"),
        EditFragOptionsModel(2, "Icon"),
        EditFragOptionsModel(3, "Name"),
        EditFragOptionsModel(4, "Address"),
        EditFragOptionsModel(5, "Mobile Number"),




    )
    private var selectedPosition = 0

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.chip_title_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.chip_item_view, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]
        holder.titleView.text = item.title
        if (position == selectedPosition) {
            holder.titleView.background =
                AppCompatResources.getDrawable(activity, R.drawable.chip_selected_bg)
            holder.titleView.setTextColor(Color.WHITE)
        } else {
            holder.titleView.background =
                AppCompatResources.getDrawable(activity, R.drawable.chip_not_selected_bg)
            holder.titleView.setTextColor(activity.getColor(com.denzcoskun.imageslider.R.color.grey_font))
        }

        holder.titleView.setOnClickListener {
            if(!activity.isEmptyFrameSelected()){
                setItemSelected(position)
            }
            mListener.onEditOptionsClicked(item)
        }

    }

    private fun setItemSelected(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }


    interface EditOptionsListener {
        fun onEditOptionsClicked(item: EditFragOptionsModel)
    }


}
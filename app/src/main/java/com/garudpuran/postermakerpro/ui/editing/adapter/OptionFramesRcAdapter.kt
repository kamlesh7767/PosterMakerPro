package com.garudpuran.postermakerpro.ui.editing.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.ui.commonui.HomeResources

class OptionFramesRcAdapter(
    private val mListener: OptionFramesRcAdapterListener,
    private val context: Context
) :
    RecyclerView.Adapter<OptionFramesRcAdapter.ItemViewHolder>() {
    private val dataset = HomeResources.miniFrames()
    private var selectedPosition = 0

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.edit_options_frame_rc_item_iv)
        val positionTv: TextView = view.findViewById(R.id.edit_frag_frames_rc_item_position_tv)
        val parentL: ConstraintLayout = view.findViewById(R.id.edit_options_frame_rc_item_parent_l)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.edit_frag_frames_rc_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.positionTv.text = position.toString()

        if (position != 0) {
            holder.imageView.setImageDrawable(AppCompatResources.getDrawable(context, dataset[position]))
        } else {
            // Clear the ImageView for the 0th item
            holder.imageView.setImageDrawable(null)
        }

        if (position == selectedPosition) {
            holder.parentL.background =
                AppCompatResources.getDrawable(context, R.drawable.chip_selected_bg)
            holder.positionTv.setTextColor(Color.WHITE)
        } else {
            holder.parentL.background =
                AppCompatResources.getDrawable(context, R.drawable.chip_not_selected_bg)
            holder.positionTv.setTextColor(context.getColor(R.color.grey_font))
        }

        holder.itemView.setOnClickListener {
            setItemSelected(position)
            mListener.onOptionFramesClicked(position)
        }
    }

    private fun setItemSelected(position: Int) {
        val oldPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(oldPosition)
        notifyItemChanged(position)
    }


    interface OptionFramesRcAdapterListener {
        fun onOptionFramesClicked(position: Int)
    }


}
package com.garudpuran.postermakerpro.ui.transactionhistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.garudpuran.postermakerpro.R

class TransactionHistoryAdapter:RecyclerView.Adapter<TransactionHistoryAdapter.ItemViewHolder>() {
    class ItemViewHolder(view: View):RecyclerView.ViewHolder(view) {
val transName = view.findViewById<TextView>(R.id.transaction_item_name_tv)
val transDesp = view.findViewById<TextView>(R.id.transaction_item_desp_tv)
val transPrice = view.findViewById<TextView>(R.id.transaction_item_price_tv)
val transPriceDesp = view.findViewById<TextView>(R.id.transaction_item_price_desp_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item,parent,false)
        return ItemViewHolder(adapter)
    }

    override fun getItemCount(): Int {
       return 10
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

    }
}
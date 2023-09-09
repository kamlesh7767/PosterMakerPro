package com.garudpuran.postermakerpro.ui.transactionhistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.garudpuran.postermakerpro.R
import com.garudpuran.postermakerpro.models.SuccessfulRecharges

class TransactionHistoryAdapter(private val recharges: ArrayList<SuccessfulRecharges>) :RecyclerView.Adapter<TransactionHistoryAdapter.ItemViewHolder>() {
    class ItemViewHolder(view: View):RecyclerView.ViewHolder(view) {
val transName: TextView = view.findViewById<TextView>(R.id.transaction_item_name_tv)
val transpointsTv: TextView = view.findViewById<TextView>(R.id.trans_wallet_points_tv)
val transDesp: TextView = view.findViewById<TextView>(R.id.transaction_item_desp_tv)
val transPrice: TextView = view.findViewById<TextView>(R.id.transaction_item_price_tv)
val transPriceDesp: TextView = view.findViewById<TextView>(R.id.transaction_item_price_desp_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapter = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item,parent,false)
        return ItemViewHolder(adapter)
    }

    override fun getItemCount(): Int {
       return recharges.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
val item = recharges[position]
        holder.transName.text = "Points Added"
        holder.transDesp.text = "Recharged On: "+item.dateTime
        holder.transPrice.text = "₹"+item.rechargeItem!!.amount
        holder.transPriceDesp.text = "Debited"
        holder.transpointsTv.text = item.rechargeItem!!.points.toString()
    }
}
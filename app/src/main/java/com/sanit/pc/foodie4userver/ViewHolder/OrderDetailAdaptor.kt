package com.sanit.pc.foodie4userver.ViewHolder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sanit.pc.foodie4userver.R
import com.sanit.pc.foodie4userver.beans.Order

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var pro_name: TextView
    var pro_qua: TextView
    var pro_price: TextView
    var pro_dis: TextView

    init {
        pro_dis = itemView.findViewById(R.id.pro_discount) as TextView
        pro_name = itemView.findViewById(R.id.pro_name) as TextView
        pro_price = itemView.findViewById(R.id.pro_price) as TextView
        pro_qua = itemView.findViewById(R.id.pro_quantity) as TextView
    }
}

class OrderDetailAdaptor(internal var listOrder: List<Order>) : RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_order_detail, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val order = listOrder[position]
        holder.pro_qua.text = String.format("Quantity : %s", order.quantiy)
        holder.pro_price.text = String.format("Price : %s", order.price)
        holder.pro_name.text = String.format("Name : %s", order.productName)
        holder.pro_dis.text = String.format("Discount : %s", order.discount)
    }

    override fun getItemCount(): Int {
        return listOrder.size
    }
}

package com.sanit.pc.foodie4userver.ViewHolder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.sanit.pc.foodie4userver.R
import com.sanit.pc.foodie4userver.interfaces.ItemClickListner

class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{


    var orderId: TextView
    var orderStatus: TextView
    var orderPhone: TextView
    var orderAddress: TextView
    var btnDelete:Button
    var btnUpdate:Button
    var btnDetails:Button
     var itemClickListner: ItemClickListner?=null

    init {

        orderId = itemView.findViewById(R.id.order_id)
        orderStatus = itemView.findViewById(R.id.order_status)
        orderAddress = itemView.findViewById(R.id.order_address)
        orderPhone = itemView.findViewById(R.id.order_phone)
        btnDelete = itemView.findViewById(R.id.btn_delete_os)
        btnUpdate = itemView.findViewById(R.id.btn_update_os)
        btnDetails = itemView.findViewById(R.id.btn_details_os)

        itemView.setOnClickListener(this)

    }


    fun SetOnItemClickListener(itemClickListner: ItemClickListner){
        this.itemClickListner = itemClickListner
    }


    override fun onClick(v: View?) {
        itemClickListner!!.onClick(v!!,adapterPosition,false)
    }


}
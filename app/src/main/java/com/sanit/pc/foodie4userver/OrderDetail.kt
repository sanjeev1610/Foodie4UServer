package com.sanit.pc.foodie4userver

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.sanit.pc.foodie4userver.Common.Common
import com.sanit.pc.foodie4userver.ViewHolder.OrderDetailAdaptor
import kotlinx.android.synthetic.main.activity_order_detail.*

class OrderDetail : AppCompatActivity() {

    lateinit var order_id_value:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)


        if (intent != null) {
            order_id_value = intent.getStringExtra("orderId")
        }

        order_id.setText(order_id_value)
        order_phone.setText(Common.currentRequest.phone)
        order_total.setText(Common.currentRequest.total)
        order_address.setText(Common.currentRequest.address)
        order_comment.setText(Common.currentRequest.comment)
        order_status.setText(Common.convertCodeToStatus(Common.currentRequest.status))

        val adaptor = OrderDetailAdaptor(Common.currentRequest.foods)
        adaptor.notifyDataSetChanged()
        recycle_orderDetail.setHasFixedSize(true)
        recycle_orderDetail.setAdapter(adaptor)

    }
}

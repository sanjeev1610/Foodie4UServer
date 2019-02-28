package com.sanit.pc.foodie4userver

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.*
import com.jaredrummler.materialspinner.MaterialSpinner
import com.sanit.pc.foodie4userver.Common.Common
import com.sanit.pc.foodie4userver.ViewHolder.OrderViewHolder
import com.sanit.pc.foodie4userver.beans.DataMessage
import com.sanit.pc.foodie4userver.beans.MyResponse
import com.sanit.pc.foodie4userver.beans.Requests
import com.sanit.pc.foodie4userver.beans.Token
import com.sanit.pc.foodie4userver.interfaces.ItemClickListner
import kotlinx.android.synthetic.main.activity_order_staus.*
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class OrderStatus : AppCompatActivity() {

    lateinit var adapter: FirebaseRecyclerAdapter<Requests, OrderViewHolder>
    lateinit var requests:DatabaseReference
    var materialSpinner:MaterialSpinner?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_staus)

        requests = FirebaseDatabase.getInstance().getReference("Requests")
        recycler_order_status.setHasFixedSize(true)
        recycler_order_status.layoutManager = LinearLayoutManager(this)

        loadOrders(Common.currentUser.phone)


    }

    private fun loadOrders(phone: String) {

        adapter = object: FirebaseRecyclerAdapter<Requests, OrderViewHolder>(

            Requests::class.java,
            R.layout.layout_order_status,
            OrderViewHolder::class.java,
            requests
        )
        {
            override fun populateViewHolder(vh: OrderViewHolder?, model: Requests?, position: Int) {
                Common.currentRequest = model!!
                vh!!.orderPhone.text = model.phone
                vh.orderAddress.text = model.address
                vh.orderStatus.text = Common.convertCodeToStatus(model.status)
                vh.orderId.text = adapter.getRef(position).key

                vh.btnUpdate.setOnClickListener {
                    showUpdateDialog(adapter.getRef(position).key,adapter.getItem(position))
                }
                vh.btnDelete.setOnClickListener {
                    showDeleteDialog(adapter.getRef(position).key)

                }
                vh.btnDetails.setOnClickListener {
                    val intent  = Intent(this@OrderStatus,OrderDetail::class.java)
                    intent.putExtra("orderId",adapter.getRef(position).key)
                    startActivity(intent)

                }
                vh.SetOnItemClickListener(object : ItemClickListner{
                    override fun onClick(view: View, pos: Int, isLongClick: Boolean) {
                        val intent  = Intent(this@OrderStatus,TrackOrder::class.java)
                        intent.putExtra("CurrentRequest",Common.currentRequest.address)
                        startActivity(intent)
                    }

                })


            }

        }

        recycler_order_status.adapter = adapter

    }

    private fun showDeleteDialog(key: String?) {
        requests.child(key!!).removeValue()
    }

    private fun showUpdateDialog(key: String?, item: Requests?) {

        val alrt = AlertDialog.Builder(this)
        alrt.setTitle("Update Order Status")
        alrt.setCancelable(true)
        val inflater = this.layoutInflater
        val updateView = inflater.inflate(R.layout.update_order_status_layout, null)
        materialSpinner = updateView.findViewById(R.id.spinner)
        materialSpinner!!.setItems("Placed","On My Way","Shipping")

        alrt.setView(updateView)
        alrt.setPositiveButton("YES"){dialog, which ->
            item!!.status = materialSpinner!!.selectedIndex.toString()
            Toast.makeText(this@OrderStatus,"Status Updated",Toast.LENGTH_SHORT).show()

            requests.child(key!!).setValue(item)
            sendNotificationToUser(key, item)

        }
        alrt.setNegativeButton(
            "No"
        ) { dialog, which -> dialog.dismiss() }
        alrt.show()

    }

    private fun sendNotificationToUser(key: String, item: Requests) {
        val tokens = FirebaseDatabase.getInstance().getReference("Tokens")
        tokens.orderByKey().equalTo(item.phone)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (postSnapshot in dataSnapshot.children) {
                        val token = postSnapshot.getValue<Token>(Token::class.java)

                        //                         //Make new Payload
                        //                         Notification notification = new Notification("Your Order"+key+" was updated","Routine basket");
                        //                         Sender content = new Sender(token.getToken(), notification);
                        val dataSend = HashMap<String, String>()
                        dataSend["title"] = "Routine Basket"
                        dataSend["message"] = "Your order " + key + "was updated"
                        val dataMessage = DataMessage(token!!.token!!, dataSend)


                        Common.getFCMService().sendNotification(dataMessage)
                            .enqueue(object : Callback<MyResponse> {
                                override fun onResponse(
                                    call: retrofit2.Call<MyResponse>,
                                    response: Response<MyResponse>
                                ) {
                                    assert(response.body()!=null)
                                    if (response.body()!!.success == 1) {
                                        Toast.makeText(this@OrderStatus, "Your Order Was Updated", Toast.LENGTH_LONG)
                                            .show()

                                    } else {
                                        Toast.makeText(
                                            this@OrderStatus,
                                            "Your Order Was Updated but failedto send Notification",
                                            Toast.LENGTH_LONG
                                        ).show()

                                    }
                                }

                                override fun onFailure(call: retrofit2.Call<MyResponse>, t: Throwable) {
                                    Log.e("ERROR", t.message)
                                }
                            })
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
    }

}

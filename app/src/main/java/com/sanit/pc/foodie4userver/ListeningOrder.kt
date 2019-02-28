package com.sanit.pc.foodie4userver

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.NotificationChannel
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.google.firebase.database.*
import com.sanit.pc.foodie4userver.Common.Common
import com.sanit.pc.foodie4userver.beans.Requests
import kotlin.random.Random


class ListeningOrder:Service(), ChildEventListener {


        var requests: DatabaseReference?=null
        override fun onCreate() {
            super.onCreate()
            requests = FirebaseDatabase.getInstance().getReference("Requests")

        }

        override fun onBind(intent: Intent?): IBinder? {
            return null
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            requests!!.addChildEventListener(this)
            return super.onStartCommand(intent, flags, startId)
        }

        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val requestBean = p0.getValue(Requests::class.java)
            if(requestBean!!.status.equals("0")){
                showNotification(p0.key,requestBean)
            }
        }

        override fun onChildRemoved(p0: DataSnapshot) {

        }

        private fun showNotification(key: String?, requestsBean: Requests?) {

            val intent = Intent(this@ListeningOrder,OrderStatus::class.java)
//        intent.putExtra("Phone",Common.currentUser.phone)
            val pIntent = PendingIntent.getActivity(this,0,intent,0)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {



                // The id of the channel.
                val id = "my_channel_02"

// The user-visible name of the channel.
                val name = "NotifiChannelName"

// The user-visible description of the channel.
                val description = "Notification Description"

                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val mChannel =  NotificationChannel(id,name,importance)

                // Configure the notification channel.
                mChannel.description = description

                mChannel.enableLights(true)
                // Sets the notification light color for notifications posted to this
                // channel, if the device supports this feature.
                mChannel.lightColor = Color.RED

                mChannel.enableVibration(true)
                mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)


                notificationManager.createNotificationChannel(mChannel)
            }

            val builder = NotificationCompat.Builder(this)
            builder.setAutoCancel(true)
            builder.setTicker("Order Status ")
            builder.setContentInfo("Info")
            builder.setSmallIcon(R.drawable.ic_shopping_cart_black_24dp)
            builder.setContentIntent(pIntent)
            builder.setDefaults(Notification.DEFAULT_ALL)
            builder.setContentText("Your have a NEW ORDER at $key was ${Common.convertCodeToStatus(requestsBean!!.status)}) ")
            builder.setChannelId("my_channel_02")
            builder.setContentTitle("ContentZTitle")
            builder.setPriority(NotificationCompat.PRIORITY_HIGH)

            val randInt = Random.nextInt(9999-1)+1
            notificationManager.notify(randInt,builder.build())

        }


        override fun onDestroy() {
            super.onDestroy()
        }

    }

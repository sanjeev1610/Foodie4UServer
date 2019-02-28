package com.sanit.pc.foodie4userver.Common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.sanit.pc.foodie4userver.Remote.APIService
import com.sanit.pc.foodie4userver.Remote.IGeoCoordinates
import com.sanit.pc.foodie4userver.Remote.RetrofitClient
import com.sanit.pc.foodie4userver.Remote.RetrofitClient1
import com.sanit.pc.foodie4userver.beans.Requests
import com.sanit.pc.foodie4userver.beans.User


object Common {
    lateinit var currentUser: User
    lateinit var currentRequest: Requests

    val UPDATE: String = "Update"
    val DELETE: String = "Delete"
    val REQUEST_PERMISSION_CODE = 999

    val baseUrl = "https://maps.googleapis.com"

    //create retrofit service class and object
    fun getGeoCodeServices(): IGeoCoordinates {
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates::class.java)
    }

    private val BASE_URL = "https://fcm.googleapis.com/"

    fun getFCMService(): APIService {
        return RetrofitClient1.getClient(BASE_URL).create(APIService::class.java)
    }


    //create bitmap with new width n height
    fun scaleBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)

        val scaleX = newWidth / bitmap.width.toFloat()
        val scaleY = newHeight / bitmap.height.toFloat()
        val pivotX = 0f
        val pivotY = 0f

        val scaleMatric = Matrix()
        scaleMatric.setScale(scaleX, scaleY, pivotX, pivotY)

        val canvas = Canvas(scaledBitmap)
        canvas.matrix = scaleMatric
        canvas.drawBitmap(bitmap, 0f, 0f, Paint(Paint.FILTER_BITMAP_FLAG))

        return scaledBitmap

    }

    //check internet connection

    fun isConnectedToInternet(context:Context):Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val networkInfo = connectivityManager.allNetworkInfo
            if (networkInfo != null) {
                for (i in networkInfo) {
                    if (i.state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
        }
        return false
    }

    //convert status code to string
    fun convertCodeToStatus(status: String): String {
        when (status) {
            "0" -> return "Placed"
            "1" -> return "On My Way"
            "2" -> return "Shipping"
            else -> return "Shipped"
        }

    }

}















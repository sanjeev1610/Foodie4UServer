package com.sanit.pc.foodie4userver.interfaces

import android.view.View

interface ItemClickListner {
    fun onClick(view: View, pos:Int, isLongClick:Boolean):Unit
}
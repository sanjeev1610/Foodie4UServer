package com.sanit.pc.foodie4userver.ViewHolder

import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.sanit.pc.foodie4userver.Common.Common
import com.sanit.pc.foodie4userver.R
import com.sanit.pc.foodie4userver.interfaces.ItemClickListner


class FoodListVIewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnCreateContextMenuListener {


    var imgView: ImageView
    var textFoodName: TextView
    lateinit var itemClickListner: ItemClickListner
    init {
        imgView = itemView.findViewById(R.id.food_image)
        textFoodName= itemView.findViewById(R.id.food_name)
        itemView.setOnClickListener(this)
        itemView.setOnCreateContextMenuListener(this)
    }

    fun setItemClickListener(itemClickListner: ItemClickListner){
        this.itemClickListner = itemClickListner
    }
    override fun onClick(v: View?) {
        itemClickListner.onClick(v!!,adapterPosition,false)

    }
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menu!!.setHeaderTitle("Select an Action")
        menu.add(0,0,adapterPosition,Common.DELETE)
        menu.add(0,1,adapterPosition,Common.UPDATE)

    }

}
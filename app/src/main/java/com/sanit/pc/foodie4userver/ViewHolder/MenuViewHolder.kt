package com.sanit.pc.foodie4userver.ViewHolder

import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.sanit.pc.foodie4userver.Common.Common
import com.sanit.pc.foodie4userver.R
import com.sanit.pc.foodie4userver.interfaces.ItemClickListner

class MenuViewHolder(itemVIew:View) : RecyclerView.ViewHolder(itemVIew),View.OnClickListener,View.OnCreateContextMenuListener{

    var imgView:ImageView
    var textMenuName:TextView
    lateinit var itemClickListner: ItemClickListner
    init {
        imgView = itemVIew.findViewById(R.id.menu_image)
        textMenuName= itemVIew.findViewById(R.id.menu_name)
        itemVIew.setOnClickListener(this)
        itemVIew.setOnCreateContextMenuListener(this)

    }

    fun setItemClickListener(itemClickListner: ItemClickListner){
        this.itemClickListner = itemClickListner
    }
    override fun onClick(v: View?) {
        itemClickListner.onClick(v!!,adapterPosition,false)

    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menu!!.setHeaderTitle("Select Action")
        menu.add(0,0,adapterPosition,Common.UPDATE)
        menu.add(0,1,adapterPosition,Common.DELETE)


    }


}












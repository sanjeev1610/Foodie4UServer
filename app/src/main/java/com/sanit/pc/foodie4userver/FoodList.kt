package com.sanit.pc.foodie4userver

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.mancj.materialsearchbar.MaterialSearchBar
import com.rengwuxian.materialedittext.MaterialEditText
import com.sanit.pc.foodie4userver.Common.Common
import com.sanit.pc.foodie4userver.R.id.recycler_food_list
import com.sanit.pc.foodie4userver.ViewHolder.FoodListVIewHolder
import com.sanit.pc.foodie4userver.beans.Category
import com.sanit.pc.foodie4userver.beans.Food
import com.sanit.pc.foodie4userver.interfaces.ItemClickListner
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_food_list.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.add_food_list_item_layout.view.*
import java.util.*

class FoodList:AppCompatActivity(){

var categoryID:String?=null
lateinit  var  firebaseRecyclerAdapter: FirebaseRecyclerAdapter<Food, FoodListVIewHolder>
lateinit var searchAdapter: FirebaseRecyclerAdapter<Food, FoodListVIewHolder>

internal var suggestList: MutableList<String> = mutableListOf()

lateinit var food: DatabaseReference
     var newFood:Food? = null
    lateinit var saveUri:Uri

    lateinit var editFoodName: MaterialEditText
    lateinit var editFoodPrice:MaterialEditText
    lateinit var editFoodDesc:MaterialEditText
    lateinit var editFoodDiscount:MaterialEditText
    lateinit var btnSelect: Button
    lateinit var btnUpload: Button

    val PICK_iMAGE_REQUEST:Int  = 123


    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_food_list)

    //get intent data

    food = FirebaseDatabase.getInstance().getReference("Food")


    recycler_food_list.setHasFixedSize(true)
    if(intent!=null){
        categoryID = intent.extras!!.getString("CategoryID")

        if(categoryID!=null && !categoryID!!.isEmpty()){
            loadListFood(categoryID!!)
        }
    }

    loadSuggestions()//load suggestions from firebase


    searchBar.setCardViewElevation(10)
    searchBar.addTextChangeListener(object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            var suggession:MutableList<String> = mutableListOf()

            for(search in suggestList){
                if(search.toLowerCase().contains(searchBar.text.toLowerCase())){
                    suggession.add(search)
                }

            }
            searchBar.lastSuggestions = suggession
        }

    })//addTextChangeListener

    searchBar.setOnSearchActionListener(object: MaterialSearchBar.OnSearchActionListener{

        override fun onButtonClicked(buttonCode: Int) {

        }

        override fun onSearchStateChanged(enabled: Boolean) {
            if(!enabled){
                recycler_food_list.adapter = firebaseRecyclerAdapter
            }
        }

        override fun onSearchConfirmed(text: CharSequence?) {
            startSearch(text)

        }

    })//setOnSearchActionListener

    floating_add_food.setOnClickListener {
        showAlertDialog()
    }

}//onCreate

private fun startSearch(text: CharSequence?) {
    searchAdapter = object: FirebaseRecyclerAdapter<Food, FoodListVIewHolder>(
        Food::class.java,
        R.layout.food_list_item,
        FoodListVIewHolder::class.java,
        food.orderByChild("name").equalTo(text.toString())
    ){

        override fun populateViewHolder(viewHolder: FoodListVIewHolder?, model: Food?, position: Int) {
            viewHolder!!.textFoodName.text = model!!.name
            Picasso.with(baseContext).load(model.image).into(viewHolder.imgView)

            viewHolder.setItemClickListener(object : ItemClickListner {
                override fun onClick(view: View, pos: Int, isLongClick: Boolean) {
//                    val foodDetailIntent = Intent(this@FoodList, FoodDetail::class.java)
//                    foodDetailIntent.putExtra("FoodID", searchAdapter.getRef(pos).key)
                    Toast.makeText(this@FoodList,"clicked on"+model.name,Toast.LENGTH_SHORT).show()

                }

            })
        }

    }
    recycler_food_list.adapter = searchAdapter
}

private fun loadSuggestions() {
    food.orderByChild("menuId").equalTo(categoryID).addValueEventListener(object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onDataChange(dataSnap: DataSnapshot) {
            for(item in dataSnap!!.children){
                val eachItem = item.getValue(Food::class.java)

                suggestList.add(eachItem!!.name)


            }
            searchBar.lastSuggestions = suggestList


        }

    })
}

private fun loadListFood(categoryID: String) {

    val food = FirebaseDatabase.getInstance().getReference("Food")

    firebaseRecyclerAdapter = object: FirebaseRecyclerAdapter<Food, FoodListVIewHolder>(
        Food::class.java,
        R.layout.food_list_item,
        FoodListVIewHolder::class.java,
        food.orderByChild("menuId").equalTo(categoryID)
    ){
        override fun populateViewHolder(viewHolder: FoodListVIewHolder?, model: Food?, position: Int) {


            viewHolder!!.textFoodName.text = model!!.name
            Picasso.with(this@FoodList).load(model.image).into(viewHolder.imgView)

            val foodmodel = model

            viewHolder.setItemClickListener(object: ItemClickListner{
                override fun onClick(view: View, pos: Int, isLongClick: Boolean) {
//                        Toast.makeText(this@FoodList,"clicked on"+foodmodel.name,Toast.LENGTH_SHORT).show()

//                    val foodDetailIntent = Intent(this@FoodList,FoodDetail::class.java)
//                    foodDetailIntent.putExtra("FoodID",firebaseRecyclerAdapter.getRef(pos).key)
//                    startActivity(foodDetailIntent)
                    Toast.makeText(this@FoodList,"clicked on"+foodmodel.name,Toast.LENGTH_SHORT).show()



                }

            })

        }

    }//firebaseRecyclerAdapter

    recycler_food_list.adapter = firebaseRecyclerAdapter

}//loadListFood


    private fun showAlertDialog() {
        val alrt = AlertDialog.Builder(this)
        alrt.setTitle("Add New Category")
        alrt.setMessage("Please fill Full Information")

        val inflater = this.layoutInflater
        val add_menu_layout = inflater.inflate(R.layout.add_food_list_item_layout, null)

        editFoodName = add_menu_layout.findViewById(R.id.edit_food_name)
        editFoodPrice = add_menu_layout.findViewById(R.id.edit_food_price)
        editFoodDesc = add_menu_layout.findViewById(R.id.edit_food_desc)
        editFoodDiscount = add_menu_layout.findViewById(R.id.edit_food_discount)
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect)
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload)

        btnSelect.setOnClickListener {
            chooseImage()
        }

        btnUpload.setOnClickListener {
            UploadImage()
        }

        alrt.setView(add_menu_layout)
        alrt.setIcon(R.drawable.ic_shopping_cart_black_24dp)
        alrt.setPositiveButton("Yes") { dialog, which ->
            dialog.dismiss()
            if (!newFood!!.equals(null)) {
                food.push().setValue(newFood)
                Snackbar.make(foos_list_layout, "New category" + newFood!!.name + "was added", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
        alrt.setNegativeButton(
            "No"
        ) { dialog, which -> dialog.dismiss() }
        alrt.show()
    }

    private fun UploadImage() {
        if (saveUri != null) {
            val mdialog = ProgressDialog(this)
            mdialog.setMessage("Uploading...")
            mdialog.show()

            val imageName = UUID.randomUUID().toString()
            val imagefolder = FirebaseStorage.getInstance().getReference().child("images/$imageName")
            imagefolder.putFile(saveUri).addOnSuccessListener {
                mdialog.dismiss()
                imagefolder.downloadUrl.addOnSuccessListener {
                    newFood =
                            Food(
                                editFoodDesc.text.toString(),editFoodDiscount.text.toString(),it.toString(),categoryID!!,
                                editFoodName.text.toString(),editFoodPrice.text.toString()
                            )
                    Toast.makeText(this@FoodList, "Uploaded", Toast.LENGTH_LONG).show()
                }
                    .addOnFailureListener {
                        Toast.makeText(
                            this@FoodList,
                            "" + it.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }


            }
        }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_iMAGE_REQUEST)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_iMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            saveUri = data.data!!
            btnSelect.setText("Image Selected !")
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {

        if(item!!.title ==Common.UPDATE ){
            showUpdateDialog(firebaseRecyclerAdapter.getRef(item.order).key,firebaseRecyclerAdapter.getItem(item.order))
        }
        if(item.title ==Common.DELETE ){
            showDeleteDialog(firebaseRecyclerAdapter.getRef(item.order).key)
        }

        return super.onContextItemSelected(item)
    }

    private fun showDeleteDialog(key: String?) {
        food.child(key!!).removeValue()
    }

    private fun showUpdateDialog(key: String?, item: Food?) {
        val alrt = AlertDialog.Builder(this)
        alrt.setTitle("Add New Category")
        alrt.setMessage("Please fill Full Information")

        val inflater = this.layoutInflater
        val add_menu_layout = inflater.inflate(R.layout.add_food_list_item_layout, null)

        editFoodName = add_menu_layout.findViewById(R.id.edit_food_name)
        editFoodPrice = add_menu_layout.findViewById(R.id.edit_food_price)
        editFoodDesc = add_menu_layout.findViewById(R.id.edit_food_desc)
        editFoodDiscount = add_menu_layout.findViewById(R.id.edit_food_discount)
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect)
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload)

        editFoodName.setText(item!!.name)
        editFoodPrice.setText(item.price)
        editFoodDiscount.setText(item.discount)
        editFoodDesc.setText(item.description)

        btnSelect.setOnClickListener {
            chooseImage()
        }

        btnUpload.setOnClickListener {
            ChangeImage(item)
        }

        alrt.setView(add_menu_layout)
        alrt.setIcon(R.drawable.ic_shopping_cart_black_24dp)
        alrt.setPositiveButton("Yes") { dialog, which ->
            dialog.dismiss()
            item.description = editFoodDesc.text.toString()
            item.discount = editFoodDiscount.text.toString()
            item.price = editFoodPrice.text.toString()
            item.menuId = categoryID.toString()

            food.child(key!!).setValue(item)
                Snackbar.make(foos_list_layout, "New category" + item.name + "was added", Snackbar.LENGTH_SHORT)
                    .show()

        }
        alrt.setNegativeButton(
            "No"
        ) { dialog, which -> dialog.dismiss() }
        alrt.show()
    }

    private fun ChangeImage(item: Food) {
        if (saveUri != null) {
            val mdialog = ProgressDialog(this)
            mdialog.setMessage("Uploading...")
            mdialog.show()

            val imageName = UUID.randomUUID().toString()
            val imagefolder = FirebaseStorage.getInstance().getReference().child("images/$imageName")
            imagefolder.putFile(saveUri).addOnSuccessListener {
                mdialog.dismiss()
                imagefolder.downloadUrl.addOnSuccessListener {
                    item.image=it.toString()
                    Toast.makeText(this@FoodList, "Uploaded", Toast.LENGTH_LONG).show()
                }
                    .addOnFailureListener {
                        Toast.makeText(
                            this@FoodList,
                            "" + it.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }


            }
        }
    }


}


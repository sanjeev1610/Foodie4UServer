package com.sanit.pc.foodie4userver

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sanit.pc.foodie4userver.Common.Common
import com.sanit.pc.foodie4userver.ViewHolder.MenuViewHolder
import com.sanit.pc.foodie4userver.beans.Category
import com.sanit.pc.foodie4userver.beans.Token
import com.sanit.pc.foodie4userver.interfaces.ItemClickListner
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_home.*
import java.util.*

class Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<Category, MenuViewHolder>

    internal var saveUri: Uri? = null
    private val PICK_iMAGE_REQUEST = 71
     var newCategory: Category?=null
    lateinit var category: DatabaseReference

    lateinit var storageReference: StorageReference

    lateinit var editMenuName: TextView
    lateinit var btnSelect: Button
    lateinit var btnUpload: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        category = FirebaseDatabase.getInstance().getReference("Category")
        storageReference = FirebaseStorage.getInstance().getReference()


        toolbar.title = "Menu Management"
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            //            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            showAlertDialog()

        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val headerView1 = nav_view.getHeaderView(0)
        val textFullName = headerView1.findViewById<TextView>(R.id.nav_txtFullname)
        textFullName.text = Common.currentUser.username
        //loa
        loadMenu()

        //start Service
//        startService(Intent(this@Home,ListeningOrder::class.java))

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            val token = it.token
            updateToken(token)

        }

    }

    private fun updateToken(token:String) {

        val db = FirebaseDatabase.getInstance()
        val tokens = db.getReference("Tokens")
        val data = Token(token, "true")
        tokens.child(Common.currentUser.phone).setValue(data)
    }


    private fun loadMenu() {
        val category: DatabaseReference = FirebaseDatabase.getInstance().getReference("Category")
        firebaseRecyclerAdapter =
                object : FirebaseRecyclerAdapter<Category, MenuViewHolder>(
                    Category::class.java,
                    R.layout.menu_item,
                    MenuViewHolder::class.java,
                    category
                ) {

                    override fun populateViewHolder(vh: MenuViewHolder?, model: Category?, position: Int) {

                        vh!!.textMenuName.setText(model!!.name)
                        Picasso.with(this@Home).load(model.image).into(vh.imgView)

                        val clickItem = model

                        vh.setItemClickListener(object : ItemClickListner {
                            override fun onClick(view: View, pos: Int, isLongClick: Boolean) {
                                Toast.makeText(this@Home, "clicked on" + clickItem.name, Toast.LENGTH_SHORT).show()

                                //get the category key and navigate to FoodList activity
                                val foodListIntent = Intent(this@Home,FoodList::class.java)
                                foodListIntent.putExtra("CategoryID",firebaseRecyclerAdapter.getRef(pos).key)
                                startActivity(foodListIntent)

                            }

                        })

                    }//populateViewHolder

                }//firebaseRecyclerAdapter
        firebaseRecyclerAdapter.notifyDataSetChanged()
        recycler_menu.adapter = firebaseRecyclerAdapter

    }//load Menu


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        when (item.itemId) {
//            R.id.action_settings -> return true
//            else ->
        return super.onOptionsItemSelected(item)
        //       }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_menu -> {
                // Handle the camera action
            }
            R.id.nav_banner -> {

            }
            R.id.nav_message -> {

            }
            R.id.nav_orders -> {
                val orderIntent = Intent(this@Home,OrderStatus::class.java)
                startActivity(orderIntent)
            }
            R.id.nav_shipper -> {

            }
            R.id.nav_logout -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    //add menu by using fab button


    private fun showAlertDialog() {
        val alrt = AlertDialog.Builder(this)
        alrt.setTitle("Add New Category")
        alrt.setMessage("Please fill Full Information")

        val inflater = this.layoutInflater
        val add_menu_layout = inflater.inflate(R.layout.add_menu_item_layout, null)

        editMenuName = add_menu_layout.findViewById(R.id.edit_menu_name)
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
            if (newCategory != null) {
                category.push().setValue(newCategory)
                Snackbar.make(drawer_layout, "New category" + newCategory!!.name + "was added", Snackbar.LENGTH_SHORT)
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
            val imagefolder = storageReference.child("images/$imageName")
            imagefolder.putFile(saveUri!!).addOnSuccessListener {
                mdialog.dismiss()
                imagefolder.downloadUrl.addOnSuccessListener {
                    newCategory =
                            Category(editMenuName.getText()!!.toString(), it.toString())
                    Toast.makeText(this@Home, "Uploaded", Toast.LENGTH_LONG).show()
                }
                    .addOnFailureListener {
                        Toast.makeText(
                            this@Home,
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
                saveUri = data.data
                btnSelect.setText("Image Selected !")
            }
        }


    override fun onContextItemSelected(item: MenuItem?): Boolean {

        if(item!!.title == Common.UPDATE){
            showUpdateDialog(firebaseRecyclerAdapter.getRef(item.order).key, firebaseRecyclerAdapter.getItem(item.order))

            Toast.makeText(this@Home,"Updated",Toast.LENGTH_SHORT).show()

        }
        if(item.title == Common.DELETE){
            showDeleteDialog(firebaseRecyclerAdapter.getRef(item.order).key)

            Toast.makeText(this@Home,"Deleted",Toast.LENGTH_SHORT).show()
        }
        return super.onContextItemSelected(item)
    }

    private fun showDeleteDialog(key: String?) {


        val db = FirebaseDatabase.getInstance().getReference("Food")
        val foodIncategory = db.orderByChild("menuId").equalTo(key)
        foodIncategory.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        category.child(key!!).removeValue()

    }

    private fun showUpdateDialog(key: String?, item: Category?) {
        val alrt = AlertDialog.Builder(this)
        alrt.setTitle("Update Category")
        alrt.setMessage("Please fill Full Information")

        val inflater = this.layoutInflater
        val add_menu_layout = inflater.inflate(R.layout.add_menu_item_layout, null)

        editMenuName = add_menu_layout.findViewById(R.id.edit_menu_name)
        btnSelect = add_menu_layout.findViewById(R.id.btnSelect)
        btnUpload = add_menu_layout.findViewById(R.id.btnUpload)
        editMenuName.text = item!!.name

        btnSelect.setOnClickListener {
            chooseImage()
        }

        btnUpload.setOnClickListener {
            changeImage(item)
        }

        alrt.setView(add_menu_layout)
        alrt.setIcon(R.drawable.ic_shopping_cart_black_24dp)
        alrt.setPositiveButton("Yes") { dialog, which ->
            dialog.dismiss()

                item.name = editMenuName.text.toString()
                category.child(key!!).setValue(item)
                Snackbar.make(drawer_layout, "New updated category" + item.name + "was added", Snackbar.LENGTH_SHORT)
                    .show()

        }
        alrt.setNegativeButton(
            "No"
        ) { dialog, which -> dialog.dismiss() }
        alrt.show()
    }

    private fun changeImage(item: Category) {

        if (saveUri != null) {
            val mdialog = ProgressDialog(this)
            mdialog.setMessage("Uploading...")
            mdialog.show()

            val imageName = UUID.randomUUID().toString()
            val imagefolder = storageReference.child("images/$imageName")
            imagefolder.putFile(saveUri!!).addOnSuccessListener {
                mdialog.dismiss()
                imagefolder.downloadUrl.addOnSuccessListener {
                   item.image=it.toString()
                    Toast.makeText(this@Home, "Uploaded", Toast.LENGTH_LONG).show()
                }
                    .addOnFailureListener {
                        Toast.makeText(
                            this@Home,
                            "" + it.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }


            }
        }
    }




}





















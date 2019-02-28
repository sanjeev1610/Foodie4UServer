package com.sanit.pc.foodie4userver

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.*
import com.sanit.pc.foodie4userver.Common.Common
import com.sanit.pc.foodie4userver.beans.User
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    lateinit var  database: FirebaseDatabase
    lateinit var db_user: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        if(Common.isConnectedToInternet(this@SignInActivity)) {
            signIn.setOnClickListener {
                login()
            }
        }else{
            Toast.makeText(this@SignInActivity,"Your not connected to Internet", Toast.LENGTH_SHORT).show()

        }

    }

    private fun login() {
//        val pDialog = ProgressDialog(this@SigninActivity)
//        pDialog.setMessage("Please wait")
//        pDialog.show()
        database = FirebaseDatabase.getInstance()
        db_user = database.getReference("User")
        Toast.makeText(this@SignInActivity,"Helloooooo--1", Toast.LENGTH_SHORT).show()
        db_user.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(dataSnap: DatabaseError) {
//                pDialog.dismiss()
                Toast.makeText(this@SignInActivity,"Cancelled", Toast.LENGTH_SHORT).show()

            }

            override fun onDataChange(dataSnap: DataSnapshot) {
//                pDialog.dismiss()
                if(dataSnap.child(edit_phone.text.toString()).exists()){
                    Toast.makeText(this@SignInActivity,"phoneExists", Toast.LENGTH_SHORT).show()

                    val user: User = dataSnap.child(edit_phone.text.toString()).getValue(User::class.java)!!
                    if(user.isstaff.toBoolean()){
                        Toast.makeText(this@SignInActivity,"isStaff", Toast.LENGTH_SHORT).show()

                        if(user.password == edit_pwd.text.toString()){
                        Toast.makeText(this@SignInActivity,"Sign in Succussfully",Toast.LENGTH_SHORT).show()
                            val homeIntent = Intent(this@SignInActivity,Home::class.java)
                            user.phone = edit_phone.text.toString()
                            Common.currentUser = user

                            startActivity(homeIntent)
//                            finish()

                        }else{
                            Toast.makeText(this@SignInActivity,"Pass word Error", Toast.LENGTH_SHORT).show()

                        }
                    }else{
                        Toast.makeText(this@SignInActivity,"not a staff"+user, Toast.LENGTH_SHORT).show()

                    }

                }else{
                    Toast.makeText(this@SignInActivity,"Un Authorized User", Toast.LENGTH_SHORT).show()

                }
            }


        })
    }

}

package com.iitp.trakon.activities

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.iitp.trakon.R
import com.iitp.trakon.firebase.firestoreclass
import com.iitp.trakon.models.Users
import com.iitp.trakon.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_update_password.*
import kotlinx.android.synthetic.main.nav_header.*
import java.io.IOException

class UpdatePassword : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var toggle : ActionBarDrawerToggle
    lateinit var progressDialog : ProgressDialog
    private val mFireStore= FirebaseFirestore.getInstance()
    protected lateinit var  drawerLayout : DrawerLayout
    lateinit var islogin:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_password)
        val user = FirebaseAuth.getInstance().currentUser

        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        button_update.setOnClickListener {
           if(validateUpdatePassForm(new_pass.text.toString(), confirm_new_pass.text.toString()))
           {
               if(confirm_new_pass.text.toString()==new_pass.text.toString()) {

                   user!!.updatePassword(new_pass.text.toString()).addOnCompleteListener { task ->
                       if (task.isSuccessful) {
                           Toast.makeText(
                               this,
                               "Your Password Has Been Updated successfully",
                               Toast.LENGTH_SHORT
                           ).show()
                           startActivity(Intent(this,Tabs::class.java))
                           this.finish()
                       } else {
                           Toast.makeText(this,"Please Login again and then try !!", Toast.LENGTH_LONG).show()
                           Log.e("aryan",task.exception.toString())
                       }
                   }
               }
               else{
                   Log.d("aryan",
                       (confirm_new_pass.text.toString()==new_pass.text.toString()).toString()
                   )
                   Toast.makeText(this,"New Password and Confirm Password should be same",Toast.LENGTH_SHORT).show()
               }
           }
           }

        firestoreclass().signInUser(this)
        fun showProgressDialog(text:String)
        {   progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Updating Password")
            progressDialog.setMessage("Please wait...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        drawerLayout = findViewById(R.id.navigationBar)
        val navView : NavigationView = findViewById(R.id.navView)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        toggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)

        toggle.syncState()
        val icon = toolbar.navigationIcon
        icon?.setVisible(false,false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
       //
        var putha:Users
        mFireStore.collection(Constants.USERS)
            .document(getcurrentUserID()).get().addOnSuccessListener { document ->
                putha = document.toObject(Users::class.java)!!
                name_display.text=putha.name;
                roll_display.text=putha.roll;
                InstituteEmail.text=putha.email
                try {
                    Glide     //using Glide to display image from url
                        .with(this)
                        .load(putha.image)
                        .centerCrop()
                        .placeholder(R.drawable.ic_baseline_person_24)
                        .into(Profile_pic_image);
                }catch (e: IOException){
                    e.printStackTrace()
                }

            }

       //



        navView.setNavigationItemSelectedListener {

            when(it.itemId)
            {
                R.id.check_found_item-> startActivity(Intent(this,Tabs::class.java))
                R.id.update_button_butoon-> startActivity(Intent(this,UpdatePassword::class.java))
                R.id.my_found_post_button-> startActivity(Intent(this,TabsFillItem::class.java))
                R.id.archive_item-> startActivity(Intent(this,ArchieveTab::class.java))
                R.id.logout_profile-> {
                    var sharedPreferences: SharedPreferences =getSharedPreferences("logindata", MODE_PRIVATE);
                    sharedPreferences.edit().clear().commit();
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this,MainActivity::class.java))
//                islogin="0"
                }
                R.id.query_section->{
                    var putha:Users
                    mFireStore.collection(Constants.USERS)
                        .document(getcurrentUserID()).get().addOnSuccessListener { document ->
                            putha = document.toObject(Users::class.java)!!
                            val intentToQuery = Intent(this,qurery::class.java)
                            intentToQuery.putExtra("nameExisting",putha.name)
                            intentToQuery.putExtra("phoneExisting",putha.mobile)
                            intentToQuery.putExtra("emailExisting",putha.email)
                            startActivity(intentToQuery)
                        }

                }
                R.id.privacy_policy->{
                    startActivity(Intent(this,PrivacyPolicy::class.java))
                }
                R.id.my_update_profile-> alpha()
            }
            drawerLayout.closeDrawers()
            true

        }




    }
    fun alpha() {
        var loggedInUser: Users
        var beta: String
        mFireStore.collection(Constants.USERS)
            .document(getcurrentUserID()).get().addOnSuccessListener { document ->
                loggedInUser = document.toObject(Users::class.java)!!
                beta = document.id

                setUpdate(beta, loggedInUser)
                setDetails(loggedInUser)
            }
    }

    fun setUpdate(uid:String,user: Users)
    {

        val name=user.name
        val phone=user.mobile
        val whatsapp=user.whatsapp
        val image=user.image
        val roll=user.roll

        val intent = Intent(this, Updateprofile::class.java)
        //listener?.onClick(AlbumsData)
        intent.putExtra("name", name)
        intent.putExtra("phone", phone)
        intent.putExtra("roll", roll)
        intent.putExtra("whatsapp", whatsapp)
        intent.putExtra("image", image)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun setDetails(user: Users)
    {

        name_display.text=user.name;
        roll_display.text=user.roll;
        InstituteEmail.text=user.email
        try {
            Glide     //using Glide to display image from url
                .with(this)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_person_24)
                .into(Profile_pic_image);
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

    private fun getcurrentUserID():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed(){
        startActivity(Intent(this,Tabs::class.java))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
             R.id.check_found_item-> startActivity(Intent(this,FoundActivity::class.java))
            R.id.update_button_butoon-> startActivity(Intent(this,UpdatePassword::class.java))
            R.id.my_found_post_button-> startActivity(Intent(this,MyFoundActivity::class.java))
            R.id.logout_profile-> {
                var sharedPreferences:SharedPreferences=getSharedPreferences("logindata", MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this,MainActivity::class.java))
//                islogin="0"
            }
            R.id.query_section->{
                var putha:Users
                mFireStore.collection(Constants.USERS)
                    .document(getcurrentUserID()).get().addOnSuccessListener { document ->
                        putha = document.toObject(Users::class.java)!!
                        val intentToQuery = Intent(this,qurery::class.java)
                        intentToQuery.putExtra("nameExisting",putha.name)
                        intentToQuery.putExtra("phoneExisting",putha.mobile)
                        intentToQuery.putExtra("emailExisting",putha.email)
                        startActivity(intentToQuery)
                    }

            }
            R.id.privacy_policy->{
                startActivity(Intent(this,PrivacyPolicy::class.java))
            }
            R.id.my_update_profile-> alpha()
        }
        drawerLayout.closeDrawers()
        return true
    }

    private fun validateUpdatePassForm(
       newpass: String,
       confirmnewpass: String
    ): Boolean {
        return when {//checking if the field are empty
            TextUtils.isEmpty(newpass.trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "New Password should be of atleast 6 characters.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            TextUtils.isEmpty(confirmnewpass.trim { it <= ' ' }) -> {
                Toast.makeText(
                    this,
                    "Confirm and New password should be same.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            else -> {
                true
            }
        }
    }



}
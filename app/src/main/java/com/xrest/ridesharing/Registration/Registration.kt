package com.xrest.ridesharing.Registration

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import com.xrest.ridesharing.LOGIN.Login
import com.xrest.ridesharing.R
import com.xrest.ridesharing.Repository.UserRepository
import com.xrest.ridesharing.model.Question
import com.xrest.ridesharing.model.RegisterModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

private lateinit var Fullname: TextInputEditText
private lateinit var PhoneNumber: TextInputEditText
private lateinit var DateOfBirth: TextInputEditText
private lateinit var Password: TextInputEditText
private lateinit var registerSpinner: Spinner
var question = mutableListOf("What is lucky number ?"
    ,"What is your favourite food ?"
    ,"Where is your favourite place to visit?",
    "What is your favourite Drink ?",
    "Your Birth Month?")
private var selected_usertype = ""

private lateinit var SignUp: Button

class Registration : AppCompatActivity(), View.OnClickListener {

    private val userType= arrayOf("User", "Rider")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        Fullname= findViewById(R.id.etFullname1)
        PhoneNumber = findViewById(R.id.etPhonenumber1)
        DateOfBirth= findViewById(R.id.etDateOfBirth1)
        Password = findViewById(R.id.etPassword)
        registerSpinner= findViewById(R.id.registerSpinner)
        SignUp = findViewById(R.id.btnSignup1)

        SignUp.setOnClickListener(this)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userType)

        registerSpinner.adapter = adapter

        registerSpinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selected_usertype = parent?.getItemAtPosition(position).toString()
                        Toast.makeText(applicationContext, "${selected_usertype}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }

                }

    }

    var qa:MutableList<Question> = mutableListOf()
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btnSignup1 ->{
              dialog(qa)
            }
        }
    }

    private fun signUp() {
        try {

            val FullName = Fullname.text.toString()
            val PhoneNumber = PhoneNumber.text.toString()
            val DateOfBirth = DateOfBirth.text.toString()
            val Password = Password.text.toString()

            val userRegister = RegisterModel(FullName = "$FullName",PhoneNumber =  "$PhoneNumber", DateOfBirth = "${DateOfBirth}",
                   Password =  "$Password", UserType = "$selected_usertype", Latitude = "", Longitude = "",Questions = qa)

            CoroutineScope(Dispatchers.IO).launch {
                val repository = UserRepository()
                val response = repository.registerUser(userRegister)

                if(response.success== true){
                    CoroutineScope(Dispatchers.Main).launch{
                        val intent = Intent(this@Registration, Login::class.java)
                        startActivity(intent)
                    }

                }
            }

        }
        catch (ex:Exception){
            CoroutineScope(Dispatchers.Main).launch{
                Toast.makeText(
                        this@Registration,
                        ex.toString(), Toast.LENGTH_SHORT
                ).show()
            }
        }

    }
    override fun onResume() {
        super.onResume()
        supportActionBar!!.hide()
    }
    var count =0
    private fun dialog(qa: MutableList<Question>) :Boolean{
        var q = ""
        var p = 0
        var result  = false
        var dialog = Dialog(this)
        dialog.setContentView(R.layout.questions)
        dialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        var spinner = dialog.findViewById<Spinner>(R.id.spinner)
        var answer = dialog.findViewById<EditText>(R.id.answer)
        var next = dialog.findViewById<Button>(R.id.next)
        spinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, question)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                q = parent!!.getItemAtPosition(position).toString()
                p = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.show()
        dialog.setCancelable(true)
        dialog.window!!.setGravity(Gravity.CENTER)

        next.setOnClickListener() {
            qa.add(Question(q, answer.text.toString()))
            question.removeAt(p)
            dialog.cancel()
            count +=1
            Log.d("coubtsasdasdas",count.toString())
            if (count == 3)
            {
                signUp()
                result =true
            }else{
                dialog(qa)
                result =false
            }
        }
        return result
    }
//    private fun showPopup() {
//        val popUp = PopupMenu(this,profile)
//        popUp.menuInflater.inflate(R.menu.camera_gallery,popUp.menu)
//        try{
//            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
//            popup.isAccessible = true
//            val menu = popup.get(popUp)
//            menu.javaClass
//                .getDeclaredMethod("setForceShowIcon",Boolean::class.java)
//                .invoke(menu,true)
//        }
//        catch (ex:Exception)
//        { ex.printStackTrace()
//        } finally {
//            popUp.show()
//            popUp.setOnMenuItemClickListener {
//                when(it.itemId)
//                {
//                    R.id.camera ->{
//
//                        var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                        startActivityForResult(intent,1)
//
//                    }
//                    R.id.gallery ->{
//                        var intent = Intent(Intent.ACTION_PICK)
//                        intent.type="image/*"
//                        startActivityForResult(intent,0)
//                    }
//                }
//                true
//            }
//
//        }
//
//
//    }
}
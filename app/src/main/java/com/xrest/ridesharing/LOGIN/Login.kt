package com.xrest.ridesharing.LOGIN

import android.app.Activity
import android.app.AlertDialog
import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.xrest.ridesharing.API.RetrofitService
import com.xrest.ridesharing.AppActivity
import com.xrest.ridesharing.R
import com.xrest.ridesharing.Registration.Registration
import com.xrest.ridesharing.Repository.UserRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor

private lateinit var etPhonenumber: TextInputEditText
private lateinit var etLoginPassword: TextInputEditText

lateinit var bio :FloatingActionButton

private lateinit var btnLogin: TextView
private lateinit var btnSignup: FloatingActionButton
lateinit var executor: Executor
lateinit var biometricPrompt: BiometricPrompt
class Login : AppCompatActivity(), View.OnClickListener {
    val cb: BiometricPrompt.AuthenticationCallback
        get() = @RequiresApi(Build.VERSION_CODES.P)
        object : BiometricPrompt.AuthenticationCallback(){

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)

                var preferences = getSharedPreferences("userLogin", Activity.MODE_PRIVATE)
                login(preferences.getString("username","")!!,preferences.getString("password","")!!)

            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(this@Login, "Invalid Finger Print Detected", Toast.LENGTH_LONG).show()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(this@Login, "Sensor Might be blocked Plese clear and try again", Toast.LENGTH_LONG).show()

            }


        }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        etPhonenumber =findViewById(R.id.username)
        etLoginPassword = findViewById(R.id.password)
        btnLogin = findViewById(R.id.sign)
        btnSignup = findViewById(R.id.fb)
        checkPermission()
        btnLogin.setOnClickListener(this)
        btnSignup.setOnClickListener(this)
        bio = findViewById(R.id.bio)
        var preferences = getSharedPreferences("Login",Activity.MODE_PRIVATE)
        if(preferences.getBoolean("biometric",false)==true)
        {
            var cancellation = CancellationSignal()
            cancellation.setOnCancelListener(){}
            executor = mainExecutor!!
            bio.isVisible =true
            findViewById<LinearLayout>(R.id.bb).isVisible = true

            biometricPrompt = BiometricPrompt.Builder(this).setTitle("FingerPrint Login").setNegativeButton("Cancel",executor,
                DialogInterface.OnClickListener(){ dialog, _ ->
                dialog.cancel()
            }).setSubtitle("Place Your Finger To Login").build()
            bio.setOnClickListener(this)
        }
    }
    fun checkPermission(){
        var permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.CAMERA,android.Manifest.permission.ACCESS_NETWORK_STATE)

        for (permission in permissions)
        {
            if(ActivityCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, permissions,1)

            }
        }

    }

    override fun onResume() {
        super.onResume()
        supportActionBar!!.hide()
    }
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.sign->{
                val Phonenumber = etPhonenumber.text.toString()
                val password = etLoginPassword.text.toString()
                login(Phonenumber,password)
            }

            R.id.fb->{
                val intent = Intent(this, Registration::class.java)
                startActivity(intent)
            }
            R.id.bio ->{
                var cancellation = CancellationSignal()
                cancellation.setOnCancelListener(){}
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    biometricPrompt.authenticate(cancellation,executor,cb)
                }
            }
        }
    }


    fun  checkBioMetricFeature(){

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M)
        {
            var keyGuard = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            if(keyGuard.isKeyguardSecure && packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {

                if (!getSharedPreferences("Login", Activity.MODE_PRIVATE)
                        .getBoolean("biometric", false)
                ) {
                    var alert = AlertDialog.Builder(this)
                    alert.setTitle("Do you  want to add fingerprint login feature?")
                        .setMessage("It might be very easy for next time").setPositiveButton(
                            "Yes",
                            DialogInterface.OnClickListener() { _, _ ->
                                var intent = Intent(this, AppActivity::class.java)
                                startActivity(intent)
                                var editor = getSharedPreferences(
                                    "Login",
                                    Activity.MODE_PRIVATE
                                )
                                editor.edit().let { editor ->
                                    editor.putString("username", etPhonenumber.text.toString())
                                    editor.putString("password", etPhonenumber.text.toString())
                                    editor.putBoolean("biometric", true)
                                    editor.apply()
                                    editor.commit()
                                }

                            }).setNegativeButton(
                            "No",
                            DialogInterface.OnClickListener() { dialog, which ->
                                var intent = Intent(this, AppActivity::class.java)
                                startActivity(intent)
                                dialog.cancel()
                            })

                    var dialog = alert.create()
                    dialog.setIcon(R.drawable.ic_person)
                    dialog.setCancelable(false)
                    dialog.show()

                }
                else{
                    var intent = Intent(this, AppActivity::class.java)
                    startActivity(intent)
                }
            }
            else{
                var intent = Intent(this, AppActivity::class.java)
                startActivity(intent)
            }

        }
        else{
            var intent = Intent(this, AppActivity::class.java)
            startActivity(intent)
        }

        Toast.makeText(this, "Pass", Toast.LENGTH_SHORT).show()

    }

    private fun login(Phonenumber:String,password:String) {
        CoroutineScope(Dispatchers.IO).launch {
            val repository =UserRepository()
            val response = repository.loginUser(Phonenumber, password)
            if(response.success==true){

                RetrofitService.user = response.data
                RetrofitService.token = "Bearer " + response.token
                    withContext(Main){
                    val sharePref =
                            getSharedPreferences("userLogin", MODE_PRIVATE)
                    val editor = sharePref.edit()
                    editor.putString("username", Phonenumber)
                    editor.putString("password", password)
                    editor.apply()
                            //Toast.makeText(this@Login, "${response.data}", Toast.LENGTH_SHORT).show()
                   checkBioMetricFeature()
                }
            }
        }

    }



}
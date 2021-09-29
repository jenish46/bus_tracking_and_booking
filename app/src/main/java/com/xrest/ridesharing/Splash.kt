package com.xrest.ridesharing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.xrest.ridesharing.API.RetrofitService
import com.xrest.ridesharing.LOGIN.Login
import com.xrest.ridesharing.Repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Splash : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_splash, container, false)

        Handler().postDelayed({
            if (!requireContext().getSharedPreferences("onBoarding", Activity.MODE_PRIVATE)
                    .getBoolean("Boarding", false)
            ) {
                changeFragment(StartActions())

            } else {
                startActivity(Intent(requireContext(), Login::class.java))
                    var pref = requireContext().getSharedPreferences("userLogin",Activity.MODE_PRIVATE)
                    CoroutineScope(Dispatchers.IO).launch {

                        var response =
                            UserRepository().loginUser(pref.getString("username","")!!,pref.getString("password","")!!)
                        if (response.success == true) {
                            withContext(Dispatchers.Main)
                            {

                                RetrofitService.token ="Bearer "+ response.token!!
                                requireContext().startActivity(Intent(requireContext(), MainActivity::class.java))

                            }
                        } else {
                            withContext(Main)
                            {

                                requireContext().startActivity(Intent(requireContext(), Login::class.java))

                            }
                        }


                    }

            }
        }, 3000)


        return view

    }


    fun checkNetworkConnection(): Boolean {
        var result: Boolean = true
        var connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var network = connectivityManager.activeNetwork ?: return false
            var networkCpabalities =
                connectivityManager.getNetworkCapabilities(network) ?: return false
            result = when {
                networkCpabalities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                networkCpabalities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                else -> false
            }
        } else {
            connectivityManager.run {

                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        else -> false
                    }
                }

            }

        }



        return result
    }

    override fun onResume() {
        //chalni code
        super.onResume()
        val supportActionBar: ActionBar? = (requireActivity() as AppCompatActivity).supportActionBar
        if (supportActionBar != null) supportActionBar.hide()

    }

    override fun onStop() {
        super.onStop()
        val supportActionBar: ActionBar? = (requireActivity() as AppCompatActivity).supportActionBar
        if (supportActionBar != null) supportActionBar.show()
    }

    fun changeFragment(fragment: Fragment) {
        (requireContext() as AppCompatActivity).supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl, fragment)
            commit()
            addToBackStack(null)
        }
    }
}
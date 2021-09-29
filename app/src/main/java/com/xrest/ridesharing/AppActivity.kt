package com.xrest.ridesharing

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.xrest.ridesharing.fragments.HomeFragment
import com.xrest.ridesharing.fragments.ProfileFragment
import com.xrest.ridesharing.fragments.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AppActivity : AppCompatActivity() {


    private lateinit var BottomNavigationView:BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        BottomNavigationView = findViewById(R.id.bottomNavigationView)

        val homeFragment = HomeFragment()
        val profileFragment = ProfileFragment()
        val thirdFragment = SearchFragment()



        BottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.Home -> makeCurrentfragment(homeFragment)
                R.id.Profile -> makeCurrentfragment(profileFragment)
                R.id.Settings -> makeCurrentfragment(thirdFragment)
            }
            return@setOnNavigationItemSelectedListener true
        }

        makeCurrentfragment(homeFragment)






    }

    private fun makeCurrentfragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply{
            replace(R.id.frameLayoutView, fragment)
            commit()
        }
    }



}
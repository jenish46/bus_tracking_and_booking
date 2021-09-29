package com.xrest.ridesharing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.xrest.ridesharing.Adapter.ViewPagerAdapter


class StartActions : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=  inflater.inflate(R.layout.fragment_start_actions, container, false)
        var vp:ViewPager2 = view.findViewById(R.id.vp)
        vp.adapter = ViewPagerAdapter(mutableListOf(PageOne(), PageTwo(), PageThree()),requireFragmentManager(),lifecycle)

        return view
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar!!.hide()
    }

}
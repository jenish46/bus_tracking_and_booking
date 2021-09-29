package com.xrest.ridesharing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2


class PageOne : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view =inflater.inflate(R.layout.fragment_page_one, container, false)
        view.findViewById<TextView>(R.id.fb).setOnClickListener(){
            requireActivity().findViewById<ViewPager2>(R.id.vp).currentItem = 1
        }
        return view
    }



}
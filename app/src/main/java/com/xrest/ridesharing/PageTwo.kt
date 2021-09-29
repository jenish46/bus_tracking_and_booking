package com.xrest.ridesharing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2


class PageTwo : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_page_two, container, false)
        view.findViewById<TextView>(R.id.next).setOnClickListener(){
            requireActivity().findViewById<ViewPager2>(R.id.vp).currentItem = 2
        }
        return view
    }

}
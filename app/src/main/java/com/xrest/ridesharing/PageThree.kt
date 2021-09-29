package com.xrest.ridesharing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.xrest.ridesharing.LOGIN.Login


class PageThree : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view=  inflater.inflate(R.layout.fragment_page_three, container, false)
        view.findViewById<TextView>(R.id.finish).setOnClickListener(){
            var editor = requireActivity().getSharedPreferences("onBoarding",Activity.MODE_PRIVATE).edit()
            editor.putBoolean("Boarding",true)
            editor.apply()
            editor.commit()
            startActivity(Intent(requireContext(), Login::class.java))

        }
        return view
    }


}
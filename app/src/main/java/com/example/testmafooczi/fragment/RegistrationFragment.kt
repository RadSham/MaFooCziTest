package com.example.testmafooczi.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.testmafooczi.databinding.FragmentRegistrationBinding


class RegistrationFragment(private val fragCloseInterface: FragmentCloseInterface) : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val message = bundle!!.getString("mText")
        binding.tv.text = message
        println(message)
    }

    override fun onDestroy() {
        super.onDestroy()
        fragCloseInterface.onFragClose()
    }

}
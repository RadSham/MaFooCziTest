package com.example.testmafooczi.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.testmafooczi.databinding.FragmentEditProfileBinding
import com.example.testmafooczi.retrofit.MainApi

class EditProfileFragment(private val fragCloseInterface: FragmentCloseInterface) : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var mainApi: MainApi
    private var accessToken: String? = null
    private var refreshToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun init() {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        fragCloseInterface.onFragClose()
    }

}
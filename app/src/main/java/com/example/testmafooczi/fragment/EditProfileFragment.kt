package com.example.testmafooczi.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.testmafooczi.R
import com.example.testmafooczi.activity.ProfileActivity.Companion.checkZodiac
import com.example.testmafooczi.databinding.FragmentEditProfileBinding
import com.example.testmafooczi.retrofit.InitRetrofit
import com.example.testmafooczi.retrofit.MainApi
import com.example.testmafooczi.retrofit.ProfileUser
import kotlinx.serialization.json.Json

class EditProfileFragment(private val fragCloseInterface: FragmentCloseInterface) : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var mainApi: MainApi
    private var accessToken: String? = null
    private var refreshToken: String? = null
    private lateinit var profileUser: ProfileUser

    var message: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgumentsFromActivity()
    }

    private fun getArgumentsFromActivity() {
        val bundle = arguments
        message = bundle!!.getString("profileUser")
        profileUser = message?.let { Json.decodeFromString<ProfileUser>(it) }!!
        accessToken = bundle.getString("accessToken")
        refreshToken = bundle.getString("refreshToken")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvEditProfilePhone.text = profileUser.profile_data.phone
        binding.tvEditProfileNickname.text = profileUser.profile_data.username
        if (profileUser.profile_data.city != "null")
            binding.edEditProfileCity.setText(profileUser.profile_data.city)
        if (profileUser.profile_data.birthday != "null")
            binding.edProfileBirthDate.setText(profileUser.profile_data.birthday)
        if (profileUser.profile_data.birthday != "null")
            binding.edProfileZodiacSign.setText(profileUser.profile_data.birthday?.let {
                checkZodiac(
                    it
                )
            })
        binding.edProfileAbout.setText(
            getString(
                R.string.name_message,
                profileUser.profile_data.name
            )
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        fragCloseInterface.onFragClose()
    }

    private fun init() {
        initButtons()
        initRetrofit()
    }

    private fun initButtons() {
        //TODO: put edits
    }

    private fun initRetrofit() {
        val inRet = InitRetrofit()
        mainApi = inRet.initRetrofitWithAccessToken(accessToken)
    }

}
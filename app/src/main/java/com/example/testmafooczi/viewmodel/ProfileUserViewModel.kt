package com.example.testmafooczi.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.testmafooczi.retrofit.ProfileUser

class ProfileUserViewModel : ViewModel() {
    val mutableProfileUser = MutableLiveData<ProfileUser>()

    fun setProfileUser(profileUser: ProfileUser) {
        println("Here " + profileUser.profile_data.city)
        mutableProfileUser.value = profileUser
    }

}
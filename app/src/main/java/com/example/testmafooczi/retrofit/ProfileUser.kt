package com.example.testmafooczi.retrofit

data class ProfileUser(
    val profile_data: ProfileData
)

data class ProfileData(
    val name: String,
    val username: String,
    val birthday: String,
    val city: String,
    val vk: String,
    val instagram: String,
    val status: String,
    val avatar: String,
    val id: Int,
    val last: String,
    val online: Boolean,
    val created: String,
    val phone: String,
    val completed_task: Int,
    val avatars: Avatars
)

data class Avatars(
    val avatar: String,
    val bigAvatar: String,
    val miniAvatar: String
)
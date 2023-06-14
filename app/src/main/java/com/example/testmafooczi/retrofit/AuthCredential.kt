package com.example.testmafooczi.retrofit

data class AuthCredential(
    val refresh_token: String = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    val access_token : String,
    val user_id: Int,
    val is_user_exists : Boolean
)

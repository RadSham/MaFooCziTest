package com.example.testmafooczi.retrofit

import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MainApi{

    @POST("api/v1/users/send-auth-code/")
    suspend fun sendAuthCode(@Body phone: Phone): Response<JSONObject>

}
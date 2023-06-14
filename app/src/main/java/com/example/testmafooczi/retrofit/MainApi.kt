package com.example.testmafooczi.retrofit

import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MainApi{

    @POST("api/v1/users/send-auth-code/")
    suspend fun sendAuthPhone(@Body phone: Phone): Response<JSONObject>

    @POST("/api/v1/users/check-auth-code/")
    suspend fun sendAuthCode(@Body loginInformation: LoginInformation): Response<AuthCredential>

}
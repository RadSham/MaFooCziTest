package com.example.testmafooczi.retrofit

import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface MainApi {

    @POST("api/v1/users/send-auth-code/")
    suspend fun sendAuthPhone(@Body phone: Phone): Response<JSONObject>

    @POST("/api/v1/users/check-auth-code/")
    suspend fun sendAuthCode(@Body loginInformation: LoginInformation): Response<AuthCredential>

    @POST("/api/v1/users/register/")
    suspend fun registerUser(@Body registerUser: RegisterUser): Response<RegisteredCredentials>

    @GET("/api/v1/users/me/")
    suspend fun getCurrentUser(): ProfileUser

    @PUT("/api/v1/users/me/")
    suspend fun updateUser(@Body updateUser: UpdateUser) : Response<Avatars>
}
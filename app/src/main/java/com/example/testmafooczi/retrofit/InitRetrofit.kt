package com.example.testmafooczi.retrofit

import com.example.testmafooczi.MainActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InitRetrofit {

    fun initRetrofit(): MainApi {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(MainActivity.MAIN_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(MainApi::class.java)
    }

    fun initRetrofitWithAccessToken(accessToken: String?): MainApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(OAuthInterceptor("Bearer", accessToken.toString())).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(MainActivity.MAIN_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(MainApi::class.java)
    }

    class OAuthInterceptor(private val tokenType: String, private val accessToken: String) :
        Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            var request = chain.request()
            request =
                request.newBuilder().header("Authorization", "$tokenType $accessToken").build()
            return chain.proceed(request)
        }
    }
}
package com.example.testmafooczi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.testmafooczi.databinding.ActivityMainBinding
import com.example.testmafooczi.retrofit.MainApi
import com.example.testmafooczi.retrofit.Phone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainApi: MainApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
    }

    private fun init() {
        initEditTextPhone()
        initRetrofit()
    }

    private fun initEditTextPhone() {
        binding.buttonAuth.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val response = mainApi.sendAuthCode(
                        Phone("${binding.countryPicker.selectedCountryCode}${binding.etPhoneAuth.text}")
                )
                runOnUiThread{
                    Toast.makeText(this@MainActivity, response.isSuccessful.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun initRetrofit() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(MAIN_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mainApi = retrofit.create(MainApi::class.java)
    }

    companion object {
        const val MAIN_URL = "https://plannerok.ru/"
    }
}

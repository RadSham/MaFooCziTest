package com.example.testmafooczi

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testmafooczi.databinding.ActivityMainBinding
import com.example.testmafooczi.retrofit.LoginInformation
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
    private var responsePhoneBoolean = false
    private lateinit var authPhone: Phone

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
    }

    private fun init() {
        initButtons()
        initRetrofit()
    }

    private fun initButtons() {
        binding.buttonSendPhone.setOnClickListener {
            authPhone =
                Phone("${binding.countryPicker.selectedCountryCode}${binding.etPhoneAuth.text}")
            CoroutineScope(Dispatchers.IO).launch {
                val responsePhone = mainApi.sendAuthPhone(authPhone)
                responsePhoneBoolean = responsePhone.isSuccessful
                println(responsePhoneBoolean)
                runOnUiThread {
                    binding.etAuthCode.visibility = View.VISIBLE
                    binding.buttonSendCode.visibility = View.VISIBLE
                    binding.buttonSendPhone.visibility = View.GONE
                    Toast.makeText(
                        this@MainActivity,
                        responsePhone.isSuccessful.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        binding.buttonSendCode.setOnClickListener {
            val loginInfo =
                LoginInformation(authPhone.phone, binding.etAuthCode.text.toString())
            CoroutineScope(Dispatchers.IO).launch {
                val responsePhoneCode = mainApi.sendAuthCode(loginInfo)
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        responsePhoneCode.isSuccessful.toString(),
                        Toast.LENGTH_LONG
                    ).show()
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

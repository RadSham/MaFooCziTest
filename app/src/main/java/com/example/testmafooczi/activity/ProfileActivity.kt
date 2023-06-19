package com.example.testmafooczi.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.testmafooczi.MainActivity
import com.example.testmafooczi.R
import com.example.testmafooczi.databinding.ActivityProfileBinding
import com.example.testmafooczi.fragment.EditProfileFragment
import com.example.testmafooczi.fragment.FragmentCloseInterface
import com.example.testmafooczi.retrofit.MainApi
import com.example.testmafooczi.retrofit.ProfileUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate

class ProfileActivity : AppCompatActivity(), FragmentCloseInterface {

    private lateinit var binding: ActivityProfileBinding
    private var accessToken: String? = null
    private var refreshToken: String? = null
    private lateinit var mainApi: MainApi
    private lateinit var profileUser: ProfileUser


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
        getUser()
        initButton()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getUser() {
        CoroutineScope(Dispatchers.IO).launch {
            //TODO: check access token for validity
            profileUser = mainApi.getCurrentUser()
            println(profileUser.toString())
            runOnUiThread {
                initProfile()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun init() {
        initTokens()
        initRetrofit()
    }

    private fun initTokens() {
        val intent = intent
        accessToken = intent.getStringExtra("accessToken")
        refreshToken = intent.getStringExtra("refreshToken")
    }

    private fun initRetrofit() {
        val client = OkHttpClient.Builder()
            .addInterceptor(OAuthInterceptor("Bearer", accessToken.toString())).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(MainActivity.MAIN_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        mainApi = retrofit.create(MainApi::class.java)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initProfile() = with(binding) {
//        imAvatar.setImageURI(profileUser.profile_data.avatar.toUri())
        tvProfilePhone.text = profileUser.profile_data.phone
        tvNickname.text = profileUser.profile_data.username
        tvCity.text = profileUser.profile_data.city
        tvBirthDate.text = profileUser.profile_data.birthday
        tvZodiacSign.text = checkZodiac(profileUser.profile_data.birthday)
        tvAbout.text = getString(R.string.name_message, profileUser.profile_data.name)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkZodiac(tDate: String): String {
        try {
            val date = LocalDate.parse(tDate)
            val month = date.month
            val day = date.dayOfMonth
            var sign = ""
            when (month.name) {
                "January" -> {
                    sign = if (day < 20)
                        "Capricorn"
                    else
                        "Aquarius"
                }
                "February" -> {
                    sign = if (day < 19)
                        "Aquarius"
                    else
                        "Pisces"
                }
                "March" -> {
                    sign = if (day < 21)
                        "Pisces"
                    else
                        "Aries"
                }
                "April" -> {
                    sign = if (day < 20)
                        "Aries"
                    else
                        "Taurus"
                }
                "May" -> {
                    sign = if (day < 21)
                        "Taurus"
                    else
                        "Gemini"
                }
                "June" -> {
                    sign = if (day < 21)
                        "Gemini"
                    else
                        "Cancer"
                }
                "July" -> {
                    sign = if (day < 23)
                        "Cancer"
                    else
                        "Leo"
                }
                "August" -> {
                    sign = if (day < 23)
                        "Leo"
                    else
                        "Virgo"
                }
                "September" -> {
                    sign = if (day < 23)
                        "Virgo"
                    else
                        "Libra"
                }
                "October" -> {
                    sign = if (day < 23)
                        "Libra"
                    else
                        "Scorpio"
                }
                "November" -> {
                    sign = if (day < 22)
                        "scorpio"
                    else
                        "Sagittarius"
                }
                "December" -> {
                    sign = if (day < 22)
                        "Sagittarius"
                    else
                        "Capricorn"
                }
            }
            return sign
        } catch (_:NullPointerException){
            return "Null"
        }
    }

    private fun initButton(){
        binding.btEditProfile.setOnClickListener {
            toEditFragment(profileUser)
        }
    }

    private fun toEditFragment(profileUser: ProfileUser) {
        val mFragmentManager = supportFragmentManager
        val mFragmentTransaction = mFragmentManager.beginTransaction()
        val mFragment = EditProfileFragment(this)

        //hide buttons and Edit Views
        binding.clEditProfile.visibility = View.GONE

        // On button click, a bundle is initialized and the
        // text from the EditText is passed in the custom
        // fragment using this bundle

        val mBundle = Bundle()
        mBundle.putString("mText", profileUser.toString())
        mFragment.arguments = mBundle
        mFragmentTransaction.replace(R.id.flEditProfile, mFragment).addToBackStack(null).commit()
    }

    override fun onFragClose() {
        binding.clEditProfile.visibility = View.VISIBLE    }
}
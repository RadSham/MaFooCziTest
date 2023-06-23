package com.example.testmafooczi.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.testmafooczi.R
import com.example.testmafooczi.databinding.ActivityProfileBinding
import com.example.testmafooczi.fragment.EditProfileFragment
import com.example.testmafooczi.fragment.FragmentCloseInterface
import com.example.testmafooczi.retrofit.InitRetrofit
import com.example.testmafooczi.retrofit.MainApi
import com.example.testmafooczi.retrofit.ProfileUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate

class ProfileActivity : AppCompatActivity(), FragmentCloseInterface {

    private lateinit var binding: ActivityProfileBinding
    private var accessToken: String? = null
    private var refreshToken: String? = null
    private lateinit var mainApi: MainApi
    lateinit var profileUser: ProfileUser


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
        val inRet = InitRetrofit()
        mainApi = inRet.initRetrofitWithAccessToken(accessToken)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initProfile() = with(binding) {
        imProfileAvatar.setImageURI(profileUser.profile_data.avatar?.toUri())
        tvProfilePhone.text = profileUser.profile_data.phone
        tvProfileNickname.text = profileUser.profile_data.username
        tvProfileCity.text = profileUser.profile_data.city
        tvProfileBirthDate.text = profileUser.profile_data.birthday
        tvProfileZodiacSign.text = profileUser.profile_data.birthday?.let { checkZodiac(it) }
        tvProfileAbout.text = getString(R.string.name_message, profileUser.profile_data.name)
    }

    private fun initButton() {
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
        mBundle.putString("profileUser", Json.encodeToString(profileUser))
        mBundle.putString("accessToken", accessToken)
        mBundle.putString("refreshToken", refreshToken)

        mFragment.arguments = mBundle
        mFragmentTransaction.replace(R.id.flEditProfile, mFragment).addToBackStack(null).commit()
    }

    override fun onFragClose() {
        binding.clEditProfile.visibility = View.VISIBLE
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun checkZodiac(tDate: String): String {
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
            } catch (_: NullPointerException) {
                return "Null"
            }
        }
    }
}
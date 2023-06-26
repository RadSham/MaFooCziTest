package com.example.testmafooczi.activity

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
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
import com.example.testmafooczi.retrofit.RefreshToken
import com.example.testmafooczi.utils.ImageManager
import com.example.testmafooczi.viewmodel.ProfileUserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ProfileActivity : AppCompatActivity(), FragmentCloseInterface {

    private lateinit var binding: ActivityProfileBinding
    private var accessToken: String? = null
    private var refreshToken: String? = null
    private lateinit var mainApi: MainApi
    lateinit var profileUser: ProfileUser
    private val viewModel: ProfileUserViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
        getUser()
        initButton()
        initViewModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun initViewModel() {
        viewModel.mutableProfileUser.observe(this) { user ->
            profileUser = user
            initProfile()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getUser() {
        CoroutineScope(Dispatchers.IO).launch {
            if (mainApi.getCurrentUser().code() == 401) {
                val inRet = InitRetrofit()
                mainApi = inRet.initRetrofit()
                val updatedCredentials = refreshToken?.let { RefreshToken(it) }
                    ?.let { mainApi.refreshToken(it) }
                accessToken = updatedCredentials?.body()?.access_token
                refreshToken = updatedCredentials?.body()?.refresh_token
                mainApi = inRet.initRetrofitWithAccessToken(accessToken)
            }
            if (mainApi.getCurrentUser().isSuccessful)
                profileUser = mainApi.getCurrentUser().body()!!

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
        println("$accessToken $refreshToken")
    }

    private fun initRetrofit() {
        val inRet = InitRetrofit()
        mainApi = inRet.initRetrofitWithAccessToken(accessToken)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initProfile() = with(binding) {
        if (profileUser.profile_data.avatar.isNullOrEmpty()) {
            imProfileAvatar.setImageResource(R.drawable.ic_person)
        } else {
            setAvatar(profileUser.profile_data.avatar!!.toUri())
        }
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

    private fun setAvatar(uri: Uri) {
        CoroutineScope(Dispatchers.Main).launch {
            val bitmapList = ImageManager.imageResize(this@ProfileActivity, arrayListOf(uri))
            binding.imProfileAvatar.setImageBitmap(bitmapList[0])
        }
    }

    override fun onFragClose() {
        binding.clEditProfile.visibility = View.VISIBLE
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun checkZodiac(tDate: String): String {
            try {
                val date = LocalDate.parse(tDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val month = date.month
                val day = date.dayOfMonth
                var sign = ""
                when (month.name) {
                    "JANUARY" -> {
                        sign = if (day < 20)
                            "Capricorn"
                        else
                            "Aquarius"
                    }
                    "FEBRUARY" -> {
                        sign = if (day < 19)
                            "Aquarius"
                        else
                            "Pisces"
                    }
                    "MARCH" -> {
                        sign = if (day < 21)
                            "Pisces"
                        else
                            "Aries"
                    }
                    "APRIL" -> {
                        sign = if (day < 20)
                            "Aries"
                        else
                            "Taurus"
                    }
                    "MAY" -> {
                        sign = if (day < 21)
                            "Taurus"
                        else
                            "Gemini"
                    }
                    "JUNE" -> {
                        sign = if (day < 21)
                            "Gemini"
                        else
                            "Cancer"
                    }
                    "JULY" -> {
                        sign = if (day < 23)
                            "Cancer"
                        else
                            "Leo"
                    }
                    "AUGUST" -> {
                        sign = if (day < 23)
                            "Leo"
                        else
                            "Virgo"
                    }
                    "SEPTEMBER" -> {
                        sign = if (day < 23)
                            "Virgo"
                        else
                            "Libra"
                    }
                    "OCTOBER" -> {
                        sign = if (day < 23)
                            "Libra"
                        else
                            "Scorpio"
                    }
                    "NOVEMBER" -> {
                        sign = if (day < 22)
                            "scorpio"
                        else
                            "Sagittarius"
                    }
                    "DECEMBER" -> {
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
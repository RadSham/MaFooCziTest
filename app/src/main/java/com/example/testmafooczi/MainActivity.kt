package com.example.testmafooczi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testmafooczi.activity.ProfileActivity
import com.example.testmafooczi.databinding.ActivityMainBinding
import com.example.testmafooczi.fragment.FragmentCloseInterface
import com.example.testmafooczi.fragment.RegistrationFragment
import com.example.testmafooczi.retrofit.InitRetrofit
import com.example.testmafooczi.retrofit.LoginInformation
import com.example.testmafooczi.retrofit.MainApi
import com.example.testmafooczi.retrofit.Phone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), FragmentCloseInterface {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainApi: MainApi
    private var responsePhoneBoolean = false
    private lateinit var authPhone: Phone
    private var accessToken: String? = null
    private var refreshToken: String? = null

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
                Phone("+${binding.countryPicker.selectedCountryCode}${binding.etPhoneAuth.text}")
            CoroutineScope(Dispatchers.IO).launch {
                val responsePhone = mainApi.sendAuthPhone(authPhone)
                responsePhoneBoolean = responsePhone.isSuccessful
                runOnUiThread {
                    binding.etAuthCode.visibility = View.VISIBLE
                    binding.buttonSendCode.visibility = View.VISIBLE
                    binding.buttonSendPhone.visibility = View.GONE
                    Toast.makeText(
                        this@MainActivity,
                        responsePhone.isSuccessful.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.buttonSendCode.setOnClickListener {
            val loginInfo =
                LoginInformation(authPhone.phone, binding.etAuthCode.text.toString())
            CoroutineScope(Dispatchers.IO).launch {
                val responsePhoneCode = mainApi.sendAuthCode(loginInfo)
                if (responsePhoneCode.body()?.is_user_exists == true) {
                    accessToken = responsePhoneCode.body()?.access_token
                    refreshToken = responsePhoneCode.body()?.refresh_token
                    //go to Profile
                    val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                    intent.putExtra("accessToken", accessToken)
                    intent.putExtra("refreshToken", refreshToken)
                    startActivity(intent)
                } else {
                    runOnUiThread {
                        toRegFragment(authPhone)
                    }

                }
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

    private fun toRegFragment(authPhone: Phone) {
        // Declaring fragment manager from making data
        // transactions using the custom fragment
        val mFragmentManager = supportFragmentManager
        val mFragmentTransaction = mFragmentManager.beginTransaction()
        val mFragment = RegistrationFragment(this)

        //hide buttons and Edit Views
        binding.clMain.visibility = View.GONE

        // On button click, a bundle is initialized and the
        // text from the EditText is passed in the custom
        // fragment using this bundle
        val mBundle = Bundle()
        mBundle.putString("mText", authPhone.phone)
        mFragment.arguments = mBundle
        mFragmentTransaction.replace(R.id.flMain, mFragment).addToBackStack(null).commit()
    }

    private fun initRetrofit() {
        val inRet = InitRetrofit()
        mainApi = inRet.initRetrofit()
    }

    companion object {
        const val MAIN_URL = "https://plannerok.ru/"
    }

    override fun onFragClose() {
        binding.clMain.visibility = View.VISIBLE
    }
}

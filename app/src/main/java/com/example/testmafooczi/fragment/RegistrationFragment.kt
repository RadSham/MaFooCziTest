package com.example.testmafooczi.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.testmafooczi.activity.ProfileActivity
import com.example.testmafooczi.databinding.FragmentRegistrationBinding
import com.example.testmafooczi.retrofit.InitRetrofit
import com.example.testmafooczi.retrofit.MainApi
import com.example.testmafooczi.retrofit.RegisterUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RegistrationFragment(private val fragCloseInterface: FragmentCloseInterface) : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var mainApi: MainApi
    private var accessToken: String? = null
    private var refreshToken: String? = null

    var message: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgumentsFromActivity()
    }

    private fun getArgumentsFromActivity() {
        val bundle = arguments
        message = bundle!!.getString("mText")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvPhone.text = message
    }

    override fun onDestroy() {
        super.onDestroy()
        fragCloseInterface.onFragClose()
    }

    private fun init() {
        initButtons()
        initRetrofit()
    }

    private fun initButtons() {
        binding.btRegister.setOnClickListener {
            if (checkUsername(binding.edUsername.text.toString())) {
                val userToReg = RegisterUser(
                    binding.tvPhone.text.toString(),
                    binding.edName.text.toString(),
                    binding.edUsername.text.toString()
                )
                CoroutineScope(Dispatchers.IO).launch {
                    val responseRegisteredUser = mainApi.registerUser(userToReg)
                    if (responseRegisteredUser.isSuccessful) {
                        accessToken = responseRegisteredUser.body()?.access_token
                        refreshToken = responseRegisteredUser.body()?.refresh_token
                        //go to Profile
                        val intent = Intent(activity, ProfileActivity::class.java)
                        intent.putExtra("accessToken", accessToken)
                        intent.putExtra("refreshToken", refreshToken)
                        startActivity(intent)
                    }
                }
            } else {
                Toast.makeText(
                    context, "Username должно быть длиной в 5 символов и более, \n" +
                            "и может содержать только следующие символы:\n" +
                            "Заглавные латинские буквы: от A до Z (26 символов)\n" +
                            "Строчные латинские буквы: от a до z (26 символов)\n" +
                            "Цифры от 0 до 9 (10 символов)\n" +
                            "Символы: -_ (2 символа)", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initRetrofit() {
        val inRet = InitRetrofit()
        mainApi = inRet.initRetrofit()
    }

    private fun checkUsername(input: String): Boolean {
        if (input.matches(Regex("[A-Za-z0-9_-]+")) && input.length >= 5)
            return true
        return false
    }


}
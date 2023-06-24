package com.example.testmafooczi.fragment

import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.testmafooczi.R
import com.example.testmafooczi.activity.ProfileActivity
import com.example.testmafooczi.activity.ProfileActivity.Companion.checkZodiac
import com.example.testmafooczi.databinding.FragmentEditProfileBinding
import com.example.testmafooczi.retrofit.*
import com.example.testmafooczi.utils.ImageManager
import com.example.testmafooczi.viewmodel.ProfileUserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class EditProfileFragment(private val fragCloseInterface: FragmentCloseInterface) : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var mainApi: MainApi
    private var accessToken: String? = null
    private var refreshToken: String? = null
    private lateinit var profileUser: ProfileUser
    var avatarPath: String = ""
    var message: String? = null
    private val viewModel: ProfileUserViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgumentsFromActivity()
    }

    private fun getArgumentsFromActivity() {
        val bundle = arguments
        message = bundle!!.getString("profileUser")
        profileUser = message?.let { Json.decodeFromString<ProfileUser>(it) }!!
        accessToken = bundle.getString("accessToken")
        println("accessToken $accessToken")
        refreshToken = bundle.getString("refreshToken")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (profileUser.profile_data.avatar.isNullOrEmpty() || profileUser.profile_data.avatar == "null") {
            binding.imEditProfileAvatar.setImageResource(R.drawable.ic_person_add)
        } else {
            binding.imEditProfileAvatar.setImageURI(profileUser.profile_data.avatar?.toUri())
        }
        binding.tvEditProfilePhone.text = profileUser.profile_data.phone
        binding.tvEditProfileNickname.text = profileUser.profile_data.username

        if (profileUser.profile_data.city != "null") binding.edEditProfileCity.setText(profileUser.profile_data.city)
        else binding.edEditProfileCity.setText(R.string.city)

        if (profileUser.profile_data.birthday != "null") binding.edProfileBirthDate.setText(
            profileUser.profile_data.birthday
        )
        else binding.edProfileBirthDate.setText(R.string.firstdate)

        if (profileUser.profile_data.birthday != "null") binding.edProfileZodiacSign.setText(
            profileUser.profile_data.birthday?.let { checkZodiac(it) })
        else (binding.edProfileZodiacSign.setText(R.string.zodiac_sign))

        binding.edProfileAbout.setText(
            getString(
                R.string.name_message,
                profileUser.profile_data.name
            )
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        fragCloseInterface.onFragClose()
    }


    private fun init() {
        initRetrofit()
        initButtons()
    }

    private fun initButtons() = with(binding) {
        imEditProfileAvatar.setOnClickListener {
            launchPickerSingleMode()
        }
        btSaveEditProfile.setOnClickListener {
            val updatedUser = UpdateUser(
                profileUser.profile_data.name.toString(),
                tvEditProfileNickname.text.toString(),
                edProfileBirthDate.text.toString(),
                edEditProfileCity.text.toString(),
                profileUser.profile_data.vk.toString(),
                profileUser.profile_data.instagram.toString(),
                profileUser.profile_data.status.toString(),
                Avatar(avatarPath, "base24")
            )
            profileUser.profile_data.avatar = avatarPath
            profileUser.profile_data.birthday = edProfileBirthDate.text.toString()
            profileUser.profile_data.city = edEditProfileCity.text.toString()
            viewModel.setProfileUser(profileUser)
            CoroutineScope(Dispatchers.IO).launch {
                val avatars = mainApi.updateUser(updatedUser)
                if (avatars.isSuccessful) {
                    profileUser.profile_data.avatars = avatars.body()
                    activity?.runOnUiThread{
                        viewModel.setProfileUser(profileUser)
                        fragCloseInterface.onFragClose()
                    }
                    activity?.supportFragmentManager?.beginTransaction()?.remove(this@EditProfileFragment)
                        ?.commit();
                }
            }
        }
    }

    private fun initRetrofit() {
        val inRet = InitRetrofit()
        mainApi = inRet.initRetrofitWithAccessToken(accessToken)
    }

    private fun launchPickerSingleMode() {
        val m = ActivityResultContracts.PickVisualMedia.ImageOnly
        try {
            startForSingleModeResult.launch(
                PickVisualMediaRequest.Builder().setMediaType(m).build()
            )
        } catch (ex: ActivityNotFoundException) {
            showToast(ex.localizedMessage ?: "error")
        }
    }

    private val startForSingleModeResult =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { currentUri ->
            if (currentUri != null) {
                avatarPath = currentUri.path.toString()
                setAvatar(currentUri)
            } else {
                showToast("No media selected")
            }
        }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun setAvatar(uri: Uri) {
        CoroutineScope(Dispatchers.Main).launch {
            val bitmapList = ImageManager.imageResize(activity as ProfileActivity, arrayListOf(uri))
            binding.imEditProfileAvatar.setImageBitmap(bitmapList[0])
        }
    }
}
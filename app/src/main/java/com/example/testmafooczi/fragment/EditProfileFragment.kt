package com.example.testmafooczi.fragment

import android.app.DatePickerDialog
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
import java.text.SimpleDateFormat
import java.util.*

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
        refreshToken = bundle.getString("refreshToken")
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
            setAvatar(profileUser.profile_data.avatar!!.toUri())
        }
        binding.tvEditProfilePhone.text = profileUser.profile_data.phone
        binding.tvEditProfileNickname.text = profileUser.profile_data.username

        if (profileUser.profile_data.city != "null") binding.edEditProfileCity.setText(profileUser.profile_data.city)
        else binding.edEditProfileCity.setText(R.string.city)

        if (profileUser.profile_data.birthday != "null") binding.tvEditProfileBirthDate.text =
            profileUser.profile_data.birthday
        else binding.tvEditProfileBirthDate.text = getString(R.string.firstdate)

        if (profileUser.profile_data.birthday != "null") binding.tvEditProfileZodiacSign.text =
            profileUser.profile_data.birthday?.let { checkZodiac(it) }
        else binding.tvEditProfileZodiacSign.text = checkZodiac(getString(R.string.firstdate))

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun init() {
        initRetrofit()
        initButtons()
        initDatePicker()
    }

    private fun initButtons() = with(binding) {
        imEditProfileAvatar.setOnClickListener {
            launchPickerSingleMode()
        }
        btSaveEditProfile.setOnClickListener {
            val updatedUser = UpdateUser(
                profileUser.profile_data.name.toString(),
                tvEditProfileNickname.text.toString(),
                tvEditProfileBirthDate.text.toString(),
                edEditProfileCity.text.toString(),
                profileUser.profile_data.vk.toString(),
                profileUser.profile_data.instagram.toString(),
                profileUser.profile_data.status.toString(),
                Avatar(avatarPath, "base24")
            )
            if (avatarPath != "" || avatarPath.isNotEmpty()) {
                profileUser.profile_data.avatar = avatarPath
            }
            profileUser.profile_data.birthday = tvEditProfileBirthDate.text.toString()
            profileUser.profile_data.city = edEditProfileCity.text.toString()
            viewModel.setProfileUser(profileUser)
            CoroutineScope(Dispatchers.IO).launch {
                if (mainApi.updateUser(updatedUser).code() == 401) {
                    val inRet = InitRetrofit()
                    mainApi = inRet.initRetrofit()
                    val updatedCredentials = refreshToken?.let { RefreshToken(it) }
                        ?.let { mainApi.refreshToken(it) }
                    accessToken = updatedCredentials?.body()?.access_token
                    refreshToken = updatedCredentials?.body()?.refresh_token
                    mainApi = inRet.initRetrofitWithAccessToken(accessToken)
                }
                if (mainApi.updateUser(updatedUser).isSuccessful) {
                    val avatars = mainApi.updateUser(updatedUser)
                    profileUser.profile_data.avatars = avatars.body()
                    activity?.runOnUiThread {
                        viewModel.setProfileUser(profileUser)
                        fragCloseInterface.onFragClose()
                    }
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.remove(this@EditProfileFragment)
                        ?.commit()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initDatePicker() {
        binding.tvEditProfileBirthDate.text =
            SimpleDateFormat("dd.MM.yyyy").format(System.currentTimeMillis())

        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "yyyy-MM-dd"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.tvEditProfileBirthDate.text = sdf.format(cal.time)

                binding.tvEditProfileZodiacSign.text =
                    checkZodiac(binding.tvEditProfileBirthDate.text.toString())
            }

        binding.tvEditProfileBirthDate.setOnClickListener {
            DatePickerDialog(
                context!!, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
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
                println(currentUri)
                avatarPath = currentUri.toString()
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
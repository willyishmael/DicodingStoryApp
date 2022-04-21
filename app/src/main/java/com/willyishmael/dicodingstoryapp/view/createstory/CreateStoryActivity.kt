package com.willyishmael.dicodingstoryapp.view.createstory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.willyishmael.dicodingstoryapp.data.local.UserPreference
import com.willyishmael.dicodingstoryapp.databinding.ActivityCreateStoryBinding
import com.willyishmael.dicodingstoryapp.utils.createFile
import com.willyishmael.dicodingstoryapp.utils.uriToFile
import com.willyishmael.dicodingstoryapp.view.ViewModelFactory
import com.willyishmael.dicodingstoryapp.view.login.LoginActivity
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "current_user")

class CreateStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateStoryBinding
    private lateinit var createStoryViewModel: CreateStoryViewModel
    private lateinit var currentPhotoPath: String

    private var getFile: File? = null
    private var userToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        requestPermission()
        setupViewModel()
        setupLiveData()
        setupButtons()
    }

    private fun setupViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        createStoryViewModel = ViewModelProvider(this, ViewModelFactory(pref))[CreateStoryViewModel::class.java]
    }

    private fun setupLiveData() {
        createStoryViewModel.getLoginState().observe(this) { loginState ->
            if (!loginState) moveToLoginActivity()
        }

        createStoryViewModel.getUserToken().observe(this) { token ->
            userToken = token
        }
    }

    private fun requestPermission() {
        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun setupButtons() {
        binding.apply {
            btnCamera.setOnClickListener { startCamera() }
            btnGallery.setOnClickListener { openGallery() }
            btnUpload.setOnClickListener { uploadStory() }
        }
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createFile(application).also { file ->
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.willyishmael.dicodingstoryapp",
                file
            )
            currentPhotoPath = file.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadStory() {

        if (getFile != null) {
            val file = getFile as File
            val description = binding.etCaption.text.toString()

            if (userToken.isNotEmpty()) {
                createStoryViewModel.uploadStory(userToken, file, description)
            }
        } else {
            Toast.makeText(
                this,
                "Take picture or choose image from your Gallery first",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val mFile = File(currentPhotoPath)
            getFile = mFile
            val mResult = BitmapFactory.decodeFile(mFile.path)
            binding.ivPreviewImage.setImageBitmap(mResult)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage: Uri = result.data?.data as Uri
            val mFile = uriToFile(selectedImage, this)
            getFile = mFile
            binding.ivPreviewImage.setImageURI(selectedImage)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun moveToLoginActivity() {
        Intent(this, LoginActivity::class.java).apply {
            startActivity(this)
        }
        finish()
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}
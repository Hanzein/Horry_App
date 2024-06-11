package com.farhanadi.horryapp.user_ui_page.add

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.exifinterface.media.ExifInterface
import com.farhanadi.horryapp.R
import com.farhanadi.horryapp.ViewModelFactory
import com.farhanadi.horryapp.databinding.ActivityAddStoryBinding
import com.farhanadi.horryapp.preferences_manager.LanguageManager
import com.farhanadi.horryapp.preferences_manager.UserManager
import com.farhanadi.horryapp.user_data.ResultResource
import com.farhanadi.horryapp.user_ui_page.camera.CameraActivity
import com.farhanadi.horryapp.user_ui_page.home.MainActivity
import com.farhanadi.horryapp.user_ui_page.ui.home.HomeFragment
import com.farhanadi.horryapp.user_ui_page.utils.hide
import com.farhanadi.horryapp.user_ui_page.utils.reduceFileImage
import com.farhanadi.horryapp.user_ui_page.utils.rotateBitmap
import com.farhanadi.horryapp.user_ui_page.utils.show
import com.farhanadi.horryapp.user_ui_page.utils.showToast
import com.farhanadi.horryapp.user_ui_page.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.Locale

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var preferences: UserManager
    private lateinit var factory: ViewModelFactory
    private val viewModel: AddViewModel by viewModels {
        factory
    }
    private var getFile: File? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addLoading.hide()

        setupLanguage()
        setupView()
        setupAction()
        setupProperty()

        if (!permissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun setupLanguage() {
        val language = LanguageManager.getLanguage(this)
        val config = resources.configuration
        config.setLocale(Locale(language))
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.btnbackAdd.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.btnCamera.setOnClickListener {
            startCameraX()
        }
        binding.btnGaleri.setOnClickListener {
            startGallery()
        }
        binding.btnUpload.setOnClickListener {
            validatedata()
        }
        binding.switch1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                requestLocationPermission()
            }
        }
    }

    private fun setupProperty() {
        factory = ViewModelFactory.getInstance(this)
        preferences = UserManager(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Example: Adding rotation animation to a view
        val rotateAnimator = ObjectAnimator.ofFloat(binding.addImage, View.ROTATION, 0f, 360f)
        rotateAnimator.duration = 1000

        // Example: Adding scaling animation to a view
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5f, 1f)
        val fadeIn = ObjectAnimator.ofPropertyValuesHolder(binding.addImage, scaleX, scaleY)
        fadeIn.duration = 1000
    }

    private fun postImage(token: String?, location: Location?) {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val userToken = "Bearer $token"
            val description = binding.txtDesc.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            val lat = location?.latitude ?: 0.0
            val lon = location?.longitude ?: 0.0
            val multiForm = "application/json"

            viewModel.postImage(
                imageMultipart,
                description,
                lat,
                lon,
                userToken,
                multiForm
            ).observe(this) { response ->
                when (response) {
                    is ResultResource.Loading -> {
                        binding.addLoading.show()
                    }
                    is ResultResource.Success -> {
                        binding.addLoading.hide()
                        showSuccessDialog()
                    }
                    is ResultResource.Error -> {
                        binding.addLoading.show()
                        showToast(response.error)
                    }
                }
            }
        } else {
            showErrorDialog()
            binding.addLoading.hide()
        }
    }

    private fun startGallery() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImage: Uri = result.data?.data as Uri
            val mFile = uriToFile(selectedImage, this)
            getFile = mFile
            binding.addImage.setImageURI(selectedImage)
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == CAMERA_X_RESULT) {
            val mFile = result.data?.getSerializableExtra("picture") as File
            val isBackCamera = result.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = mFile
            val resultBitmap = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            binding.addImage.setImageBitmap(resultBitmap)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!permissionGranted()) {
                Toast.makeText(this, "Can't Get Permission", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    uploadData()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    uploadData()
                }
                else -> {
                    // No location access granted.
                    Snackbar.make(
                        binding.root,
                        getString(R.string.location_permission_denied),
                        Snackbar.LENGTH_SHORT
                    )
                        .setActionTextColor(getColor(R.color.white))
                        .setAction(getString(R.string.location_permission_denied_action)) {
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also { intent ->
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }.show()
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun validatedata() {
        val name = binding.txtDesc.text.toString()

        when {
            name.isEmpty() -> {
                binding.txtDesc.error = "Input this field"
                binding.txtDesc.requestFocus()
            }
            else -> {
                uploadData()
            }
        }
    }

    private fun uploadData() {
        val shareLocation = binding.switch1.isChecked

        if (shareLocation) {
            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            ) {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val token = preferences.getToken()
                        if (!token.isNullOrEmpty()) {
                            postImage(token, location)
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Location is not found. Try Again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        } else {
            val token = preferences.getToken()
            if (!token.isNullOrEmpty()) {
                postImage(token, null)
            }
        }
    }

    private fun permissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        val fineLocationPermissionGranted = checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocationPermissionGranted = checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

        if (!fineLocationPermissionGranted || !coarseLocationPermissionGranted) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.add_success_upload)
        builder.setMessage(R.string.add_thankyou_fsharing)
        builder.setPositiveButton("OK") { _, _ ->
            startActivity(Intent(this, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                finish()
            })
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.add_failed_upload)
        builder.setMessage(R.string.add_check_again)
        builder.setPositiveButton("OK") { _, _ ->
        }
        val dialog = builder.create()
        dialog.show()
    }
    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 100
    }
}

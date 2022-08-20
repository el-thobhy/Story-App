package com.elthobhy.storyapp.ui.posting

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.elthobhy.storyapp.R
import com.elthobhy.storyapp.core.utils.*
import com.elthobhy.storyapp.core.utils.vo.Status
import com.elthobhy.storyapp.databinding.ActivityPostStoryBinding
import com.elthobhy.storyapp.ui.camera.CameraActivity
import com.elthobhy.storyapp.ui.main.MainActivity.Companion.INSERT_RESULT
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.android.ext.android.inject
import java.io.File

class PostStoryActivity : AppCompatActivity() {
    private var getFile: File? = null
    private lateinit var binding: ActivityPostStoryBinding
    private val postingViewModel by inject<PostingViewModel>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var dialog: AlertDialog
    private lateinit var dialogLoading: AlertDialog
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        dialog = showDialogError(this)
        dialogLoading = showDialogLoading(this)
        setPermission()
        setUpActionBar()
        onClick()
    }

    private fun onClick() {
        binding.apply {
            buttonCameraAdd.setOnClickListener {
                startCameraX()
            }
            buttonGalleryAdd.setOnClickListener {
                startGallery()
            }
            buttonUpload.setOnClickListener {
                uploadImage()
            }
            icSearchLocation.setOnClickListener {
                getMyLocation()
            }
        }
    }

    private fun checkPermission(permission: String): Boolean{
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if(location != null){
                        setTextLocation(location)
                    }else{
                        dialog = showDialogError(this,getString(R.string.location_error))
                        dialog.show()
                    }
                }
        }else{
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){permission ->
            when{
                permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLocation()
                }
                permission[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLocation()
                }
            }
        }

    private fun setTextLocation(location: Location) {
        val latLng = "${location.latitude}, ${location.longitude}"
        this.location = location
        binding.etLocation.setText(latLng)
    }

    private fun uploadImage() {
        if (getFile != null) {
            dialogLoading.show()
            val file = compressImage(getFile as File)

            val description =
                binding.editDescription.text.toString()
                    .toRequestBody("text/plain".toMediaType())

            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part =
                MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
            val lat = if(location != null) location?.latitude.toString().toRequestBody("text/plain".toMediaType()) else null
            val lon = if(location != null) location?.longitude.toString().toRequestBody("text/plain".toMediaType()) else null

            lifecycleScope.launch {
                postingViewModel.postingStory(
                    imageMultipart = imageMultipart,
                    description = description,
                    lat = lat,
                    lon = lon
                ).observe(this@PostStoryActivity) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            Intent().apply {
                                setResult(INSERT_RESULT, this)
                                finish()
                            }
                        }
                        Status.LOADING-> dialogLoading.show()
                        Status.ERROR -> {
                            dialogLoading.dismiss()
                            dialog = showDialogError(this@PostStoryActivity, it.message)
                            dialog.show()
                            Log.e("post", "uploadImage: ")
                        }
                    }
                }
            }
        } else {
            dialog = showDialogError(this, getString(R.string.empty_picture))
            dialog.show()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                dialog = showDialogError(this, getString(R.string.permission_denied))
                dialog.show()
                finish()
            }
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun setPermission() {
        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                Constants.REQUIRED_PERMISSIONS,
                Constants.REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun allPermissionGranted() = Constants.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Post Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> true
        }

    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Constants.CAMERA_X_RESULT) {
            val file = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            val result = rotateBitmap(
                BitmapFactory.decodeFile(file.path),
                isBackCamera
            )
            binding.postingImage.setImageBitmap(result)
            getFile = file
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImage: Uri = it.data?.data as Uri
            val file = uriToFile(selectedImage, this@PostStoryActivity)
            binding.postingImage.setImageURI(selectedImage)
            getFile = file
        }
    }

    override fun onStop() {
        super.onStop()
        dialog.dismiss()
        dialogLoading.dismiss()
    }
}